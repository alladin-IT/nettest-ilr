package at.alladin.nettest.mango;

/**
 * 
 *  @author lb@alladin.at
 *  
 */
import java.util.regex.Matcher;

import com.google.gson.JsonElement;

import at.alladin.nettest.mango.ast.expression.Selector;

/**
 * For parsing queries use {@link MangoParser#fromJsonString(String)} or {@link MangoParser#fromJsonElement(JsonElement)}<br>
 * <h3>Supported combination operators</h3><br>
 * <ul>
 * <li><code>$and</code> (matches if all the selectors in the array match)</li>
 * <li><code>$or</code> (matches if any of the selectors in the array match)</li>
 * <li><code>$nor</code> (matches if none of the selectors in the array match)</li>
 * <li><code>$not</code> (matches if the given selector does not match)</li>
 * <li><code>$all</code> (matches an array value if it contains all the elements of the argument array, non-array fields cannot match this condition)</li>
 * </ul> 
 * <h3>Supported condition operators</h3><br>
 * <ul>
 * <li><code>$lt</code> (lower than)</li>
 * <li><code>$lte</code> (lower than or equal)</li>
 * <li><code>$eq</code> (equal)</li>
 * <li><code>$ne</code> (not equal)</li>
 * <li><code>$gt</code> (greater than)</li>
 * <li><code>$gte</code> (greater than or equal)</li>
 * <li><code>$in</code> (matches if field value exists in array)</li>
 * <li><code>$nin</code> (matches if field value does not exist in array)</li>
 * <li><code>$regex</code> (Java regular expression, matches if {@link Matcher#find()} returns true)</li>
 * <li><code>$size</code> (matches the length of an array field in a document, non-array fields cannot match this condition)</li>
 * </ul> 
 * <h3>Supported nested objects structures</h3><br>
 * Standard JSON structure:<br>
 * <code>
 * <pre>
 * 	{
 * 		"field": {
 * 			"subfield": 10
 * 		}
 * 	}
 * </pre>
 * </code>
 * dot notation:<br>
 * <code>
 * <pre>
 * 	{
 * 		"field.subfield": 10
 * 	}
 * </pre>
 * </code>
 * @author lb@alladin.at
 *
 */
public class MangoQuery {

	private Selector selector;

	public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public boolean evaluate(final JsonElement json) {
		return selector.evaluate(json);
	}
	
	@Override
	public String toString() {
		return "MangoQuery [selector=" + selector + "]";
	}
}
