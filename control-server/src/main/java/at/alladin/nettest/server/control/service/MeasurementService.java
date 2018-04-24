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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import at.alladin.nettest.server.control.exception.NotClientsMeasurementException;
import at.alladin.nettest.server.control.exception.measurement.MeasurementNotFoundException;
import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.shared.model.MeasurementSpeedtest;
import at.alladin.nettest.shared.model.MeasurementSpeedtest.Status;
import at.alladin.nettest.shared.model.NetworkType;
import at.alladin.nettest.shared.model.NetworkType.NetworkTypeName;
import at.alladin.nettest.spring.data.couchdb.api.ViewParams;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author lb@specure.com
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Service
public class MeasurementService {

	private final Logger logger = LoggerFactory.getLogger(MeasurementService.class);
	
	/**
	 * 
	 */
	private final long OPEN_UUID_MAX_TIME_DIFFERENCE_MS = 1000*60*60*4; //4 hours
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Measurement> measurementRepository;
	
	@Inject
	private ClientService clientService;
	
	@Inject
	private TimeService timeService;
	
	@Inject
	private NetworkTypeService networkTypeService;
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Measurement findMeasurementByUuid(String uuid) {
		return measurementRepository.findOneByUuid(uuid).map(m -> {
			return m;
		}).orElseThrow(() -> new MeasurementNotFoundException("Measurement could not be found."));
	}
	
	/**
	 * 
	 * @param openTestUuid
	 * @return
	 */
	public Measurement findMeasurementByOpenTestUuid(String openTestUuid) {
		final ViewParams viewParams = new ViewParams();
		
		viewParams.setReduce(false);
		viewParams.setView("by_open_test_uuid");
		viewParams.setIncludeDocs(true);
		viewParams.setKey(openTestUuid);
				
		final List<Measurement> measurementList = measurementRepository.findByView(viewParams);
		
		if (measurementList != null && !measurementList.isEmpty()) {
			return measurementList.get(0);
		}
		
		throw new MeasurementNotFoundException();
	}
	
	/**
	 * 
	 */
	//@Async // synchronous for now, find a better solution later
	public void processMeasurement(Measurement measurement) {
		logger.info("Processing measurement /*a*/synchronous (uuid={})", measurement.getUuid());
		
		// TODO: process global values from measurement
		
		final MeasurementSpeedtest measurementSpeedtest = measurement.getSpeedtest();
		
		// process speed measurement
		if (measurementSpeedtest != null) {
			processSpeedMeasurement(measurementSpeedtest);
		}
		
//		final MeasurementQosTest measurementQosTest = measurement.getMeasurementQosTest();
//		
//		// process speed measurement
//		if (measurementQosTest != null) {
//			processQosMeasurement(measurementQosTest);
//		}
	}
	
	/**
	 * 
	 * @param measurementSpeedtest
	 */
	private void processSpeedMeasurement(MeasurementSpeedtest measurementSpeedtest) {
		
	}
	
	/**
	 * 
	 * @param measurement
	 */
	public void simulatePostgresTrigger(final Measurement measurement) {
		try {
			final boolean isUpdate = measurement.getId() != null;
			boolean testFinishedNow = false;
			
			final MeasurementSpeedtest speedtestInfo = measurement.getSpeedtest();
			
			if (isUpdate) {
				//ping median:
				long[] pings = new long[speedtestInfo.getPings().size()];
				for (int i = 0; i < speedtestInfo.getPings().size(); i++) {
					pings[i] = speedtestInfo.getPings().get(i).getValueServer();
				}		
				Arrays.sort(pings);
				if (pings.length > 0) {
					speedtestInfo.setPingMedian(pings.length % 2 == 0 ? ((pings[pings.length/2] + pings[pings.length/2-1]) / 2) : pings[pings.length/2]);
				}
				
				if (Status.STARTED.equals(speedtestInfo.getStatus())) {
					//status: FINISHED
					if (Seconds.secondsBetween(measurement.getClientInfo().getTime(), DateTime.now()).getSeconds() <= 300) {
						//difference is less than 300 seconds = 5 minutes
						speedtestInfo.setStatus(Status.FINISHED);
						testFinishedNow = true;
					}
				}

				//calculate log10 values:
		        speedtestInfo.setSpeedDownloadLog(Math.log10((double)speedtestInfo.getSpeedDownload()/10d) / 4d);
		        speedtestInfo.setSpeedUploadLog(Math.log10((double)speedtestInfo.getSpeedUpload()/10d) / 4d);
		        if (speedtestInfo.getPingMedian() != null) {
		        	speedtestInfo.setPingMedianLog(Math.log10((double)speedtestInfo.getPingMedian()/1000000d) / 3d);
		        }
		        
		        if (speedtestInfo.getPingShortest() != null) {
		        	speedtestInfo.setPingShortestLog(Math.log10((double)speedtestInfo.getPingShortest()/1000000d) / 3d);
		        }
			}
			
			//set last update timestamp
			measurement.setTimestamp(timeService.getDateTimeNow());

			//get last measurement
			final Measurement lastMeasurement =  clientService.getLastFinishedMeasurement(measurement.getClientInfo().getUuid());
			
			//trigger lines 191-201:
			//open uuid:
			if (!isUpdate) {
				//on insert:
				if (lastMeasurement != null) {
					final long timeDiffMs = measurement.getTimestamp().getMillis() - lastMeasurement.getSpeedtest().getTime().getMillis();
					if (timeDiffMs <= OPEN_UUID_MAX_TIME_DIFFERENCE_MS && lastMeasurement.getOpenUuid() != null) {
							measurement.setOpenUuid(lastMeasurement.getOpenUuid());
					}
				}
				
				if (measurement.getOpenUuid() == null) {
					measurement.setOpenUuid(UUID.randomUUID().toString());
				}
			}
						
			//trigger lines 203-215:
			if (isUpdate && testFinishedNow) {
				final NetworkType networkType = networkTypeService.getByNetworkTypeId(measurement.getNetworkInfo().getNetworkType());
				if (networkType != null) {
					measurement.getNetworkInfo().setNetworkGroupName(networkType.getGroupName());
					measurement.getNetworkInfo().setNetworkGroupType(networkType.getType());
					
					if (lastMeasurement != null) {
						final NetworkType.NetworkTypeName lastNetworkGroupType = lastMeasurement.getNetworkInfo().getNetworkGroupType();
						final NetworkType.NetworkTypeName curNetworkGroupType = measurement.getNetworkInfo().getNetworkGroupType();
						if (NetworkTypeName.WLAN.equals(lastNetworkGroupType) && !NetworkTypeName.WLAN.equals(curNetworkGroupType) || 
								!NetworkTypeName.WLAN.equals(lastNetworkGroupType) && NetworkTypeName.WLAN.equals(curNetworkGroupType)) {
							measurement.setOpenUuid(UUID.randomUUID().toString());
						}
					}
				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param uuid
	 */
	public void disassociateMeasurement(String clientUuid, String uuid) {
		final Measurement measurement = findMeasurementByUuid(uuid);
		
		if (!clientUuid.equals(measurement.getClientInfo().getUuid())) { // if it's not the measurement of the client
			throw new NotClientsMeasurementException("this measurement does not belong to your client");
		}
		
		measurement.getClientInfo().setUuid(null);
		
		// TODO: is there more to delete from the measurement?
		
		measurementRepository.save(measurement);
	}
	
	/**
	 * 
	 * @param measurementQosTest
	 */
//	private void processQosMeasurement(MeasurementQosTest measurementQosTest) {
//		
//	}
}
