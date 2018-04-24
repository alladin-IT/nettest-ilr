package at.alladin.nettest.mango;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MangoParserTestWithSize {
	
	@Test
	public void testParsingAndEvaluatingJsonWithSizeSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'keys':{'$size': 3}}}");
		//System.out.println(query);
		
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		
		JsonObject testObject = new Gson().fromJson("{keys: [3,4,7]}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));

		testObject = new Gson().fromJson("{keys: ['one','two','four']}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));

		testObject = new Gson().fromJson("{keys: 'test0'}", JsonObject.class);
		assertFalse("query doesn't match json (keys not an array)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{keys: null}", JsonObject.class);
		assertFalse("query doesn't match json (keys is null)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{keys: []}", JsonObject.class);
		assertFalse("query doesn't match json (keys is empty = size 0)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{keys: [0,1,2,3]}", JsonObject.class);
		assertFalse("query doesn't match json (keys size is not 3)", query.evaluate(testObject));
	}
}
