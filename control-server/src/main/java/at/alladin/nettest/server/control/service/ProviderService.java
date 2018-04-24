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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.model.AsnMapping;
import at.alladin.nettest.shared.model.MccMnc;
import at.alladin.nettest.shared.model.MccMncMapping;
import at.alladin.nettest.shared.model.Provider;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author Specure GmbH (lw@specure.com)
 *
 */
@Service
public class ProviderService {

	private final Logger logger = LoggerFactory.getLogger(ProviderService.class);
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Provider> providerRepository;
	
	/**
	 * 
	 * @param asn
	 * @param reverseDns
	 * @return
	 */
	public Provider findByASN(long asn, String reverseDns) {
		
		logger.debug("find provider by {} {}", asn, reverseDns);
		
		final List<Provider> providers = providerRepository.getView("by_asn")
			.reduce(false)
			.startKey(asn, "\ufff0")
			.endKey(asn)
			.includeDocs(true)
			.descending(true)
			.query(Provider.class);
		
		if (providers.isEmpty()) {
			return null;
		}
		
		final String reverseDnsLc = reverseDns == null ? null : reverseDns.toLowerCase(Locale.US);
		for (final Provider provider : providers) {
			final List<AsnMapping> mappings = provider.getAsnMappings();
			if (mappings != null) {
				for (final AsnMapping mapping : mappings) {
					if (mapping.getAsn() != asn) {
						continue;
					}
					
					final String suffix = mapping.getReverseDnsSuffix();
					
					if (suffix == null) {
						return provider;
					}
					
					if (reverseDnsLc != null && reverseDnsLc.endsWith(suffix.toLowerCase(Locale.US))) {
						return provider;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param mccMncSim
	 * @param mccMncNetwork
	 * @param date
	 * @return
	 */
	public Provider findByMccMnc(MccMnc mccMncSim, MccMnc mccMncNetwork, Date date) {
		final List<Provider> providers = providerRepository.getView("by_mccmnc")
			.reduce(false)
			.startKey(mccMncSim.getMcc(), mccMncSim.getMnc(), "\ufff0")
			.endKey(mccMncSim.getMcc(), mccMncSim.getMnc())
			.includeDocs(true)
			.descending(true)
			.query(Provider.class);

		logger.debug("provider lookup for sim mcc-mnc: {} and network mcc-mnc: {}", mccMncSim, mccMncNetwork);
		
		if (providers.isEmpty()) {
			return null;
		}
		
		for (final Provider provider : providers) {
			final List<MccMncMapping> mappings = provider.getMccMncMappings();

			if (mappings != null) {
				for (final MccMncMapping mapping : mappings) {
					if (!mccMncSim.equals(mapping.getMccMncSim())) {
						continue;
					}

					final Date validFrom = mapping.getValidFrom();
					if (validFrom != null && date.before(validFrom)) {
						continue;
					}

					final Date validTo = mapping.getValidTo();
					if (validTo != null && date.after(validTo)) {
						continue;
					}
					
					final MccMnc mappingNetworkMccMnc = mapping.getMccMncNetwork();
					if (mappingNetworkMccMnc == null || (mccMncNetwork != null && mccMncNetwork.equals(mappingNetworkMccMnc))) {
						return provider;
					}
				}
			}
		}

		return null;
	}
}
