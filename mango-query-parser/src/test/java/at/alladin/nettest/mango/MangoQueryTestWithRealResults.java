package at.alladin.nettest.mango;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MangoQueryTestWithRealResults {

	JsonObject loadTest(String fileName) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("at/alladin/nettest/mango/" + fileName).getFile());
		return new Gson().fromJson(new FileReader(file), JsonObject.class);
	}
	
	MangoQuery loadQuery(String fileName) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("at/alladin/nettest/mango/query/" + fileName).getFile());
		final BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		final StringBuilder sb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		br.close();
		return MangoParser.fromJsonString(sb.toString());		
	}

	JsonObject testFile1;
	
	@Before
	public void loadTestFile() throws Exception {
		this.testFile1 = loadTest("test1.json");
	}
	
	@Test
	public void testNegativeByIdNull() throws Exception {
		MangoQuery query = loadQuery("query01_id_null.json");
		assertNotNull("query not null", query);
		assertFalse("_id is not null", query.evaluate(testFile1));
	}

	@Test
	public void testPositiveByIdNotNull() throws Exception {
		MangoQuery query = loadQuery("query01_id_not_null.json");
		assertNotNull("query not null", query);
		assertTrue("_id is not null", query.evaluate(testFile1));
	}

	@Test
	public void testPositiveByIdAndNotDeleted() throws Exception {
		MangoQuery query = loadQuery("query01_id_and_not_deleted.json");
		assertNotNull("query not null", query);
		assertTrue("_id is not null and deleted is false", query.evaluate(testFile1));
	}

	@Test
	public void testPositiveByClientInfoUuid() throws Exception {
		MangoQuery query = loadQuery("query01_clientinfo_uuid.json");
		assertNotNull("query not null", query);
		assertTrue("client_info.uuid is matching", query.evaluate(testFile1));
	}
}
