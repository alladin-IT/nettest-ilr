package at.alladin.nettest.mango.ast.expression;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class Selector {

	private List<Expression> expressionList = new ArrayList<>();

	public List<Expression> getExpressionList() {
		return expressionList;
	}

	public void setExpressionList(List<Expression> statementList) {
		this.expressionList = statementList;
	}
	
	public boolean evaluate(final JsonElement json) {
		for (final Expression e : expressionList) {
			if (!e.evaluate(json)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "Selector [expressionList=" + expressionList + "]";
	}
}
