package at.alladin.nettest.mango;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import at.alladin.nettest.mango.ast.expression.AbstractCombinedExpression;
import at.alladin.nettest.mango.ast.expression.AllExpression;
import at.alladin.nettest.mango.ast.expression.AndExpression;
import at.alladin.nettest.mango.ast.expression.ComparisonExpression;
import at.alladin.nettest.mango.ast.expression.ComparisonExpression.Operator;
import at.alladin.nettest.mango.ast.expression.Expression;
import at.alladin.nettest.mango.ast.expression.InExpression;
import at.alladin.nettest.mango.ast.expression.NorExpression;
import at.alladin.nettest.mango.ast.expression.NotExpression;
import at.alladin.nettest.mango.ast.expression.OrExpression;
import at.alladin.nettest.mango.ast.expression.RegexExpression;
import at.alladin.nettest.mango.ast.expression.Selector;
import at.alladin.nettest.mango.ast.expression.SelectorExpression;
import at.alladin.nettest.mango.ast.expression.SizeExpression;
import at.alladin.nettest.mango.ast.expression.ValueExpression;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class MangoParser {

	final static class MangoParserException extends JsonParseException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MangoParserException(final String reason) {
			super(reason);
		}
	}
	
	final static JsonDeserializer<Selector> selectorDeserializer = new JsonDeserializer<Selector>() {

		@Override
		public Selector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			//System.out.println("parsing selector: " + json);
			final Selector selector = new Selector();
			for (final Entry<String, JsonElement> e : json.getAsJsonObject().entrySet()) {
				final JsonObject obj = new JsonObject();
				obj.add(e.getKey(), e.getValue());
				selector.getExpressionList().add((Expression)context.deserialize(obj, Expression.class));
			}
			
			return selector;
		}
	};

	final static JsonDeserializer<Expression> expressionDeserializer = new JsonDeserializer<Expression>() {

		private SelectorExpression nestKeys(final String[] keys) {
			//System.out.println(Arrays.toString(keys));
			final SelectorExpression expr = new SelectorExpression();
			if (keys.length > 1) {
				expr.setComparisonList(new ArrayList<Expression>());
				expr.getComparisonList().add(nestKeys(Arrays.copyOfRange(keys, 1, keys.length)));
			}

			expr.setKey(keys[0]);
			return expr;
		}
		
		private Expression parseExpression(final JsonDeserializationContext context, final JsonElement json) {
			return (Expression) (json.isJsonNull() ? new ValueExpression() : context.deserialize(json, Expression.class));
		}
		
		@Override
		public Expression deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			//System.out.println("parsing expr: " + json + ", isJsonObject: " + json.isJsonObject() + " isJsonPrimitive: " + json.isJsonPrimitive());
			if (json.isJsonObject()) {
				final Set<Entry<String, JsonElement>> entries = json.getAsJsonObject().entrySet();
				
				if (entries.size() != 1) {
					throw new MangoParserException("Only one key allowed in comparison expression: " + json);
				}
				
				final Entry<String, JsonElement> e = entries.iterator().next();
				if (!e.getKey().startsWith("$")) {
					final SelectorExpression expr = nestKeys(e.getKey().split("\\."));

					SelectorExpression lastExpr = expr;
					while (lastExpr.getComparisonList().size() > 0 &&
							(lastExpr.getComparisonList().get(0) instanceof SelectorExpression)) {
						lastExpr = (SelectorExpression) lastExpr.getComparisonList().get(0);
					}
					if (e.getValue().isJsonObject()) {
						for (Entry<String, JsonElement> jsonElement : e.getValue().getAsJsonObject().entrySet()) {
							final JsonObject object = new JsonObject();
							object.add(jsonElement.getKey(), jsonElement.getValue());
							lastExpr.getComparisonList().add((Expression) context.deserialize(object, Expression.class));
						}
					}
					else {
						//it's always an EQ comparison if we end up here
						final ComparisonExpression comparisonExpression = new ComparisonExpression();
						comparisonExpression.setOperator(Operator.EQ);
						comparisonExpression.setValue((ValueExpression) parseExpression(context, e.getValue()));
						lastExpr.getComparisonList().add(comparisonExpression);
					}
					return expr;
				}
				else if (e.getKey().startsWith("$")) {
					if ("$or".equals(e.getKey()) || "$and".equals(e.getKey()) || "$nor".equals(e.getKey()) ||
							"$in".equals(e.getKey()) || "$nin".equals(e.getKey()) || "$all".equals(e.getKey())) {
						if (!e.getValue().isJsonArray()) {
							throw new MangoParserException(e.getKey() + " must contain an array: " + e);
						}

						if (e.getValue().getAsJsonArray().size() == 0) {
							throw new MangoParserException(e.getKey() + " array must contain at least one element: " + e);
						}
						
						final Iterator<JsonElement> it = e.getValue().getAsJsonArray().iterator();
						final List<Expression> exprList = new ArrayList<>();
						while (it.hasNext()) {
							exprList.add((Expression) context.deserialize(it.next(), Expression.class));
						}
						
						final AbstractCombinedExpression expr;
						switch (e.getKey()) {
							case "$or":
								expr = new OrExpression();
								break;
							case "$nor":
								expr = new NorExpression();
								break;
							case "$in":
								expr = new InExpression(true);
								break;
							case "$nin":
								expr = new InExpression(false);
								break;
							case "$all":
								expr = new AllExpression();
								break;
							default:
								expr = new AndExpression();
						};
						
						expr.setExpressionList(exprList);
						return expr;
					}
					else if ("$regex".equals(e.getKey())) {
						final Expression expr = new RegexExpression(e.getValue().getAsString());
						return expr;
					}
					else if ("$size".equals(e.getKey())) {
						final Expression expr = new SizeExpression(e.getValue().getAsLong());
						return expr;
					}
					else if ("$not".equals(e.getKey())) {
						final NotExpression expr = new NotExpression();
						expr.setExpression(parseExpression(context, e.getValue()));
						return expr;
					}
					else {
						final ComparisonExpression expr = new ComparisonExpression();
						final Operator op = context.deserialize(new JsonPrimitive(e.getKey()), Operator.class);
						final ValueExpression ex = (ValueExpression) parseExpression(context, e.getValue());
						expr.setOperator(op);
						expr.setValue(ex);
						return expr;
					}
				}
			}
			else if (json.isJsonPrimitive()) {
				if (json.getAsJsonPrimitive().isString()) {
					return new ValueExpression(json.getAsString());
				}
				else if (json.getAsJsonPrimitive().isNumber()) {
					return new ValueExpression(json.getAsNumber().doubleValue());
				}
				else if (json.getAsJsonPrimitive().isBoolean()) {
					return new ValueExpression(json.getAsBoolean());
				}
			}
			
			return null;
		}
	};
	
	final static JsonDeserializer<ComparisonExpression.Operator> operatorDeserializer = new JsonDeserializer<ComparisonExpression.Operator>() {

		@Override
		public ComparisonExpression.Operator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return ComparisonExpression.Operator.fromString(json.getAsString());
		}
	};
	
	final static Gson gson = new GsonBuilder()
			.registerTypeAdapter(Expression.class, expressionDeserializer)
			.registerTypeAdapter(Selector.class, selectorDeserializer)
			.registerTypeAdapter(ComparisonExpression.Operator.class, operatorDeserializer)
			.serializeNulls()
			.create();
	
	public static MangoQuery fromJsonString(final String json) {
		return gson.fromJson(json, MangoQuery.class);
	}
	
	public static MangoQuery fromJsonElement(final JsonElement json) {
		return gson.fromJson(json, MangoQuery.class);
	}
}
