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

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import at.alladin.nettest.server.control.service.NetworkTypeService;
import at.alladin.nettest.shared.model.NetworkType;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author lb@specure.com
 *
 */
public class NetworkTypeServiceTest extends AbstractTest {


	@Inject
	private NetworkTypeService networkTypeService;
	
	@Inject
	private CouchDbRepository<NetworkType> networkTypeRepository;

	@Test
	public void testInsertAndFetchByTechnologyOrderDescending() {
		NetworkType networkType1 = new NetworkType();
		networkType1.setName("NET9999999-0");
		networkType1.setNetworkTypeId(9999999);
		networkType1.setTechnologyOrder(0);
		networkTypeRepository.save(networkType1);
		
		NetworkType networkType2 = new NetworkType();
		networkType2.setName("NET9999999-100");
		networkType2.setNetworkTypeId(9999999);
		networkType2.setTechnologyOrder(100);
		networkTypeRepository.save(networkType2);

		final List<NetworkType> networkTypeList = networkTypeService.getListByTechnologyOrderDescendingForNetworkTypeId(networkType1.getNetworkTypeId());
		
		assertNotNull(networkTypeList);
		assertEquals(networkType2.getTechnologyOrder(), networkTypeList.get(0).getTechnologyOrder());
		
		networkTypeRepository.delete(networkType1);
		networkTypeRepository.delete(networkType2);
	}
	
	@Test
	public void testInsertAndFetchByNetworkTypeId() {
		NetworkType networkType1 = new NetworkType();
		networkType1.setName("NET666");
		networkType1.setNetworkTypeId(666);
		networkTypeRepository.save(networkType1);
		
		NetworkType networkType2 = new NetworkType();
		networkType2.setName("NET777");
		networkType2.setNetworkTypeId(777);
		networkTypeRepository.save(networkType2);

		final NetworkType networkType1Test = networkTypeService.getByNetworkTypeId(networkType1.getNetworkTypeId());
		
		assertNotNull(networkType1Test);
		assertEquals(networkType1.getNetworkTypeId(), networkType1Test.getNetworkTypeId());
		assertEquals(networkType1.getName(), networkType1Test.getName());

		final NetworkType networkType2Test = networkTypeService.getByNetworkTypeId(networkType2.getNetworkTypeId());
		
		assertNotNull(networkType2Test);
		assertEquals(networkType2.getNetworkTypeId(), networkType2Test.getNetworkTypeId());
		assertEquals(networkType2.getName(), networkType2Test.getName());

		networkTypeRepository.delete(networkType1);
		networkTypeRepository.delete(networkType2);
	}
}
