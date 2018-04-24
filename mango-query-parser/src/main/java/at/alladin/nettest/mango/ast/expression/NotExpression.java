package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * <h2>Query operator <code>$not</code></h2>
 * 
 * Matches if the given selector does not match.
 * 
 * @author lb@alladin.at
 *
 */
public class NotExpression implements Expression {
	
	private Expression expression;

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public boolean evaluate(JsonElement json) {
		return !expression.evaluate(json);
	}

	@Override
	public String toString() {
		return "NotExpression [expression=" + expression + "]";
	}
}
