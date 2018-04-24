package at.alladin.nettest.mango.ast.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;

/**
 * <h2>Query operator <code>$regex</code></h2>
 * 
 * Java regular expression, matches if {@link Matcher#find()} returns true.
 * 
 * @author lb@alladin.at
 *
 */
public class RegexExpression implements Expression {
	
	private final String regex;

	private final Pattern pattern;
	
	public RegexExpression(final String regex) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
	}
	
	public String getRegex() {
		return regex;
	}

	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public boolean evaluate(JsonElement json) {
		if (!json.isJsonPrimitive() || pattern == null) {
			return false;
		}
		
		return pattern.matcher(json.getAsJsonPrimitive().getAsString()).find();
	}

	@Override
	public String toString() {
		return "RegexExpression [regex=" + regex + ", pattern=" + pattern + "]";
	}
}
