package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * represents a primitive value (number, string, boolean, null) 
 * 
 * @author lb@alladin.at
 *
 */
public class ValueExpression implements Expression {

	private Object value;

	public ValueExpression() {
		this(null);
	}
	
	public ValueExpression(final Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ValueExpression [value=" + value + "]";
	}

	@Override
	public boolean evaluate(JsonElement json) {
		return value != null;
	}	
}
