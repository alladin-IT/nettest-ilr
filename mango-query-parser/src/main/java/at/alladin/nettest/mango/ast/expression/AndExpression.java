package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * <h2>Query operator <code>$and</code></h2>
 * 
 * Matches if all the selectors in the array match.
 * 
 * @author lb@alladin.at
 *
 */
public class AndExpression extends AbstractCombinedExpression {

	@Override
	public boolean evaluate(JsonElement json) {
		for (final Expression e : getExpressionList()) {
			if (!(boolean)e.evaluate(json)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "AndExpression [expressionList=" + expressionList + "]";
	}
}
