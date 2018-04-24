package at.alladin.nettest.mango.ast.expression;

import java.util.Locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class ComparisonExpression implements Expression {

	public interface OpComparable {
		boolean compare(final JsonElement json, final Object expectedValue);
	}
	
	public static enum Operator implements OpComparable {
		EQ {
			@Override
			public boolean compare(JsonElement json, Object expectedValue) {
				if (json.isJsonPrimitive()) {
					if (expectedValue instanceof Boolean && json.getAsJsonPrimitive().isBoolean()) {
						return Boolean.parseBoolean(String.valueOf(expectedValue)) == json.getAsBoolean();
					}
					else if (expectedValue instanceof String && json.getAsJsonPrimitive().isString()) {
						return String.valueOf(expectedValue).equals(json.getAsString());
					}
					else if (expectedValue instanceof Double && json.getAsJsonPrimitive().isNumber()) {
						return json.getAsNumber().doubleValue() == (double)(expectedValue);
					}
				}
				
				if (expectedValue == null) {
					return json == null || json.isJsonNull();
				}
				return false;
			}
		},
		NE {
			@Override
			public boolean compare(JsonElement json, Object expectedValue) {
				if (json.isJsonPrimitive()) {
					if (expectedValue instanceof Boolean && json.getAsJsonPrimitive().isBoolean()) {
						return Boolean.parseBoolean(String.valueOf(expectedValue)) != json.getAsBoolean();
					}
					else if (expectedValue instanceof String && json.getAsJsonPrimitive().isString()) {
						return !String.valueOf(expectedValue).equals(json.getAsString());
					}
					else if (expectedValue instanceof Double && json.getAsJsonPrimitive().isNumber()) {
						return json.getAsNumber().doubleValue() != (double)(expectedValue);
					}
				}
				
				if (expectedValue == null) {
					return json != null && !json.isJsonNull();
				}
				
				return true;
			}
		},
		GT {
			@Override
			public boolean compare(JsonElement json, Object expectedValue) {
				if (json.isJsonPrimitive() && expectedValue instanceof Double && json.getAsJsonPrimitive().isNumber()) {
					return json.getAsNumber().doubleValue() > (double)(expectedValue);
				}			
				return false;
			}
		},
		LT {
			@Override
			public boolean compare(JsonElement json, Object expectedValue) {
				if (json.isJsonPrimitive() && expectedValue instanceof Double && json.getAsJsonPrimitive().isNumber()) {
					return json.getAsNumber().doubleValue() < (double)(expectedValue);
				}			
				return false;
			}
		},
		GTE {
			@Override
			public boolean compare(JsonElement json, Object expectedValue) {
				if (json.isJsonPrimitive() && expectedValue instanceof Double && json.getAsJsonPrimitive().isNumber()) {
					return json.getAsNumber().doubleValue() >= (double)(expectedValue);
				}						
				return false;
			}
		},
		LTE {
			@Override
			public boolean compare(JsonElement json, Object expectedValue) {
				if (json.isJsonPrimitive() && expectedValue instanceof Double && json.getAsJsonPrimitive().isNumber()) {
					return json.getAsNumber().doubleValue() <= (double)(expectedValue);
				}						
				return false;
			}
		};
		
		public static Operator fromString(final String op) {
			if (op == null) {
				return null;
			}
			if (op.startsWith("$")) {
				return Operator.valueOf(op.substring(1).toUpperCase(Locale.US));
			}
			else {
				return Operator.valueOf(op.toUpperCase(Locale.US));
			}
		}
	}
	
	private Operator operator;
	
	private ValueExpression value;

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public ValueExpression getValue() {
		return value;
	}

	public void setValue(ValueExpression value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ComparisonExpression [operator=" + operator + ", value=" + value + "]";
	}

	@Override
	public boolean evaluate(JsonElement json) {
		//System.out.println(json + " -> isPrimitive: " + json.isJsonPrimitive() + " isNull: " + json.isJsonNull());
		if (!json.isJsonPrimitive() && !json.isJsonNull()) {
			return false;
		}

		if (json.isJsonPrimitive()) {
			return operator.compare(json, value.getValue());
		}
		else {
			return operator.compare(JsonNull.INSTANCE, value.getValue());
		}
	}
}
