package at.alladin.nettest.mango;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MangoParserTestWithNot {
	
	@Test
	public void testParsingAndEvaluatingJsonWithNotSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'key': {'$not': {'$eq': 30}}}}");
		System.out.println(query);
		
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		
		JsonObject testObject = new Gson().fromJson("{key: 'test123'}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 'test0'}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 31}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 30}", JsonObject.class);
		assertFalse("query doesn't match json (key = 30)", query.evaluate(testObject));
	}
}
