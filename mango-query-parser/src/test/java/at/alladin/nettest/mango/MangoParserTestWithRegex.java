package at.alladin.nettest.mango;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MangoParserTestWithRegex {
	
	@Test
	public void testParsingAndEvaluatingJsonWithInSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: {'key':{'$regex': 'test[0-2]'}}}");
		//System.out.println(query);
		
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		
		JsonObject testObject = new Gson().fromJson("{key: 'test123'}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 'test0'}", JsonObject.class);
		assertTrue("query matches json", query.evaluate(testObject));

		testObject = new Gson().fromJson("{keys: 'test0'}", JsonObject.class);
		assertFalse("query doesn't match json ('key' missing)", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 'test4'}", JsonObject.class);
		assertFalse("query doesn't match json ('test4' not in regex 'test[0-2]')", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 'text1'}", JsonObject.class);
		assertFalse("query doesn't match json ('text1' not in regex 'test[0-2]')", query.evaluate(testObject));

		testObject = new Gson().fromJson("{key: 'Testo'}", JsonObject.class);
		assertFalse("query doesn't match json ('Test0' not in regex 'test[0-2]')", query.evaluate(testObject));
	}
}
