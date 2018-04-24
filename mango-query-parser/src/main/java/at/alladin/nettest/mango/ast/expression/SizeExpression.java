package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * <h2>Query operator <code>$size</code></h2>
 * 
 * Matches the length of an array field in a document, non-array fields cannot match this condition.
 * 
 * @author lb@alladin.at
 *
 */
public class SizeExpression implements Expression {
	
	private final Long size;

	public SizeExpression(final Long size) {
		this.size = size;
	}
	
	@Override
	public boolean evaluate(JsonElement json) {
		if (!json.isJsonArray()) {
			return false;
		}
		
		return json.getAsJsonArray().size() == size;
	}
}
