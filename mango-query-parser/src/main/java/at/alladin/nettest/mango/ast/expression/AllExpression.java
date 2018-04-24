package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * <h2>Query operator <code>$all</code></h2>
 * Matches an array value if it contains all the elements of the argument array.
 * Non-array fields cannot match this condition.
 * 
 * @author lb@alladin.at
 *
 */
public class AllExpression extends AbstractCombinedExpression {
	
	@Override
	public boolean evaluate(JsonElement json) {
		if (!json.isJsonArray()) {
			return false;
		}
		
		if (json.getAsJsonArray().size() < getExpressionList().size()) {
			return false;
		}

		for (final Expression v : getExpressionList()) {
			boolean found = false;
			for (JsonElement e : json.getAsJsonArray()) {
				//System.out.println("compare " + e + " to " + v);
				if (ComparisonExpression.Operator.EQ.compare(e, ((ValueExpression) v).getValue())) {
					//System.out.println("found");
					found = true; 
					break;
				}
			}
			
			if (!found) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "AllExpression [expressionList=" + expressionList + "]";
	}
}
