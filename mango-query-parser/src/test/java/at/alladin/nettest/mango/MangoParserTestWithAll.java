package at.alladin.nettest.mango;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MangoParserTestWithAll {
	
	@Test
	public void testParsingAndEvaluatingJsonWithAllSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'key':{'$all': [0,1]}}}");
		//System.out.println(query);
		
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertFalse("query doesn't match json (value is not in array)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: null}", JsonObject.class);
		assertFalse("query doesn't match json (key is null)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: []}", JsonObject.class);
		assertFalse("query doesn't match json (0 and 1 is missing in array)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: [2]}", JsonObject.class);
		assertFalse("query doesn't match json (0 and 1 is missing in array)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: [0,2,3,4]}", JsonObject.class);
		assertFalse("query doesn't match json (1 is missing in array)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: [0,1,2,3,4]}", JsonObject.class);
		assertTrue("query matches", query.evaluate(testObject));
	}
}
