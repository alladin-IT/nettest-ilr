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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.model.Device;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Service
public class DeviceService {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(DeviceService.class);
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Device> deviceRepository;
	
	/**
	 * 
	 */
	public DeviceService() {

	}

	/**
	 * 
	 * @param codename
	 * @return
	 */
	@Cacheable(value = "defaultCache", key = "'device_' + #codename")
	public Device findByCodename(String codename) {
		return deviceRepository.findOne("device__" + codename); // could be improved: remove device__
	}
}
