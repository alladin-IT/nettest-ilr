package at.alladin.nettest.mango.ast.expression;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class SelectorExpression implements Expression {

	private String key;
	
	private List<Expression> comparisonList = new ArrayList<>();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Expression> getComparisonList() {
		return comparisonList;
	}

	public void setComparisonList(List<Expression> comparisonList) {
		this.comparisonList = comparisonList;
	}

	@Override
	public String toString() {
		return "SelectorExpression [key=" + key + ", comparisonList=" + comparisonList + "]";
	}

	@Override
	public boolean evaluate(JsonElement json) {
		//System.out.println(json + ", isJsonObject: " + json.isJsonObject());
		if (json == null || !json.isJsonObject()) {
			return false;
		}
		
		boolean result = false;
		final JsonElement sub = json.getAsJsonObject().get(key);
		
		//System.out.println(key + " = " + sub);
		for (final Expression e : comparisonList) {
			result |= e.evaluate(sub == null ? JsonNull.INSTANCE : sub);
		}
		
		return result;
	}
}
