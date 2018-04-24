package at.alladin.nettest.mango;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import at.alladin.nettest.mango.ast.expression.NorExpression;

public class MangoParserTestWithNor {
	
	@Test
	public void testParsingAndEvaluatingJsonWithDoubleSelectorsUsingNor() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{'selector': "
				+ "{'$nor': "
				+ "	[{'key':{'$eq': 10.1}},"
				+ "	{'key':{'$eq': 10.2}}]"
				+ "}}");
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		assertNotNull("expression index 0 != null", query.getSelector().getExpressionList().get(0));
		assertEquals("expression index 0 = NorExpression", NorExpression.class, 
				query.getSelector().getExpressionList().get(0).getClass());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: '10.1'}", JsonObject.class);
		assertTrue("query matches json (because '10.1' is not a number)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10.1}", JsonObject.class);
		assertFalse("query doesn't match json (10.1 is in nor list)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 10.2}", JsonObject.class);
		assertFalse("query doesn't match json (10.2 is in nor list)", query.evaluate(testObject));
	}
}
