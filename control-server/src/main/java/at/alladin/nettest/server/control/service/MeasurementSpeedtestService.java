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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import at.alladin.nettest.server.control.config.ControlServerProperties;
import at.alladin.nettest.server.control.exception.measurement.MeasurementInvalidException;
import at.alladin.nettest.server.control.service.GeoLocationService.LocationGraph;
import at.alladin.nettest.shared.model.GeoLocation;
import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.shared.model.MeasurementSpeedtest;
import at.alladin.nettest.shared.model.MeasurementSpeedtest.Status;
import at.alladin.nettest.shared.model.Settings;
import at.alladin.nettest.shared.model.Settings.SpeedtestDetailGroup.SpeedtestDetailGroupEntry.FormatEnum;
import at.alladin.nettest.shared.model.response.SpeedtestDetailGroupResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestDetailGroupResultResponse.SpeedtestDetailResponseGroup;
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse.SpeedtestDetailItem;
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse.SpeedtestTimeDetailItem;
import at.alladin.nettest.shared.model.response.SpeedtestResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestResultResponse.ClassifiedResultItem;
import at.alladin.nettest.shared.model.response.SpeedtestResultResponse.ResultItem;

import at.alladin.nettest.shared.server.helper.ClassificationHelper;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationItem;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationType;
import at.alladin.nettest.shared.server.helper.GroupSpeedtestDetailResultsHelper;
import at.alladin.nettest.shared.server.helper.GsonHelper;
import at.alladin.rmbt.shared.Helperfunctions;
import at.alladin.rmbt.shared.SignificantFormat;

/**
 * 
 * @author lb@specure.com
 *
 */
@Service
public class MeasurementSpeedtestService {
	
	private final Logger logger = LoggerFactory.getLogger(MeasurementSpeedtestService.class);
	
	private static final Locale STRING_FORMAT_LOCALE = Locale.US;

	@Inject
	private MessageSource messageSource;
	
	@Inject
	private SettingsService settingsService;
	
	@Inject
	private ControlServerProperties controlServerProperties;
	
	@Inject
	private GeoLocationService geoLocationService;
	
	@Inject
	private ClientService clientService;
	
	@Inject
	private ClassificationService classificationService;
		
	/**
	 * 
	 */
	private final Gson databaseGson = GsonHelper.createDatabaseGsonBuilder().create();
	
	/**
	 * 
	 * @param key
	 * @param locale
	 * @return
	 */
	private String getLocalizedMessage(final String key, final Locale locale) {
		try {
			return messageSource.getMessage(key, null, locale);
		} catch (NoSuchMessageException ex) {
			logger.error("Could not find message with key {}", key, ex);
			
			return key;
		}
	}
	
	/**
	 * 
	 * @param titleKey
	 * @param value
	 * @param locale
	 * @param includeKey
	 * @return
	 */
	private SpeedtestDetailItem generateItem(final String titleKey, final Object value, final Locale locale, final boolean includeKey) {
		final SpeedtestDetailItem item = new SpeedtestDetailItem();
		
		item.setTitle(getLocalizedMessage("key_" + titleKey, locale));
		item.setValue(String.valueOf(value));

		if (includeKey) {
			item.setKey(titleKey);
		}
		
		return item;
	}

	/**
	 * 
	 * @param resultList
	 * @param titleKey
	 * @param value
	 * @param locale
	 * @param includeKey
	 */
	private void addIfValueNotNull(final List<SpeedtestDetailItem> resultList, final String titleKey, final Object value, final Locale locale, final boolean includeKey) {
		if (value != null) {
			final SpeedtestDetailItem item = generateItem(titleKey, value, locale, includeKey);
			resultList.add(item);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @param formatEnum
	 * @param format
	 * @return
	 */
	private String formatResultValueString(final String value, final FormatEnum formatEnum, final Format format) {
		try {
			return format.format(Double.parseDouble(value) / formatEnum.getDivider());
		} catch (NumberFormatException | NullPointerException ex) {
			ex.printStackTrace();
			// do nothing with it, just return original value
		}
		
		return value;
	}
	
	/**
	 * 
	 * @param titleKey
	 * @param value
	 * @param locale
	 * @return
	 */
	private ResultItem generateResultItem(final String titleKey, final String value, final Locale locale) {
		return new ResultItem(titleKey, getLocalizedMessage(titleKey, locale), value);
	}
	
	/**
	 * 
	 * @param titleKey
	 * @param value
	 * @param classification
	 * @param locale
	 * @return
	 */
	private ClassifiedResultItem generateClassifiedResultItem(final String titleKey, final String value, final int classification, final Locale locale) {
		return new ClassifiedResultItem(titleKey, getLocalizedMessage(titleKey, locale), value, classification);
	}
	
	/**
	 * 
	 * @param titleKey
	 * @param value
	 * @param classification
	 * @param locale
	 * @return
	 */
	private ClassifiedResultItem generateClassifiedResultItem(final String titleKey, final String value, final ClassificationItem classification, final Locale locale) {
		return new ClassifiedResultItem(titleKey, getLocalizedMessage(titleKey, locale), value, classification.getClassificationNumber(), classification.getClassificationColor());
	}
	
	/**
	 * main test result (overview)
	 * @param locale
	 * @param measurement
	 * @param httpServletRequest
	 * @return
	 */
	public SpeedtestResultResponse getSpeedtestMainResult(final Locale locale, final Measurement measurement, final HttpServletRequest httpServletRequest) {
		final SpeedtestResultResponse response = new SpeedtestResultResponse();
		
		//check if client is registered
		//clientService.ensureClientRegistered(measurement.getClientInfo().getUuid()); // TODO: this is not possible if user is in anonymous mode!
		
        final String clientIpRaw = clientService.getClientIpAddressString(httpServletRequest);
        logger.info("New test result request from {}", clientIpRaw);

        final MeasurementSpeedtest speedtestInfo = measurement.getSpeedtest();
        
        if (speedtestInfo == null || !Status.FINISHED.equals(speedtestInfo.getStatus())) {
        	throw new MeasurementInvalidException("Measurement invalid.");
        }
        
    	final Settings settings = settingsService.getSettings(controlServerProperties.getSettingsKey());
    	
        final Format format = new SignificantFormat(2, locale);

        response.setOpenUuid("P" + measurement.getOpenUuid());
        
        response.setOpenTestUuid("O" + measurement.getOpenTestUuid());
        
        response.setTime(measurement.getSpeedtest().getTime().getMillis());
        response.setTimezone(measurement.getClientInfo().getTimezone());

        final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        
        dateFormat.setTimeZone(TimeZone.getTimeZone(measurement.getClientInfo().getTimezone()));
        final String timeString = dateFormat.format(measurement.getSpeedtest().getTime().toDate());
        response.setTimeString(timeString);

        final List<ClassifiedResultItem> classifiedResultList = new ArrayList<>();
        response.setClassifiedMeasurementDataList(classifiedResultList);
        
        final int networkTypeId = ClassificationHelper.getNetworkTypeId(measurement);
        
        //TODO: classifications
        final String downloadString = String.format("%s %s", format.format(speedtestInfo.getSpeedDownload() / 1000d),
        		getLocalizedMessage("RESULT_DOWNLOAD_UNIT", locale));
        classifiedResultList.add(generateClassifiedResultItem("RESULT_DOWNLOAD", downloadString, 
        		classificationService.classifyColor(ClassificationType.DOWNLOAD, speedtestInfo.getSpeedDownload(), 
        				networkTypeId), locale));
        
        final String uploadString = String.format("%s %s", format.format(speedtestInfo.getSpeedUpload() / 1000d),
        		getLocalizedMessage("RESULT_UPLOAD_UNIT", locale));
        classifiedResultList.add(generateClassifiedResultItem("RESULT_UPLOAD", uploadString, 
        		classificationService.classifyColor(ClassificationType.UPLOAD, speedtestInfo.getSpeedUpload(), 
        				networkTypeId), locale));
        String pingString = new String();
        if (speedtestInfo.getPingMedian() != null) {
	        pingString = String.format("%s %s", format.format(speedtestInfo.getPingMedian() / 1000000d),
	        		getLocalizedMessage("RESULT_PING_UNIT", locale));
	        classifiedResultList.add(generateClassifiedResultItem("RESULT_PING", pingString, 
	        		classificationService.classifyColor(ClassificationType.PING, speedtestInfo.getPingMedian() / 1000000,
	        				networkTypeId), locale));
        }
        
        String signalString = "";
        
        if (measurement.getSignalStrength() != null || measurement.getLteRsrp() != null) {
        	if (measurement.getLteRsrp() == null) {
//        		final ClassificationType classificationType = 
//        				measurement.getNetworkInfo().getNetworkType() == 99 || measurement.getNetworkInfo().getNetworkType() == 0 ? 
//        				ClassificationType.SIGNAL_WIFI : ClassificationType.SIGNAL_MOBILE;
        		
        		signalString = String.format("%s %s", measurement.getSignalStrength(), getLocalizedMessage("RESULT_SIGNAL_UNIT", locale));
                classifiedResultList.add(generateClassifiedResultItem("RESULT_SIGNAL", signalString, 
                		classificationService.classifyColor(ClassificationType.SIGNAL, measurement.getSignalStrength(), networkTypeId), locale));
        	}
        	else {
        		signalString = String.format("%s %s", measurement.getLteRsrp(), getLocalizedMessage("RESULT_SIGNAL_UNIT", locale));
                
        		classifiedResultList.add(generateClassifiedResultItem("RESULT_SIGNAL_RSRP", signalString, 
                		classificationService.classifyColor(ClassificationType.SIGNAL, measurement.getLteRsrp(), networkTypeId), locale));
        	}
        }
        
        final List<ResultItem> networkResultList = new ArrayList<>();
        response.setNetworkDetailList(networkResultList);
        
        final String networkTypeString = Helperfunctions.getNetworkTypeName(measurement.getNetworkInfo().getNetworkType());
        networkResultList.add(generateResultItem("RESULT_NETWORK_TYPE", networkTypeString, locale));
        
        if (measurement.getNetworkInfo().getNetworkType() == 98 || 
        		measurement.getNetworkInfo().getNetworkType() == 99) {
        	//mobile wifi or browser
        	if (measurement.getNetworkInfo().getProvider() != null && 
        			measurement.getNetworkInfo().getProvider().getName() != null) {
                networkResultList.add(generateResultItem("RESULT_OPERATOR_NAME", 
                		measurement.getNetworkInfo().getProvider().getName(), locale));
        	}
        	
        	if (measurement.getNetworkInfo().getNetworkType() == 99 &&
        			measurement.getWifiInfo() != null && measurement.getWifiInfo().getSsid() != null) {
        		networkResultList.add(generateResultItem("RESULT_WIFI_SSID", 
        				measurement.getWifiInfo().getSsid(), locale));                        		
        	}
        }
        else {
        	//mobile
        	if (measurement.getMobileNetworkInfo() != null && 
        			measurement.getMobileNetworkInfo().getOperatorName() != null) {
        		networkResultList.add(generateResultItem("RESULT_OPERATOR_NAME", 
        				measurement.getMobileNetworkInfo().getOperatorName(), locale));
        	}
        	
        	if (measurement.getMobileNetworkInfo().getRoamingType() != null &&
        			measurement.getMobileNetworkInfo().getRoamingType() > 0) {
        		networkResultList.add(generateResultItem("RESULT_ROAMING", 
        				getRoamingType(messageSource, measurement.getMobileNetworkInfo().getRoamingType(), locale), locale));
        	}
        }

        if (measurement.getLocations() != null && measurement.getLocations().size() > 0) {
        	final GeoLocation location = measurement.getLocations().get(0);
        	
        	if (location.getLatitude() != null && location.getLongitude() != null && location.getAccuracy() != null) {
        		if (location.getAccuracy() < 100000) { //TODO: rmbt_geo_accuracy_button_limit
        			response.setLatitude(location.getLatitude());
        			response.setLongitude(location.getLongitude());
        		}
        	}
        	
        	try {
        		final String geoLocationString = getGeoLocation(settings, locale, location);
        		if (geoLocationString != null) {
        			response.setLocation(geoLocationString);
        		}
        	} catch (JSONException ex) {
        		// do nothing
        	}
        }
        
        String providerString = "";
        if (measurement.getNetworkInfo().getProvider() != null &&
        		measurement.getNetworkInfo().getProvider().getName() != null) {
        	providerString = measurement.getNetworkInfo().getProvider().getName();
        }
        
        String platformString = "";
        if (measurement.getDeviceInfo().getPlatform() != null) {
        	platformString = measurement.getDeviceInfo().getPlatform(); 
        }

        
        String modelString = "";
        if (measurement.getDeviceInfo().getFullname() != null) {
        	modelString = measurement.getDeviceInfo().getFullname(); 
        }
        
        String mobileNetworkString = "";
        if (measurement.getMobileNetworkInfo() != null && measurement.getMobileNetworkInfo().getOperatorName() != null) {
        	if (measurement.getMobileNetworkInfo().getMobileProvider() != null) {
        		String.format("%s (%s)", measurement.getMobileNetworkInfo().getMobileProvider(), measurement.getMobileNetworkInfo().getOperatorName());
        	}
        	else {
        		mobileNetworkString = measurement.getMobileNetworkInfo().getOperatorName();
        	}
        }
        
 
        String shareTextField4 = "";
        if (signalString != null) {
        	if (measurement.getLteRsrp() == null) {
        		shareTextField4 = MessageFormat.format(
        				getLocalizedMessage("RESULT_SHARE_TEXT_SIGNAL_ADD", locale), signalString);
        	}
        	else {
        		shareTextField4 = MessageFormat.format(
        				getLocalizedMessage("RESULT_SHARE_TEXT_RSRP_ADD", locale), signalString);
        	}
        }

        String shareLocation = "";
        if (response.getLatitude() != null && response.getLocation() != null) {
        	shareTextField4 = MessageFormat.format(
        			getLocalizedMessage("RESULT_SHARE_TEXT_LOCATION_ADD", locale), response.getLocation());
        }

        final String shareText = messageSource.getMessage("RESULT_SHARE_TEXT",
        		new Object[] {
        				timeString,
        				downloadString,
        				uploadString,
        				pingString,
        				shareTextField4,
        				networkTypeString,
        				providerString.isEmpty() ? "" : MessageFormat.format(getLocalizedMessage("RESULT_SHARE_TEXT_PROVIDER_ADD", locale), providerString),
        				mobileNetworkString == null ? "" : MessageFormat.format(getLocalizedMessage("RESULT_SHARE_TEXT_MOBILE_ADD", locale), mobileNetworkString),
        				platformString,
        				modelString,
        				shareLocation,
        				response.getOpenTestUuid() //TODO: settings + url_open_data_prefix
        		},
        		"RESULT_SHARE_TEXT",
        		locale);
        
        response.setShareText(shareText);
        response.setShareSubject(messageSource.getMessage("RESULT_SHARE_SUBJECT", new Object[] {timeString}, "RESULT_SHARE_SUBJECT", locale));
        
        response.setNetworkType(measurement.getNetworkInfo().getNetworkType());
        
        response.setQosResultAvailable(measurement.getQos() != null);
        
        return response;
	}
	
	/**
	 * test details result (details request)
	 * @param locale
	 * @param measurement
	 * @param includeKeys
	 * @return
	 */
	public SpeedtestDetailResultResponse getSpeedtestDetailResult(final Locale locale, final Measurement measurement, boolean includeKeys) {
		final SpeedtestDetailResultResponse result = new SpeedtestDetailResultResponse();
		final List<SpeedtestDetailItem> resultList = new ArrayList<>(); 
		result.setTestResultDetailList(resultList);
		
		final Settings settings = settingsService.getSettings(controlServerProperties.getSettingsKey());

		final Format format = new SignificantFormat(2, locale);

		final MeasurementSpeedtest measurementSpeedtest = measurement.getSpeedtest();
		
		final SpeedtestDetailItem item = generateItem("time", measurementSpeedtest.getTime().toString(), locale, includeKeys);

		final SpeedtestTimeDetailItem timeItem = new SpeedtestTimeDetailItem();
		timeItem.setKey(item.getKey());
		timeItem.setTitle(item.getTitle());
		timeItem.setValue(item.getValue());
		timeItem.setTime(measurement.getSpeedtest().getTime().getMillis());
		timeItem.setTimezone(measurement.getSpeedtest().getTime().getZone().getID());
		
		resultList.add(timeItem);

		final TimeZone tz = TimeZone.getTimeZone(timeItem.getTimezone());
		final Format tzFormat = new DecimalFormat("+0.##;-0.##", new DecimalFormatSymbols(locale));
		final float offset = tz.getOffset(timeItem.getTime()) / 1000f / 60f / 60f;
		resultList.add(generateItem("timezone", String.format("UTC%sh", tzFormat.format(offset)), locale, includeKeys));

		resultList.add(generateItem("speed_download", 
				String.format("%s %s", format.format(measurementSpeedtest.getSpeedDownload() / 1000d), 
						getLocalizedMessage("RESULT_DOWNLOAD_UNIT", locale)), locale, includeKeys));

		resultList.add(generateItem("speed_upload", 
				String.format("%s %s", format.format(measurementSpeedtest.getSpeedUpload() / 1000d), 
						getLocalizedMessage("RESULT_UPLOAD_UNIT", locale)), locale, includeKeys));
		
		resultList.add(generateItem("ping_median", 
				String.format("%s %s", format.format(measurementSpeedtest.getPingMedian() / 1000000d), 
						getLocalizedMessage("RESULT_PING_UNIT", locale)), locale, includeKeys));

		resultList.add(generateItem("ping_variance", 
				String.format("%s %s", format.format(measurementSpeedtest.getPingVariance() / 1000000000000d), 
						getLocalizedMessage("RESULT_PING_UNIT", locale)), locale, includeKeys));

		if (measurement.getSignalStrength() != null) {
			resultList.add(generateItem("signal_strength", 
					String.format("%d %s", measurement.getSignalStrength(), 
							getLocalizedMessage("RESULT_SIGNAL_UNIT", locale)), locale, includeKeys));
		}

		if (measurement.getLteRsrp() != null) {
			resultList.add(generateItem("signal_rsrp", 
					String.format("%d %s", measurement.getLteRsrp(), 
							getLocalizedMessage("RESULT_SIGNAL_UNIT", locale)), locale, includeKeys));
		}

		if (measurement.getLteRsrq() != null) {
			resultList.add(generateItem("signal_rsrq", 
					String.format("%d %s", measurement.getLteRsrq(), 
							getLocalizedMessage("RESULT_DB_UNIT", locale)), locale, includeKeys));
		}
                     
		if (measurement.getNetworkInfo().getNetworkType() != null) {
			resultList.add(generateItem("network_type",
					Helperfunctions.getNetworkTypeName(measurement.getNetworkInfo().getNetworkType()), locale, includeKeys));
		}

		if (measurement.getLocations() != null && measurement.getLocations().size() > 0) {
			try {
				addIfValueNotNull(resultList, "location", getGeoLocation(settings, locale, measurement.getLocations().get(0)), locale, includeKeys);
			} catch (JSONException ex) {
        		// do nothing
        	}
				
			double heading = 0d, altitude = 0d;
			
			for(final GeoLocation l : measurement.getLocations()) {
				if (l.getAltitude() != null && altitude <= 0d && l.getAltitude() > 0d) {
					altitude = l.getAltitude();
				}
				if (l.getHeading() != null && heading <= 0d && l.getHeading() > 0d) {
					heading = l.getHeading();
				}
			}
			
			if (heading > 0d) {
				addIfValueNotNull(resultList, "heading", String.format(STRING_FORMAT_LOCALE, "%.1f°", heading), locale, includeKeys);
			}
			
			if (altitude > 0d) {
				addIfValueNotNull(resultList, "altitude", String.format(STRING_FORMAT_LOCALE, "%.1f m", altitude), locale, includeKeys);
			}
		}
		
		//TODO: country_location (trigger)
//                        	if (locationJson.has("country_location")) {
//                        		jsonUtil.addString(resultList, "country_location", locationJson.getString("country_location"));
//                        	}
		//TODO: motion
//                        	if (locationJson.has("motion")) {
//                        		jsonUtil.addString(resultList, "motion", locationJson.getString("motion"));
//                        	}                        	
//                        }
//
		addIfValueNotNull(resultList, "country_asn", measurement.getNetworkInfo().getCountryAsn(), locale, includeKeys);

		addIfValueNotNull(resultList, "country_geoip", measurement.getNetworkInfo().getCountryGeoip(), locale, includeKeys);

		if (measurement.getZipCode() != null && measurement.getZipCode() > 999 && measurement.getZipCode() <= 9999) {
			resultList.add(generateItem("zip_code", measurement.getZipCode(), locale, includeKeys));
		}
		//TODO data:
//                        final Field dataField = test.getField("data");
//                        if (! Strings.isNullOrEmpty(dataField.toString()))
//                        {
//                            final JSONObject data = new JSONObject(dataField.toString());
//                            
//                            if (data.has("region"))
//                            	jsonUtil.addString(resultList, "region", data.getString("region"));
//                            if (data.has("municipality"))
//                            	jsonUtil.addString(resultList, "municipality", data.getString("municipality"));
//                            if (data.has("settlement"))
//                                jsonUtil.addString(resultList, "settlement", data.getString("settlement"));
//                            if (data.has("whitespace"))
//                                jsonUtil.addString(resultList, "whitespace", data.getString("whitespace"));
//                            
//                            if (data.has("cell_id"))
//                                jsonUtil.addString(resultList, "cell_id", data.getString("cell_id"));
//                            if (data.has("cell_name"))
//                                jsonUtil.addString(resultList, "cell_name", data.getString("cell_name"));
//                            if (data.has("cell_id_multiple") && data.getBoolean("cell_id_multiple"))
//                                jsonUtil.addString(resultList, "cell_id_multiple", jsonUtil.getTranslation("value" , "cell_id_multiple"));
//                        }
//                        
		resultList.add(generateItem("speed_test_duration", 
				String.format("%s %s", format.format(measurementSpeedtest.getTotalDurationNs() / (1000d * 1000d)), 
						getLocalizedMessage("RESULT_DURATION_UNIT", locale)), locale, includeKeys));
		
		addIfValueNotNull(resultList, "client_public_ip", measurement.getNetworkInfo().getClientPublicIp(), locale, includeKeys);

		addIfValueNotNull(resultList, "client_public_ip_as", measurement.getNetworkInfo().getPublicIpAsn(), locale, includeKeys);

		addIfValueNotNull(resultList, "client_public_ip_as_name", measurement.getNetworkInfo().getPublicIpAsName(), locale, includeKeys);

		addIfValueNotNull(resultList, "client_public_ip_rdns", measurement.getNetworkInfo().getPublicIpRdns(), locale, includeKeys);

		//TODO provider
		//addIfValueNotNull(resultList, "provider", , locale, includeKeys);
//                        // operator - derived from provider_id (only for pre-defined operators)
//                        //TODO replace provider-information by more generic information
//                        jsonUtil.addString(resultList, "provider", test.getField("provider_id_name"));
//                        
		addIfValueNotNull(resultList, "client_local_ip", measurement.getClientInfo().getIpLocalType(), locale, includeKeys);

		addIfValueNotNull(resultList, "nat_type", measurement.getNetworkInfo().getNatType(), locale, includeKeys);

		addIfValueNotNull(resultList, "wifi_ssid", measurement.getWifiInfo().getSsid(), locale, includeKeys);

		addIfValueNotNull(resultList, "wifi_bssid", measurement.getWifiInfo().getBssid(), locale, includeKeys);

		if (measurement.getWifiLinkSpeed() != null) {
			resultList.add(generateItem("wifi_link_speed", 
					String.format("%s %s", measurement.getWifiLinkSpeed() / 1000d, 
							getLocalizedMessage("RESULT_WIFI_LINK_SPEED_UNIT", locale)), locale, includeKeys));
		}

		if (measurement.getMobileNetworkInfo() != null) {
			addIfValueNotNull(resultList, "network_operator_name", measurement.getMobileNetworkInfo().getOperatorName(), locale, includeKeys);

			if (measurement.getMobileNetworkInfo().getMobileProvider() == null) {
				addIfValueNotNull(resultList, "network_operator", measurement.getMobileNetworkInfo().getOperator(), locale, includeKeys);	
			}
			else {
				if (measurement.getMobileNetworkInfo().getOperator() == null) {
					addIfValueNotNull(resultList, "network_operator", measurement.getMobileNetworkInfo().getMobileProvider().getName(), locale, includeKeys);	
				}
				else {
					addIfValueNotNull(resultList, "network_operator",
							String.format("%s (%s)", measurement.getMobileNetworkInfo().getMobileProvider().getName(),
									measurement.getMobileNetworkInfo().getOperatorName()), locale, includeKeys);
				}
			}
			
			addIfValueNotNull(resultList, "network_sim_operator_name", measurement.getMobileNetworkInfo().getSimOperatorName(), locale, includeKeys);

			//TODO netrwork_sim_operator....
//                        final Field networkSimOperatorField = test.getField("network_sim_operator");
//                        final Field networkSimOperatorTextField = test.getField("network_sim_operator_mcc_mnc_text");
//                        if (networkSimOperatorTextField.isNull())
//                            jsonUtil.addString(resultList, "network_sim_operator", networkSimOperatorField);
//                        else
//                            jsonUtil.addString(resultList, "network_sim_operator",
//                                    String.format("%s (%s)", networkSimOperatorTextField, networkSimOperatorField));
//                        
			if (measurement.getMobileNetworkInfo().getRoamingType() != null) {
				addIfValueNotNull(resultList, "roaming", getRoamingType(messageSource, 
						measurement.getMobileNetworkInfo().getRoamingType(), locale), locale, includeKeys);
			}
		}
                        
		final long totalBytes = measurementSpeedtest.getTotalBytesDownload() + measurementSpeedtest.getTotalBytesUpload();
		if (totalBytes > 0) {
			addIfValueNotNull(resultList, "total_bytes", String.format("%s %s", format.format(totalBytes / (1000d * 1000d)), 
					getLocalizedMessage("RESULT_TOTAL_BYTES_UNIT", locale)), locale, includeKeys);
		}

		final long totalIfBytes = measurementSpeedtest.getInterfaceTotalBytesDownload() + measurementSpeedtest.getInterfaceTotalBytesUpload();
		if (totalIfBytes > 0) {
			addIfValueNotNull(resultList, "total_if_bytes", String.format("%s %s", format.format(totalIfBytes / (1000d * 1000d)), 
					getLocalizedMessage("RESULT_TOTAL_BYTES_UNIT", locale)), locale, includeKeys);				
		}
		
		if (measurementSpeedtest.getInterfaceDltestBytesDownload() > 0) {
			addIfValueNotNull(resultList, "testdl_if_bytes_download", 
					String.format("%s %s", format.format(measurementSpeedtest.getInterfaceDltestBytesDownload() / (1000d * 1000d)), 
							getLocalizedMessage("RESULT_TOTAL_BYTES_UNIT", locale)), locale, includeKeys);
		}

		if (measurementSpeedtest.getInterfaceDltestBytesUpload() > 0) {
			addIfValueNotNull(resultList, "testdl_if_bytes_upload", 
					String.format("%s %s", format.format(measurementSpeedtest.getInterfaceDltestBytesUpload() / (1000d * 1000d)), 
							getLocalizedMessage("RESULT_TOTAL_BYTES_UNIT", locale)), locale, includeKeys);
		}

		if (measurementSpeedtest.getInterfaceUltestBytesUpload() > 0) {
			addIfValueNotNull(resultList, "testul_if_bytes_upload", 
					String.format("%s %s", format.format(measurementSpeedtest.getInterfaceUltestBytesUpload() / (1000d * 1000d)), 
							getLocalizedMessage("RESULT_TOTAL_BYTES_UNIT", locale)), locale, includeKeys);
		}
		
		if (measurementSpeedtest.getInterfaceUltestBytesDownload() > 0) {
			addIfValueNotNull(resultList, "testul_if_bytes_download", 
					String.format("%s %s", format.format(measurementSpeedtest.getInterfaceUltestBytesDownload() / (1000d * 1000d)), 
							getLocalizedMessage("RESULT_TOTAL_BYTES_UNIT", locale)), locale, includeKeys);
		}

		if (measurementSpeedtest.getRelativeTimeDlNs() > 0) {
			addIfValueNotNull(resultList, "time_dl", 
					String.format("%s %s", format.format(measurementSpeedtest.getRelativeTimeDlNs() / 1000000000d), 
							getLocalizedMessage("RESULT_DURATION_UNIT", locale)), locale, includeKeys);		
		}

		if (measurementSpeedtest.getDurationDownloadNs() > 0) {
			addIfValueNotNull(resultList, "duration_dl", 
					String.format("%s %s", format.format(measurementSpeedtest.getDurationDownloadNs() / 1000000000d), 
							getLocalizedMessage("RESULT_DURATION_UNIT", locale)), locale, includeKeys);		
		}

		if (measurementSpeedtest.getRelativeTimeUlNs() > 0) {
			addIfValueNotNull(resultList, "time_ul", 
					String.format("%s %s", format.format(measurementSpeedtest.getRelativeTimeUlNs() / 1000000000d), 
							getLocalizedMessage("RESULT_DURATION_UNIT", locale)), locale, includeKeys);		
		}

		if (measurementSpeedtest.getDurationUploadNs() > 0) {
			addIfValueNotNull(resultList, "duration_ul", 
					String.format("%s %s", format.format(measurementSpeedtest.getDurationUploadNs() / 1000000000d), 
							getLocalizedMessage("RESULT_DURATION_UNIT", locale)), locale, includeKeys);		
		}

		addIfValueNotNull(resultList, "server_name", measurementSpeedtest.getTargetMeasurementServer().getName(), locale, includeKeys);		
		
		addIfValueNotNull(resultList, "plattform", measurement.getDeviceInfo().getPlatform(), locale, includeKeys);

		addIfValueNotNull(resultList, "os_version", measurement.getDeviceInfo().getOsVersion(), locale, includeKeys);
		
		addIfValueNotNull(resultList, "model", measurement.getDeviceInfo().getFullname(), locale, includeKeys);

		addIfValueNotNull(resultList, "client_name", measurement.getClientInfo().getName(), locale, includeKeys);

		addIfValueNotNull(resultList, "client_software_version", measurement.getClientInfo().getSoftwareVersion(), locale, includeKeys);
                        
		if (measurementSpeedtest.getEncryption() != null && !measurementSpeedtest.getEncryption().isEmpty()) {
			addIfValueNotNull(resultList, "encryption", ("NONE".equals(measurementSpeedtest.getEncryption()) ? 
					getLocalizedMessage("key_encryption_false", locale) : 
						getLocalizedMessage("key_encryption_true", locale)), locale, includeKeys);
		}

		addIfValueNotNull(resultList, "client_version", measurement.getClientInfo().getVersion(), locale, includeKeys);		

		addIfValueNotNull(resultList, "duration", String.format("%d %s", measurementSpeedtest.getDuration(),
				getLocalizedMessage("RESULT_DURATION_UNIT", locale)), locale, includeKeys);

		addIfValueNotNull(resultList, "num_threads", measurementSpeedtest.getNumThreads(), locale, includeKeys);
		
		addIfValueNotNull(resultList, "num_threads_ul", measurementSpeedtest.getNumThreadsUl(), locale, includeKeys);

		if (measurement.getOpenTestUuid() != null) {
			addIfValueNotNull(resultList, "open_test_uuid", "O" + measurement.getOpenTestUuid(), locale, includeKeys);
		}

		if (measurement.getOpenUuid() != null) {
			addIfValueNotNull(resultList, "open_uuid", "P" + measurement.getOpenUuid(), locale, includeKeys);
		}

		addIfValueNotNull(resultList, "tag", measurement.getTag(), locale, includeKeys);


		final LocationGraph locGraph = geoLocationService.getLocationGraph(measurement, measurementSpeedtest.getTime().getMillis());
		
    	if (locGraph != null && (locGraph.getTotalDistance() > 0) &&
    	        locGraph.getTotalDistance() <= settings.getRmbt().getGeoDistanceDetailLimit()) {
    		addIfValueNotNull(resultList, "motion", Math.round(locGraph.getTotalDistance()) + " m", locale, includeKeys);
    	}

		//TODO NDT?
//      if (ndt != null)
//      {
//          final String downloadNdt = format.format(ndt.getField("s2cspd").doubleValue());
//          jsonUtil.addString(resultList, "speed_download_ndt",
//                  String.format("%s %s", downloadNdt, labels.getString("RESULT_DOWNLOAD_UNIT")));
//          
//          final String uploaddNdt = format.format(ndt.getField("c2sspd").doubleValue());
//          jsonUtil.addString(resultList, "speed_upload_ndt",
//                  String.format("%s %s", uploaddNdt, labels.getString("RESULT_UPLOAD_UNIT")));
//          
//          // final String pingNdt =
//          // format.format(ndt.getField("avgrtt").doubleValue());
//          // jsonUtil.addString(resultList, "ping_ndt",
//          // String.format("%s %s", pingNdt,
//          // labels.getString("RESULT_PING_UNIT")));
//      }

//                        if (ndt != null)
//                        {
//                            jsonUtil.addString(resultList, "ndt_details_main", ndt.getField("main"));
//                            jsonUtil.addString(resultList, "ndt_details_stat", ndt.getField("stat"));
//                            jsonUtil.addString(resultList, "ndt_details_diag", ndt.getField("diag"));
//                        }
//                        
		//TODO: F2 advertised speed options
//                        AdvertisedSpeedOptionUtil.expandWithAdvertisedSpeedStatus(jsonUtil, this, test, resultList, format);
//                       
//                        
		//TODO: additional report fields...
//                        final JsonField reportField = (JsonField) test.getField("additional_report_fields");
//                        if (reportField != null) {
//                        	final JSONObject reportJson = new JSONObject(reportField.toString());
//                        	final JSONObject resultReportJson = new JSONObject();
//                        	final DecimalFormat fmt = (DecimalFormat) DecimalFormat.getInstance(locale);
//                        	fmt.setMaximumFractionDigits(2);
//                        	
//                        	double peak = reportJson.optDouble("peak_up_kbit");
//                        	if (peak > 0) {
//                        		resultReportJson.put("peak_up_kbit", String.format("%s %s", fmt.format(peak / 1000d),
//                        					labels.getString("RESULT_UPLOAD_UNIT")));
//                        	}
//
//                        	peak = reportJson.optDouble("peak_down_kbit");
//                        	if (peak > 0) {
//                        		resultReportJson.put("peak_down_kbit", String.format("%s %s", fmt.format(peak / 1000d),
//                        					labels.getString("RESULT_DOWNLOAD_UNIT")));
//                        	}
//
//                       		jsonUtil.addJSONObject(resultList, "additional_report_fields", resultReportJson);
//                        }                        
                        
		return result;
	}
	
	/**
	 * Takes a Measurement object, groups desired values together and does formatting
	 * @param locale Locale to be translated to
	 * @param measurement the measurement to be displayed in groups
	 * @return
	 * @throws JSONException
	 */
	public SpeedtestDetailGroupResultResponse getSpeedtestDetailGroupResult(final Locale locale, final Measurement measurement, boolean includeKeys) {
		final Settings settings = settingsService.getSettings( controlServerProperties.getSettingsKey());
		final JsonObject settingsJson = databaseGson.fromJson(databaseGson.toJson(settings, Settings.class), JsonObject.class);
		
		final SpeedtestDetailGroupResultResponse response = GroupSpeedtestDetailResultsHelper.groupResult(measurement, settingsJson.getAsJsonArray("speedtestDetailGroups"), 
				locale, settings.getRmbt().getGeoAccuracyDetailLimit(), databaseGson);
		
		//localise the grouped results
		for(SpeedtestDetailResponseGroup group : response.getSpeedtestDetailGroups()) {
    		group.setTitle(getLocalizedMessage(group.getTitle(), locale));
    		for(SpeedtestDetailItem item : group.getEntries()) {
    			if(item.getTitle().equals("key_location")) {
    				final String geoKey = item.getValue().substring(item.getValue().indexOf("(") + 1).split(",")[0];
    				item.setValue(item.getValue().replace(geoKey, getLocalizedMessage(geoKey, locale)));
    			} else if (item.getValue().startsWith("key_")) {
    				item.setValue(getLocalizedMessage(item.getValue(), locale));
    			}
    			item.setTitle(getLocalizedMessage(item.getTitle(), locale));
    			if(item.getUnit() != null) {
    				item.setValue(item.getValue() + " " + getLocalizedMessage(item.getUnit(), locale));
    				//no need to forward the unit to the frontend
    				item.setUnit(null);
    			}
    		}
    		 
    	}
		
		return response;
	}
	
	/**
	 * 
	 * @param settings
	 * @param locale
	 * @param location
	 * @return
	 * @throws JSONException
	 */
    public String getGeoLocation(final Settings settings, Locale locale, GeoLocation location) throws JSONException {
        // geo-location
    	
    	final Double latitude = location.getLatitude();
    	final Double longitude = location.getLongitude();
    	final Double accuracy = location.getAccuracy();
    	final Double altitude = location.getAltitude();
    	
        if (location != null && latitude != null && longitude != null && accuracy != null) {
            if (accuracy < settings.getRmbt().getGeoAccuracyDetailLimit()) {
                
            	final StringBuilder geoString = new StringBuilder(Helperfunctions.geoToString(latitude, longitude));
                geoString.append(" (");
                
                String provider = location.getProvider();
                
                if (provider != null) {
                	
                	provider = provider.toUpperCase();
                	String key = null;
                	
                	switch(provider) {
	                	case "GPS":
	            			key  = "key_geo_source_gps";
	            			break;
	                	case "BROWSER":
	            			key  = "key_geo_source_browser";
	            			break;
	            		case "NETWORK":
                		default:
                			key = "key_geo_source_network";
                	}
                	
                    geoString.append(messageSource.getMessage(key, null, key, locale));
                    geoString.append(", ");
                }
                
                geoString.append(String.format(STRING_FORMAT_LOCALE, "+/- %.0f m", accuracy));

                if (altitude != null) {
                	geoString.append(String.format(STRING_FORMAT_LOCALE, ", alt: %.0f m", altitude));
                }

                geoString.append(")");
                
                return geoString.toString();                
            }
            
            // country derived from location
//            final Field countryLocationField = test.getField("country_location"); 
//            if (!countryLocationField.isNull()) {
//                json.put("country_location", countryLocationField.toString());
//            }
        }
        
        return null;
    }
    
    public String getRoamingType(final MessageSource labels, final int roamingType, final Locale locale)
    {
        final String roamingValue;
        switch (roamingType)
        {
        case 0:
            roamingValue = labels.getMessage("value_roaming_none", null, "value_roaming_none", locale);
            break;
        case 1:
            roamingValue = labels.getMessage("value_roaming_national", null, "value_roaming_national", locale);
            break;
        case 2:
            roamingValue = labels.getMessage("value_roaming_international", null, "value_roaming_international", locale);
            break;
        default:
            roamingValue = "?";
            break;
        }
        return roamingValue;
    }
    
    
}
