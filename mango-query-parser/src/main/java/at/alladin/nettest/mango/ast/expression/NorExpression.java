package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * <h2>Query operator <code>$nor</code></h2>
 * 
 * Matches if none of the selectors in the array match.
 * 
 * @author lb@alladin.at
 *
 */
public class NorExpression extends AbstractCombinedExpression {

	@Override
	public boolean evaluate(JsonElement json) {
		for (final Expression e : getExpressionList()) {
			if (e.evaluate(json)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "NorExpression [expressionList=" + expressionList + "]";
	}
}
