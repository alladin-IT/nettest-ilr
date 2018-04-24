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

package at.alladin.nettest.server.control.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.model.NetworkType;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author lb@specure.com
 *
 */
@Service
public class NetworkTypeService {

	@Inject
	private CouchDbRepository<NetworkType> networkTypeRepository;
	
	/**
	 * 
	 * @return
	 */
	public List<NetworkType> getListByTechnologyOrderDescendingForNetworkTypeId(final int networkTypeId) {
		final List<NetworkType> networkTypes = networkTypeRepository.getView("by_network_type_id")
				.reduce(false)
				.includeDocs(true)
				.descending(true)
				.endKey(networkTypeId, null)
				.startKey(networkTypeId, new Object())
				.query(NetworkType.class);

		return networkTypes;
	}
	
	/**
	 * 
	 * @param networkTypeId
	 * @return
	 */
	public NetworkType getByNetworkTypeId(final int networkTypeId) {
		final List<NetworkType> networkTypes = networkTypeRepository.getView("by_network_type_id")
				.reduce(false)
				.endKey(networkTypeId, null)
				.startKey(networkTypeId, new Object())
				.includeDocs(true)
				.descending(true)
				.query(NetworkType.class);

		if (networkTypes != null && !networkTypes.isEmpty()) {
			return networkTypes.get(0);
		}
		
		return null;
	}
}
