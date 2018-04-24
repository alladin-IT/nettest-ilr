package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * <h2>Query operators: <code>$in</code>, <code>$nin</code></h2>
 * 
 * <p><code>$in</code>: Matches if field value exists in array.</p>
 * <p><code>$nin</code>: Matches if field value does not exist in array.</p>
 * Matches if field value exists in array

 * @author lb@alladin.at
 *
 */
public class InExpression extends AbstractCombinedExpression {
	
	private boolean isPositive = true;
	
	public InExpression(final boolean isPositive) {
		this.isPositive = isPositive;
	}
	
	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

	@Override
	public boolean evaluate(JsonElement json) {
		if (!json.isJsonPrimitive() && !json.isJsonNull()) {
			return false;
		}

		for (final Expression v : getExpressionList()) {
			if (ComparisonExpression.Operator.EQ.compare(json, ((ValueExpression) v).getValue())) {
				return isPositive;
			}
		}
		
		return !isPositive;
	}

	@Override
	public String toString() {
		return "InExpression [isPositive=" + isPositive + ", expressionList=" + expressionList + "]";
	}
}
