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

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.net.InetAddresses;
import at.alladin.nettest.server.control.exception.ClientNotRegisteredException;
import at.alladin.nettest.server.control.exception.EntityAlreadyExistsException;
import at.alladin.nettest.server.control.exception.TermsAndConditionsNotAcceptedException;
import at.alladin.nettest.shared.model.Client;
import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.spring.data.couchdb.api.ViewParams;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 * @author lb@specure.com
 *
 */
@Service
public class ClientService {

	private final Logger logger = LoggerFactory.getLogger(ClientService.class);
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Client> clientRepository;
	
	@Inject
	private CouchDbRepository<Measurement> measurementRepository;
	
	/**
	 * 
	 */
	private final String[] HTTP_HEADER_IP_ADDRESS_FIELDS = {
		//"HTTP_CLIENT_IP",
	    //"HTTP_X_CLUSTER_CLIENT_IP",
	    //"HTTP_X_FORWARDED_FOR", // can be comma delimited list of IPs
	    //"HTTP_X_FORWARDED",
	    //"HTTP_FORWARDED_FOR",
	    //"HTTP_FORWARDED",
	    "X-Real-IP",
	    "X-Client-IP",
		"X-FORWARDED-FOR"
	};
	
	/**
	 * 
	 */
	public ClientService() {

	}
	
	/**
	 * 
	 * @return
	 */
	public String getClientIpAddressString(HttpServletRequest request) { // TODO: correctly handle IPv6
//		final Enumeration<String> enumeration = request.getHeaderNames();
//		while (enumeration.hasMoreElements()) {
//			final String n = enumeration.nextElement();
//			logger.debug("{}: {}", n, request.getHeader(n));
//		}
		
		//logger.debug("USERAGENT: {}", request.getHeader("User-Agent"));
		
		for (String field : HTTP_HEADER_IP_ADDRESS_FIELDS) { // TODO: add correct handling (order) of these values
			final String ipAddress = request.getHeader(field);
			
			if (StringUtils.hasLength(ipAddress) && !"127.0.0.1".equals(ipAddress) && !ipAddress.contains("%")) { // TODO: check ipv6 localhost (but allow in dev)
				logger.debug("Found ip address {} in {} header", ipAddress, field);
				return ipAddress;
			}
		}
		
		logger.debug("Did not get ip address from headers, using request.getRemoteAddr");
		
		// workaround for %1 after ipv6... TODO: fix this properly
		String ipAddress = request.getRemoteAddr();
		if (ipAddress.contains("%")) {
			ipAddress = ipAddress.substring(0, ipAddress.lastIndexOf('%'));
		}
		
		return ipAddress;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public InetAddress getClientIpAddress(HttpServletRequest request) {
		return InetAddresses.forString(getClientIpAddressString(request));
	}

	/**
	 * 
	 * @param client
	 */
	public void registerNewClient(Client client) {
		if (!client.getTermsAndConditionsAccepted()) { // TODO: use bean validation?
			throw new TermsAndConditionsNotAcceptedException();
		}
		
		// check if client is already registered // TODO: is this necessary? since the uuid is created by the server, there will be no conflicts.
		if (client.getUuid() != null && !client.getUuid().isEmpty() && clientRepository.exists(client.getUuid())) {
			throw new EntityAlreadyExistsException("This client already exists."); // Don't return anything else -> information hiding
		}
		
		// TODO: other validation
		
		// generate uuid
		client.setUuid(UUID.randomUUID().toString()); // TODO: hypothetical: check for collision? 
		
		clientRepository.save(client);
	}
	
	/**
	 * 
	 * @param clientUuid
	 * @return
	 */
	public boolean isClientRegistered(String clientUuid) {
		if (clientUuid == null) {
			return false;
		}
		
		final Optional<Client> optionalDbClient = clientRepository.findOneByUuid(clientUuid);
		return optionalDbClient.isPresent();
	}
	
	/**
	 * 
	 * @param clientUuid
	 * @return
	 */
	public Client ensureClientRegistered(String clientUuid) {
		if (clientUuid == null) {
			throw new ClientNotRegisteredException("Your client is not registered."); // ClientNotFoundException?
		}
		
		final Optional<Client> optionalDbClient = clientRepository.findOneByUuid(clientUuid);
		
		return optionalDbClient.map(client -> {
			if (!client.getTermsAndConditionsAccepted()) {
				throw new TermsAndConditionsNotAcceptedException("Your client has not accepted the terms and conditions.");
			}
			
			logger.debug("Client (uuid={}) is registered", clientUuid);
			
			return client;
		}).orElseThrow(() -> new ClientNotRegisteredException("Your client is not registered.")); // ClientNotFoundException?
	}

	/**
	 * 
	 * @param clientUuid
	 * @return
	 */
	public Measurement getLastFinishedMeasurement(final String clientUuid) {
		final List<Measurement> measurementList  = measurementRepository.getView("by_client")
				.reduce(false)
				.startKey(clientUuid, "\ufff0")
				.endKey(clientUuid, null)
				.includeDocs(true)
				.descending(true)
				.limit(1)
				.query(Measurement.class);
			
		if (measurementList != null && !measurementList.isEmpty()) {
			return measurementList.get(0);
		}
		
		return null;
	}

	/**
	 * 
	 * @param clientUuid
	 * @param sortDescending
	 * @param includeDocs
	 * @return
	 */
	public List<Measurement> getMeasurementListByDate(final String clientUuid, final boolean sortDescending, final boolean includeDocs) {
		return getMeasurementListByDate(clientUuid, sortDescending, includeDocs, null);
	}
	
	/**
	 * 
	 * @param clientUuid
	 * @param sortDescending
	 * @param includeDocs
	 * @param pageable
	 * @return
	 */
	public List<Measurement> getMeasurementListByDate(final String clientUuid, final boolean sortDescending, final boolean includeDocs, final Pageable pageable) {
		final ViewParams viewParams = new ViewParams();
		viewParams.setReduce(false);
		viewParams.setDescending(sortDescending);
		viewParams.setIncludeDocs(includeDocs);
		viewParams.setStartKey(new Object[]{clientUuid, "\ufff0"});
		viewParams.setEndKey(new Object[]{clientUuid, null});
		viewParams.setValueType(Measurement.class);
		viewParams.setKeyType(String[].class);
		viewParams.setView("by_client");

		if (pageable == null) {
			return measurementRepository.findByView(viewParams);
		} else {
			return measurementRepository.findByView(viewParams, pageable).getContent();
		}
	}
}
