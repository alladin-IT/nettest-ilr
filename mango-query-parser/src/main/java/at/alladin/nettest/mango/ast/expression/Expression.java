package at.alladin.nettest.mango.ast.expression;

import com.google.gson.JsonElement;

/**
 * 
 * @author lb@alladin.at
 *
 */
public interface Expression {

	boolean evaluate(final JsonElement json);
}
