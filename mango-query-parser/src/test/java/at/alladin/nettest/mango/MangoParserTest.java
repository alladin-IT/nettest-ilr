package at.alladin.nettest.mango;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import at.alladin.nettest.mango.ast.expression.AndExpression;
import at.alladin.nettest.mango.ast.expression.ComparisonExpression;
import at.alladin.nettest.mango.ast.expression.ComparisonExpression.Operator;
import at.alladin.nettest.mango.ast.expression.Expression;
import at.alladin.nettest.mango.ast.expression.OrExpression;
import at.alladin.nettest.mango.ast.expression.SelectorExpression;

public class MangoParserTest {

	@Test
	public void testParsingEmptySelector() {
		final MangoQuery query = MangoParser.fromJsonString("{selector: {}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 0", 0, query.getSelector().getExpressionList().size());
	}

	@Test
	public void testParsingSimpleEqSelector() {
		final MangoQuery query = MangoParser.fromJsonString("{selector: {'key': 'value'}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		final Expression eqExpr = ((SelectorExpression)query.getSelector().getExpressionList().get(0)).getComparisonList().get(0);
		assertEquals("expression index 0 in selector is ComparisonExpression", ComparisonExpression.class, eqExpr.getClass());
		assertEquals("operator of ComparisonExpression is EQ", Operator.EQ, ((ComparisonExpression)eqExpr).getOperator());
	}

	@Test
	public void testParsingNestedJsonSimpleEqSelector() {
		final MangoQuery query = MangoParser.fromJsonString("{selector: {'key.sub': 'value'}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());

		final SelectorExpression firstExpr = (SelectorExpression) query.getSelector().getExpressionList().get(0);
		assertEquals("expression index 0 in first selector expression is also a SelectorExpression", SelectorExpression.class, 
				firstExpr.getComparisonList().get(0).getClass());
		
		final Expression eqExpr = ((SelectorExpression) firstExpr.getComparisonList().get(0)).getComparisonList().get(0);
		assertEquals("expression index 0 in second selector is ComparisonExpression", ComparisonExpression.class, eqExpr.getClass());
		assertEquals("operator of ComparisonExpression is EQ", Operator.EQ, ((ComparisonExpression)eqExpr).getOperator());
	}

	@Test
	public void testParsingAdvancedEqSelector() {
		final MangoQuery query = MangoParser.fromJsonString("{selector: {'key': {'$eq' : 'value'}}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
	}

	@Test
	public void testParsingAndEvaluatingMultipleEqSelectors() {
		final MangoQuery query = MangoParser.fromJsonString("{selector: {'key':{'$eq': 'value'}, 'key2': {'$eq': 'value2'}}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 2", 2, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		assertNotNull("expression index 1 != null", query.getSelector().getExpressionList().get(1));
		assertEquals("expression index 1 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(1).getClass());

		final JsonObject testObject = new Gson().fromJson("{key: 'value', key2: 'value2'}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
	}

	@Test
	public void testParsingAndEvaluatingNestedJsonWithStringEqSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {"
				+ "'key':{'sub':{'$eq': 'value'}},"
				+ "'key2':{'sub2':{'$eq': 'value2'}}}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 2", 2, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		assertNotNull("expression index 1 != null", query.getSelector().getExpressionList().get(1));
		assertEquals("expression index 1 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(1).getClass());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value', key2: 'value2'}", JsonObject.class);
		assertFalse("query doesn't match json", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: {sub: 'value'}, key2: {sub: 'value2'}}", JsonObject.class);
		assertFalse("query doesn't match json (because: key2.sub != key2.sub2)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: {sub: 'value'}, key2: {sub2: 'value2'}}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
	}

	@Test
	public void testParsingAndEvaluatingNestedJsonWithBooleanEqSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {"
				+ "'key':{'sub':{'$eq': false}},"
				+ "'key2':{'sub2':{'$eq': true}}}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 2", 2, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		assertNotNull("expression index 1 != null", query.getSelector().getExpressionList().get(1));
		assertEquals("expression index 1 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(1).getClass());
		
		JsonObject testObject = new Gson().fromJson("{key: 'false', key2: 'true'}", JsonObject.class);
		assertFalse("query doesn't match json (strings are not booleans)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: {sub: false}, key2: {sub: 'true'}}", JsonObject.class);
		assertFalse("query doesn't match json ('true' is a string)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: {sub: false}, key2: {sub2: true}}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
	}

	@Test
	public void testParsingAndEvaluatingJsonWithIntegerGtSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'key':{'$gt': 10}}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertFalse("query doesn't match json (value is string)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: '11'}", JsonObject.class);
		assertFalse("query doesn't match json ('11' is not a number)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10}", JsonObject.class);
		assertFalse("query doesn't match json (10 is not greater than 10)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 11}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
	}
	
	@Test
	public void testParsingAndEvaluatingJsonWithDoubleEqSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'key':{'$eq': 10.1}}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertFalse("query doesn't match json (value is string)", query.evaluate(testObject));

		//System.out.println(query);

		testObject = new Gson().fromJson("{key: '10.1'}", JsonObject.class);
		assertFalse("query doesn't match json ('10.1' is not a number)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10}", JsonObject.class);
		assertFalse("query doesn't match json (10 is not 10.1)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10.1}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
	}

	@Test
	public void testParsingAndEvaluatingNestedJsonWithDoubleEqSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'key.sub.moresub':{'$eq': 10.1}}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = SelectorExpression", SelectorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		//System.out.println(query);
		JsonObject testObject = new Gson().fromJson("{key: {sub: {moresub: 10.1}}}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: {submoresub: 10.1}}", JsonObject.class);
		assertFalse("query doesn't match json (sub element 'moresub' missing)", query.evaluate(testObject));
	}

	@Test
	public void testParsingAndEvaluatingJsonWithDoubleEqSelectorsUsingOr() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{'selector': "
				+ "{'$or': "
				+ "	[{'key':{'$eq': 10.1}},"
				+ "	{'key':{'$eq': 10.2}}]"
				+ "}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = OrExpression", OrExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertFalse("query doesn't match json (value is string)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: '10.1'}", JsonObject.class);
		assertFalse("query doesn't match json ('10.1' is not a number)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10}", JsonObject.class);
		assertFalse("query doesn't match json (10 is neither 10.1 nor 10.2)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10.1}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10.2}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
	}
	
	@Test
	public void testParsingAndEvaluatingJsonWithDoubleEqAndLtSelectorsUsingAnd() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{'selector': "
				+ "{'$and': "
				+ "	[{'key':{'$lt': 10.3}},"
				+ "	{'key':{'$eq': 10.2}}]"
				+ "}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = OrExpression", AndExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertFalse("query doesn't match json (value is string)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: '10.1'}", JsonObject.class);
		assertFalse("query doesn't match json ('10.1' is not a number)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10.1}", JsonObject.class);
		assertFalse("query doesn't match json (10.1 is lower than 10.3 but not equal to 10.2)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10.2}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
	}
}
