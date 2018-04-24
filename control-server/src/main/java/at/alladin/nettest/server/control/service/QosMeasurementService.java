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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import at.alladin.nettest.shared.model.MeasurementQos;
import at.alladin.nettest.shared.model.MeasurementQosResult;
import at.alladin.nettest.shared.model.qos.QosMeasurementObjective;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nettest.shared.model.response.QosMeasurementResultResponse;
import at.alladin.nettest.spring.data.couchdb.api.ViewParams;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

import at.alladin.nettest.shared.server.helper.GsonHelper;
import at.alladin.rmbt.shared.db.QoSTestResult;
import at.alladin.rmbt.shared.db.QoSTestResult.TestType;
import at.alladin.rmbt.shared.qos.AbstractResult;
import at.alladin.rmbt.shared.qos.QoSUtil;
import at.alladin.rmbt.shared.qos.ResultDesc;
import at.alladin.rmbt.shared.qos.ResultOptions;
import at.alladin.rmbt.shared.qos.testscript.TestScriptInterpreter;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Service
public class QosMeasurementService {

	private final Logger logger = LoggerFactory.getLogger(QosMeasurementService.class);

	/**
	 * 
	 */
	@Inject
	private MessageSource messageSource;
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<QosMeasurementObjective> qosMeasurementObjectiveRepository;
	
	/**
	 * 
	 */
	@Inject
	private CouchDbClient configurationDbClient;
	
	/**
	 * 
	 */
	final Set<String> excludeTestTypeKeys = new HashSet<>(Arrays.asList(new String[] { "test_type", "qos_test_uid" }));
	
	/**
	 * 
	 * @return
	 */
	public List<QosMeasurementObjective> findActiveObjectives() {
		final ViewParams viewParams = new ViewParams();
	
		viewParams.setReduce(false);
		viewParams.setView("by_measurement_class");
		viewParams.setIncludeDocs(true);
		//viewParams.setKeyType(Integer.class);
		viewParams.setKey(1); // previously: testObjectiveDao.getByTestClass(1);
	
		final List<QosMeasurementObjective> activeQosMeasurementObjectives = qosMeasurementObjectiveRepository.findByView(viewParams);
	
		logger.debug("Found active qosMeasurementObjectives: {}", activeQosMeasurementObjectives);
	
		return activeQosMeasurementObjectives;
	}
	
	public List<QosMeasurementObjective> findActiveObjectivesByProfile(final List<String> profileList) {		
		//add all qos objectives without profile {"profiles": null}
		final List<Object[]> keys = new ArrayList<>();
		keys.add(new Object[]{"QosMeasurementObjective", null, 1});
		for (final String profile : profileList) {
			keys.add(new Object[]{"QosMeasurementObjective", profile, 1});
		}
		
		final List<QosMeasurementObjective> activeQosMeasurementObjectives = configurationDbClient
				.view("General/by_profile")
				.keys(keys)
				.reduce(false)
				.includeDocs(true)
				.query(QosMeasurementObjective.class);
	
		logger.debug("Found active qosMeasurementObjectives for profiles {}: {}", profileList, activeQosMeasurementObjectives);
	
		return activeQosMeasurementObjectives;
	}

	
	// improve speed by caching objectives...
	// TODO: use/fix spring caching, see http://stackoverflow.com/questions/12115996/spring-cache-cacheable-method-ignored-when-called-from-within-the-same-class
	// need compile time weaving or a QosMeasurementDao object that has @Cachable annotations...
	private final Map<Long, QosMeasurementObjective> objectiveCache = new HashMap<>();
	
	/**
	 * 
	 * @param qosTestUid
	 * @return
	 */
	//@Cacheable(value = "qos", key = /*"'qos_objective_by_uid_' + */"#qosTestUid") // TODO: cache!
	public QosMeasurementObjective findObjectiveByQosTestUid(Long qosTestUid) {
		if (objectiveCache.containsKey(qosTestUid)) {
			return objectiveCache.get(qosTestUid);
		}
		
		final ViewParams viewParams = new ViewParams();
	
		viewParams.setReduce(false);
		viewParams.setView("by_qos_test_uid");
		viewParams.setIncludeDocs(true);
		//viewParams.setKeyType(Integer.class);
		viewParams.setKey(qosTestUid);
				
		final List<QosMeasurementObjective> activeQosMeasurementObjectives = qosMeasurementObjectiveRepository.findByView(viewParams);
	
		//logger.debug("Found qosMeasurementObjectives for qos_test_uid {}: {}", qosTestUid, activeQosMeasurementObjectives);
	
		if (activeQosMeasurementObjectives.size() < 1) {
			return null;
		}
		
		final QosMeasurementObjective objective = activeQosMeasurementObjectives.get(0);
		
		objectiveCache.put(qosTestUid, objective);
		
		return objective;
	}
	
	/**
	 * 
	 * @param locale
	 * @return
	 */
	public List<Map<String,Object>> generateTestResultDetailDescription(Locale locale, Map<TestType, TreeSet<ResultDesc>> resultKeys) {
		final Gson gson = GsonHelper.createDatabaseGsonBuilder().create();
		
		final JSONArray resultDescArray = new JSONArray();
		//final List<ResultDesc> resultDescs = new ArrayList<>();
        
        // fetch all test result descriptions 
        for (TestType testType : resultKeys.keySet()) {
        	final TreeSet<ResultDesc> descSet = resultKeys.get(testType);
        	
        	// fetch results to same object
            for (ResultDesc resultDesc : descSet) {
            	resultDesc.setValue(messageSource.getMessage(resultDesc.getKey(), null, resultDesc.getKey(), locale));
            }

            // another tree set for duplicate entries:
            // TODO: there must be a better solution 
            // (the issue is: compareTo() method returns differnt values depending on the .value attribute (if it's set or not))
            final TreeSet<ResultDesc> descSetNew = new TreeSet<>();
            // add fetched results to json

            for (ResultDesc desc : descSet) {
            	if (!descSetNew.contains(desc)) {
                	descSetNew.add(desc);
            	} else {
            		for (ResultDesc d : descSetNew) {
            			if (d.compareTo(desc) == 0) {
            				d.getTestResultUidList().addAll(desc.getTestResultUidList());
            			}
            		}
            	}
            }
            
            for (ResultDesc desc : descSetNew) {
            	if (desc.getValue() != null) {
            		//resultDescs.add(desc);
                    try {
						resultDescArray.put(desc.toJson());
					} catch (JSONException e) {
						// do nothing
					}
            	}	
            }
        }
        
        logger.info("testresultdetail_desc: {}", resultDescArray);//gson.toJson(resultDescs)); // TODO: gson problem? Attempted to serialize java.lang.Class: at.alladin.rmbt.shared.qos.DnsResult$DnsEntry. Forgot to register a type adapter?"
		
		return (List<Map<String, Object>>) gson.fromJson(resultDescArray.toString(), ArrayList.class);
	}
	
	/**
	 * 
	 * @param locale
	 * @return
	 */
	@Cacheable(value = "defaultCache", key = "'testresultdetail_testdesc_' + #locale.toString()") // TODO: cache!
	public List<Map<String, Object>> generateTestResultDetailTestDescription(Locale locale) {
		final List<Map<String, Object>> testResultDetailTestDescriptionList = new ArrayList<>();
		
		QosMeasurementType.CONSTANTS.forEach((k, v) -> {
			final Map<String, Object> typeMap = new HashMap<>();
			
			typeMap.put("test_type", v);
			typeMap.put("name", messageSource.getMessage(v.getNameKey(), null, v.getNameKey(), locale));
			typeMap.put("desc", messageSource.getMessage(v.getDescriptionKey(), null, v.getDescriptionKey(), locale));
			
			testResultDetailTestDescriptionList.add(typeMap);
		});
		
		return testResultDetailTestDescriptionList;
	}
	
	/**
	 * 
	 * @param locale
	 * @param measurementQos
	 * @param submitRequest
	 * @return
	 */
	public QosMeasurementResultResponse processQosMeasurements(Locale locale, MeasurementQos measurementQos, List<JsonObject> qosResultList) {
		final QosMeasurementResultResponse qosMeasurementResultResponse = new QosMeasurementResultResponse();
		
		///
		
		final List<MeasurementQosResult> measurementQosResultList = new ArrayList<>();
		measurementQos.setResults(measurementQosResultList);
		
		///
		
    	final Map<TestType, TreeSet<ResultDesc>> resultKeys = new HashMap<>();
    	
    	final AtomicLong uids = new AtomicLong(0);
    	
    	////
    	final ResultOptions resultOptions = new ResultOptions(locale);
		////
		
    	final StopWatch stopWatch = new StopWatch("evaluation");
    	stopWatch.start("testresultdetail");
    	
		qosResultList./*parallelS*/stream().forEach(qosResult -> {
			//logger.info("QOS RESULT: {}", qosResult);
			
			final JSONObject resultJson;
			try {
				resultJson = new JSONObject(qosResult.toString());
			} catch (Exception e1) {
				return;
			}
			
    		for (String excludeKey : excludeTestTypeKeys) {
    			resultJson.remove(excludeKey);
    		}
			
    		///
    		final String testTypeString = qosResult.get("test_type").getAsString();
    		//final QosMeasurementType type = QosMeasurementType.fromValue(testTypeString); // TODO: use this instead of TestType
    		final TestType testType = TestType.valueOf(testTypeString.toUpperCase());
    		
    		long qosTestId = qosResult.get("qos_test_uid").getAsLong(); // TODO: check for existence
    		
    		final QosMeasurementObjective currentObjective = findObjectiveByQosTestUid(qosTestId);
    		///
    		
			final QoSTestResult testResult = new QoSTestResult();
			
    		testResult.setResults(resultJson.toString());
    		testResult.setTestType(testTypeString);
    		
    		testResult.setUid(uids.incrementAndGet());
            testResult.setQoSTestObjectiveId(qosTestId);
            
            //
    		testResult.setTestDescription(currentObjective.getDescription());
    		testResult.setTestSummary(currentObjective.getSummary());
    		testResult.setExpectedResults(new JSONArray(currentObjective.getResults()));
    		//
    		
    		final JSONObject resultJson2;
			try {
				resultJson2 = new JSONObject(qosResult.toString());
			} catch (Exception e1) {
				return;
			}
    		
    		final AbstractResult result = QoSUtil.HSTORE_PARSER.fromJSON(resultJson2, testType.getClazz()); //gson.fromJson(qosResult, AbstractResult.class);
    		
    		result.setResultJson(resultJson);
    		
        	testResult.setResult(result);

    		int successCount = 0;
			int failureCount = 0;
    		
    		try {
				QoSUtil.compareTestResults(testResult, result, resultKeys, testType, resultOptions);
				
				successCount = testResult.getSuccessCounter();
				failureCount = testResult.getFailureCounter();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
			//////////////////////////////////////////////////////
			
			final MeasurementQosResult measurementQosResult = new MeasurementQosResult();
			measurementQosResult.setType(QosMeasurementType.fromValue(qosResult.get("test_type").getAsString())); // TODO
			measurementQosResult.setObjectiveId(qosResult.get("qos_test_uid").getAsInt()); // TODO
			
			measurementQosResult.setResult(new JsonParser().parse(resultJson.toString()).getAsJsonObject()); // TODO!!!
			
			measurementQosResult.setSuccessCount(successCount);
			measurementQosResult.setFailureCount(failureCount);
			
			measurementQosResult.setResultKeyMap(testResult.getResultKeyMap());
			
			///

			final String localizedDescription = messageSource.getMessage(testResult.getTestDescription(),
					null, testResult.getTestDescription(), locale);
			final String description = String.valueOf(TestScriptInterpreter.interprete(localizedDescription, QoSUtil.HSTORE_PARSER, testResult.getResult(), true, resultOptions)); // TODO: cache?
			
			final String localizedSummary = messageSource.getMessage(testResult.getTestSummary(), null,
					testResult.getTestSummary(), locale);
			final String summary = String.valueOf(TestScriptInterpreter.interprete(localizedSummary, QoSUtil.HSTORE_PARSER, testResult.getResult(), true, resultOptions)); // TODO: cache?
			
			//
			measurementQosResult.setOldUid(testResult.getUid());
			measurementQosResult.setTestDesc(description);
			measurementQosResult.setSummary(summary);
			//
			
			///
			
			//logger.info("QOS MEASUREMENT RESULT: {}", measurementQosResult);
			
			measurementQosResultList.add(measurementQosResult);
		});
		
		qosMeasurementResultResponse.setTestResultDetail(measurementQosResultList);

		//
		stopWatch.stop();
		stopWatch.start("testresultdetail_desc");
		//
		
		qosMeasurementResultResponse.setTestResultDetailDescription(generateTestResultDetailDescription(locale, resultKeys));
		
        //
        stopWatch.stop();
		stopWatch.start("testresultdetail_testdesc");
        //
        
        qosMeasurementResultResponse.setTestResultDetailTestDescription(generateTestResultDetailTestDescription(locale)); // TODO: move to settings
        
        stopWatch.stop();
        
        //
        
        final Map<String, Object> evalTimes = new HashMap<>();
		
		for (TaskInfo taskInfo : stopWatch.getTaskInfo()) {
			evalTimes.put(taskInfo.getTaskName(), taskInfo.getTimeMillis());
		}
		
		evalTimes.put("full", stopWatch.getTotalTimeMillis());
		
		///
		
		qosMeasurementResultResponse.setEvalTimes(evalTimes);
		qosMeasurementResultResponse.setEvaluation("Time needed to evaluate test result: " + stopWatch.getTotalTimeMillis() + " ms");
	
		return qosMeasurementResultResponse;
	}
}
