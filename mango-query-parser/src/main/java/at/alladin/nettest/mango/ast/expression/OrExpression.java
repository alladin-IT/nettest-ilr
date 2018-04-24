package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * <h2>Query operator <code>$or</code></h2>
 * Matches if any of the selectors in the array match.
 * 
 * @author lb@alladin.at
 *
 */
public class OrExpression extends AbstractCombinedExpression {

	@Override
	public boolean evaluate(JsonElement json) {
		for (final Expression e : getExpressionList()) {
			if (e.evaluate(json)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "OrExpression [expressionList=" + expressionList + "]";
	}
}
