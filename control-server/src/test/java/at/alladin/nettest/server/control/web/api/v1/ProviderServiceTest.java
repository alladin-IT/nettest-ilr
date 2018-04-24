/*******************************************************************************
 * Copyright 2016-2017 alladin-IT GmbH
 * Copyright 2016 SPECURE GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.server.control.web.api.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import at.alladin.nettest.server.control.service.ProviderService;
import at.alladin.nettest.shared.model.AsnMapping;
import at.alladin.nettest.shared.model.MccMnc;
import at.alladin.nettest.shared.model.MccMncMapping;
import at.alladin.nettest.shared.model.Provider;
import at.alladin.nettest.shared.model.exception.IllegalMccMncException;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

public class ProviderServiceTest extends AbstractTest {

	@Inject
	private ProviderService providerService;

	@Inject
	private CouchDbRepository<Provider> providerRepository;

	// @Before
	// public void setUp() {
	// }

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testMccMncToString() throws Exception {
		assertEquals("123-45", new MccMnc(123, 45).toString());
		assertEquals("231-456", new MccMnc(231, 456).toString());
		assertEquals("123-01", new MccMnc(123, 1).toString());
		assertEquals("123-01", MccMnc.fromString("123-01", null).toString());

		exception.expect(IllegalMccMncException.class);
		MccMnc.fromString("123-1", null);
	}

	private Provider insertedProvider;

	@Test
	public void testInsertProvider() {
		{
			Provider provider = new Provider();
			provider.setName("testprovider123");
			provider.setShortname("tp123");

			List<AsnMapping> asnMappings = new ArrayList<>();
			asnMappings.add(new AsnMapping(987, null));
			provider.setAsnMappings(asnMappings);

			List<MccMncMapping> mccMncMappings = new ArrayList<>();
			MccMncMapping mapping = new MccMncMapping();
			mapping.setMccMncSim(new MccMnc(220, 1));
			mapping.setMccMncNetwork(new MccMnc(220, 99));
			mapping.setValidFrom(new Date(1463155348000l));
			mccMncMappings.add(mapping);
			provider.setMccMncMappings(mccMncMappings);

			providerRepository.save(provider);

			// insertedProvider = provider;
		}
		{
			Provider provider = providerService.findByASN(987, null);
			assertNotNull(provider);
			assertEquals("testprovider123", provider.getName());
			assertEquals("tp123", provider.getShortname());
		}
	}

	@After
	public void cleanUp() {
		if (insertedProvider != null)
			providerRepository.delete(insertedProvider);
	}

	@Test
	public void testFindByMccMnc() {
		Provider provider;
		
		final Date now = new Date();
		
		provider = providerService.findByMccMnc(new MccMnc(200,1), new MccMnc(200,99), now);
		assertNotNull(provider);
		assertEquals("Telenor", provider.getShortname());
		
		provider = providerService.findByMccMnc(new MccMnc(200,1), new MccMnc(201,88), now);
		assertNotNull(provider);
		assertEquals("mt:s", provider.getShortname());
		
		provider = providerService.findByMccMnc(new MccMnc(200,1), null, now);
		assertNotNull(provider);
		assertEquals("mt:s", provider.getShortname());
	}

	@Test
	public void testFindByASN() {
		Provider provider;

		provider = providerService.findByASN(123, "test.at");
		assertNotNull(provider);
		assertEquals("UPC", provider.getShortname());

		provider = providerService.findByASN(123, "test123.AC.at");
		assertNotNull(provider);
		assertEquals("Telenor", provider.getShortname());

		provider = providerService.findByASN(999, "test123.ac.at");
		assertNull(provider);

		provider = providerService.findByASN(124, "test123.ac.at");
		assertNotNull(provider);
		assertEquals("UPC", provider.getShortname());

		provider = providerService.findByASN(123, "bla.com");
		assertNotNull(provider);
		assertEquals("mt:s", provider.getShortname());

		provider = providerService.findByASN(123, null);
		assertNotNull(provider);
		assertEquals("mt:s", provider.getShortname());
	}
}
