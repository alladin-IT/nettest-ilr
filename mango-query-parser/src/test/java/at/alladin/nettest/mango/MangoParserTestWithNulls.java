package at.alladin.nettest.mango;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MangoParserTestWithNulls {
	
	@Test
	public void testParsingAndEvaluatingJsonWithNullNeAndEqSelectors() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: "
				+ "	{'key':{'$eq': null},"
				+ "'key2':{'$ne': null}}}");
		//System.out.println(query);
		
		assertNotNull("query != null", query);
		assertEquals("selector size = 2", 2, query.getSelector().getExpressionList().size());
		
		JsonObject testObject = new Gson().fromJson("{key: 'value'}", JsonObject.class);
		assertFalse("query doesn't match json (value is string)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: 'null'}", JsonObject.class);
		assertFalse("query doesn't match json (key contains 'null' string)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: null}", JsonObject.class);
		assertFalse("query matches (key2 is missing but its also null)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: null, key2: null}", JsonObject.class);
		assertFalse("query doesn't match (key2 is null)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: null, key2: 10}", JsonObject.class);
		assertTrue("query matches", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: null, key2: 'null'}", JsonObject.class);
		assertTrue("query matches", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: null, key2: true}", JsonObject.class);
		assertTrue("query matches", query.evaluate(testObject));
	}
	
	@Test
	public void testParsingAndEvaluatingJsonWithNonexsitingNe() {
		final MangoQuery query = MangoParser.fromJsonString(
				"{selector: "
				+ "	{'key':{'$ne': 1}}}");
		//System.out.println(query);
		
		assertNotNull("query != null", query);
		assertEquals("selector size = 1", 1, query.getSelector().getExpressionList().size());
		
		JsonObject testObject = new Gson().fromJson("{key: 1}", JsonObject.class);
		assertFalse("query doesn't match json (value is = 1)", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: null}", JsonObject.class);
		assertTrue("query matches", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key2: 1}", JsonObject.class);
		assertTrue("query matches", query.evaluate(testObject));
		
		testObject = new Gson().fromJson("{key: '1'}", JsonObject.class);
		assertTrue("query matches", query.evaluate(testObject));
	}

}
