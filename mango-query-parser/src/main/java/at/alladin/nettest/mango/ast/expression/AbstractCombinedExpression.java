package at.alladin.nettest.mango.ast.expression;

import java.util.List;

/**
 * 
 * @author lb@alladin.at
 *
 */
public abstract class AbstractCombinedExpression implements Expression {

	protected List<Expression> expressionList;

	public List<Expression> getExpressionList() {
		return expressionList;
	}

	public void setExpressionList(List<Expression> expressionList) {
		this.expressionList = expressionList;
	}

	@Override
	public String toString() {
		return "AbstractCombinedExpression [expressionList=" + expressionList + "]";
	}
}
