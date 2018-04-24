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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import at.alladin.nettest.server.control.service.ClientService;
import at.alladin.nettest.server.control.service.MeasurementService;
import at.alladin.nettest.server.control.service.ProfileService;
import at.alladin.nettest.server.control.service.QosMeasurementService;
import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.shared.model.MeasurementQos;
import at.alladin.nettest.shared.model.MeasurementQosResult;
import at.alladin.nettest.shared.model.MeasurementServer;
import at.alladin.nettest.shared.model.qos.QosMeasurementObjective;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nettest.shared.model.request.QosMeasurementRequest;
import at.alladin.nettest.shared.model.request.QosMeasurementResultSubmitRequest;
import at.alladin.nettest.shared.model.response.QosMeasurementRequestResponse;
import at.alladin.nettest.shared.model.response.QosMeasurementResultResponse;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

import at.alladin.rmbt.shared.qos.testscript.TestScriptInterpreter;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements/qos")
public class QosMeasurementResource {
	
	private final Logger logger = LoggerFactory.getLogger(QosMeasurementResource.class);

	/**
	 * 
	 */
	@Inject
	private ClientService clientService;
	
	/**
	 * 
	 */
	@Inject
	private MeasurementService measurementService;
	
	/**
	 * 
	 */
	@Inject
	private QosMeasurementService qosMeasurementService;
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Measurement> measurementRepository;
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<MeasurementServer> measurementServerRepository;
	
	/**
	 * 
	 */
	@Inject
	private ProfileService profileService;
	
	/**
	 * 
	 */
	private Map<String, MeasurementServer> temporaryQosMeasurementServerCache = new HashMap<>(); // TODO: use spring cache/memcached
	
	/**
	 * 
	 * @return
	 */
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QosMeasurementRequestResponse> createQosMeasurement(@RequestBody QosMeasurementRequest request, HttpServletRequest httpServletRequest, @ApiIgnore Locale locale) {
		// clientService.ensureClientRegistered(request.getClientUuid()); // TODO
		
		// get measurement model (either the existing one or a new one)
		Measurement measurement = null;
		if (request.getMeasurementUuid() != null) { // check if measurement uuid is provided
			// yes -> add qos measurements to the same document
			measurement = measurementService.findMeasurementByUuid(request.getMeasurementUuid());
		} else {
			// no -> create a new measurement document only for the qos test
			measurement = new Measurement(); // TODO: add more values!
		}
		
		// TODO: return custom error code if there are no objectives in the database
		
		final MeasurementQos measurementQos = new MeasurementQos();
		measurementQos.setStatus("IN_PROGRESS"); // TODO: enum
		
		measurement.setQos(measurementQos);
		
		// save measurement
		measurementRepository.save(measurement);
		
		///
		
		final InetAddress clientAddress = clientService.getClientIpAddress(httpServletRequest);
		
		logger.info("New qos measurement request from {}@{}", request.getClientUuid(), clientAddress.getHostAddress());
		
		// get all activated qos objectives
		final List<QosMeasurementObjective> activeQosMeasurementObjectives = qosMeasurementService
				.findActiveObjectivesByProfile(profileService.matchBasicRequest(request));

		final Map<QosMeasurementType, List<Map<String, Object>>> objectives = new HashMap<>();
		
		activeQosMeasurementObjectives./*parallelS*/stream().forEach(o -> {
			final QosMeasurementType type = o.getType();
			
			List<Map<String, Object>> objectiveList = objectives.get(type);
			if (objectiveList == null) {
				objectiveList = new ArrayList<>();
				objectives.put(type, objectiveList);
			}
			
			final Map<String, Object> objective = new HashMap<>();
			
			///
			
			boolean testInvalid = false;
			
			final Map<String, Object> params = o.getParams();
			
			final Set<String> paramKeySet = params.keySet();
			
			for (String key : paramKeySet) {
				// TODO: only process strings? (because ints/doubles cannot be interpreted)
				final Object scriptResult = TestScriptInterpreter.interprete(params.get(key).toString(), null);
				if (scriptResult != null) {
					objective.put(key, scriptResult);
				} else {
					testInvalid = true;
				}
			}
			
			if (testInvalid) {
				logger.debug("TEST INVALID -> return");
				return; // skip this objective if invalid
			}
			
			objective.put("qos_test_uid", "" + o.getObjectiveId());
			objective.put("concurrency_group", "" + o.getConcurrencyGroup());
			
			final String measurementServerUuid = o.getMeasurementServerUuid();
			if (measurementServerUuid != null && !"".equals(measurementServerUuid)) {
				// TODO: cache
				final MeasurementServer measurementServer = 
					temporaryQosMeasurementServerCache.containsKey(measurementServerUuid) ? 
							temporaryQosMeasurementServerCache.get(measurementServerUuid) : 
								measurementServerRepository.findOneByUuid(measurementServerUuid).get(); // TODO: check also if server is active
				
				if (measurementServer != null) {
					temporaryQosMeasurementServerCache.putIfAbsent(measurementServerUuid, measurementServer);
					
					if (clientAddress instanceof Inet6Address) {
						objective.put("server_addr", measurementServer.getWebAddressIpv6());
					} else {
						objective.put("server_addr", measurementServer.getWebAddressIpv4());
					}
					
					objective.put("server_port", "" + measurementServer.getPortSsl()); // TODO: configure if ssl shall be used or not // TODO: !!! // TODO: gson bug?
				};
			}
			
//			// add objective if not invalid
//			if (!testInvalid) {
				objectiveList.add(objective);
//			}
		});
		
		///
		
		final QosMeasurementRequestResponse qosMeasurementRequestResponse = new QosMeasurementRequestResponse();
		qosMeasurementRequestResponse.setObjectives(objectives);
		qosMeasurementRequestResponse.setTestUuid(measurement.getUuid());
		//qosMeasurementRequestResponse.setTestToken(testToken); // TODO
		
		return new ResponseEntity<>(qosMeasurementRequestResponse, HttpStatus.CREATED);
	}
	
	/**
	 * 
	 * @return
	 */
	@ApiImplicitParams({
		@ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> putQosMeasurement(@PathVariable String uuid, @RequestBody QosMeasurementResultSubmitRequest qosResultRequest, @ApiIgnore Locale locale) {
		// get previously stored document (by measurement_uuid)
		final Measurement measurement = measurementService.findMeasurementByUuid(uuid); // if null, create new one?

		// TODO: check (for) test token

		final MeasurementQos measurementQos = measurement.getQos(); // TODO: check if measurementQos is null?
		measurementQos.setStatus("FINISHED"); // TODO: enum
		
//		if (measurementQos.getResults() != null && !measurementQos.getResults().isEmpty()) { // TODO: improve check
//			// qos already added to measurement
//			throw new QosMeasurementAlreadyDoneException(); // TODO: message
//		}
		
		final QosMeasurementResultResponse qosMeasurementResultResponse = qosMeasurementService.processQosMeasurements(locale, measurementQos, qosResultRequest.getQosResultList());
		
		measurementRepository.save(measurement); // TODO: since we don't return something to the client here, do this async? or just return the qos result?
		
		// TODO: AdvancedReporting
        
		return new ResponseEntity<>(qosMeasurementResultResponse, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @return
	 */
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QosMeasurementResultResponse> getQosMeasurement(@PathVariable String uuid, @ApiIgnore Locale locale) {
		final Measurement measurement = measurementService.findMeasurementByUuid(uuid); // TODO: checks
		final MeasurementQos measurementQos = measurement.getQos(); // TODO: checks
		
		final List<MeasurementQosResult> measurementQosResultList = measurementQos.getResults();
		
		// workarounds for old<-->new model, TODO: needs refactoring
		final List<JsonObject> resultList = measurementQosResultList.parallelStream().map(result -> {
			final JsonObject jsonObject = result.getResult();
			
			jsonObject.addProperty("test_type", result.getType().getValue());
			jsonObject.addProperty("qos_test_uid", result.getObjectiveId());
			
			return jsonObject;
		}).collect(Collectors.toList());
		
		final QosMeasurementResultResponse qosMeasurementResultResponse = qosMeasurementService.processQosMeasurements(locale, measurementQos, resultList);
		
		// TODO: AdvancedReporting
		
		return new ResponseEntity<>(qosMeasurementResultResponse, HttpStatus.OK);
	}
}
