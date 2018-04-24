package at.alladin.nettest.mango;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MangoParserTestWithIn {
	
	@Test
	public void testParsingAndEvaluatingJsonWithInSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'key':{'$in': [0,1]}}}");
		//System.out.println(query);
		
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertFalse("query doesn't match json (value is not in array)", query.evaluate(testObject));
			
		testObject = new Gson().fromJson("{key: 2}", JsonObject.class);
		assertFalse("query doesn't match json (2 is not in array)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 1}", JsonObject.class);
		assertTrue("query matches", query.evaluate(testObject));
	}

	@Test
	public void testParsingAndEvaluatingJsonWithNinSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'key':{'$nin': [0,1]}}}");
		//System.out.println(query);
		
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertTrue("query matches json (value is not in array)", query.evaluate(testObject));
			
		testObject = new Gson().fromJson("{key: 2}", JsonObject.class);
		assertTrue("query matches json (2 is not in array)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 1}", JsonObject.class);
		assertFalse("query doesn't match json (1 is in array)", query.evaluate(testObject));
	}
}
