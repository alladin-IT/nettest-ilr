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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.InetAddresses;
import at.alladin.nettest.server.control.config.ControlServerProperties;
import at.alladin.nettest.server.control.exception.UnsupportedClientNameException;
import at.alladin.nettest.server.control.exception.UnsupportedClientVersionException;
import at.alladin.nettest.server.control.exception.measurement.MeasurementInvalidNetworkTypeException;
import at.alladin.nettest.server.control.exception.measurement.MeasurementInvalidSpeedException;
import at.alladin.nettest.server.control.exception.measurement.MeasurementServerNotFoundException;
import at.alladin.nettest.server.control.exception.measurement.MeasurementTestTokenInvalidException;
import at.alladin.nettest.server.control.service.ClientService;
import at.alladin.nettest.server.control.service.DeviceService;
import at.alladin.nettest.server.control.service.GeoLocationService;
import at.alladin.nettest.server.control.service.MeasurementServerService;
import at.alladin.nettest.server.control.service.MeasurementService;
import at.alladin.nettest.server.control.service.MeasurementSpeedtestService;
import at.alladin.nettest.server.control.service.ProfileService;
import at.alladin.nettest.server.control.service.ProviderService;
import at.alladin.nettest.server.control.service.PurgeService;
import at.alladin.nettest.server.control.service.SettingsService;
import at.alladin.nettest.server.control.service.TimeService;
import at.alladin.nettest.server.control.util.MeasurementUtil;
import at.alladin.nettest.server.control.util.TestTokenUtil;
import at.alladin.nettest.server.control.util.TestTokenUtil.TestToken;
import at.alladin.nettest.shared.model.Client.ClientType;
import at.alladin.nettest.shared.model.Device;
import at.alladin.nettest.shared.model.GeoLocation;
import at.alladin.nettest.shared.model.MccMnc;
import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.shared.model.MeasurementClientInfo;
import at.alladin.nettest.shared.model.MeasurementDeviceInfo;
import at.alladin.nettest.shared.model.MeasurementMobileNetworkInfo;
import at.alladin.nettest.shared.model.MeasurementNetworkInfo;
import at.alladin.nettest.shared.model.MeasurementServer;
import at.alladin.nettest.shared.model.MeasurementSpeedRaw;
import at.alladin.nettest.shared.model.MeasurementSpeedRawItem;
import at.alladin.nettest.shared.model.MeasurementSpeedtest;
import at.alladin.nettest.shared.model.MeasurementSpeedtest.Status;
import at.alladin.nettest.shared.model.MeasurementWifiInfo;
import at.alladin.nettest.shared.model.Ping;
import at.alladin.nettest.shared.model.Provider;
import at.alladin.nettest.shared.model.Settings;
import at.alladin.nettest.shared.model.Settings.RmbtSettings;
import at.alladin.nettest.shared.model.Signal;
import at.alladin.nettest.shared.model.exception.IllegalMccMncException;
import at.alladin.nettest.shared.model.request.MeasurementRequest;
import at.alladin.nettest.shared.model.request.SpeedtestResultSubmitRequest;
import at.alladin.nettest.shared.model.request.SpeedtestResultSubmitRequest.SpeedRawItem;
import at.alladin.nettest.shared.model.request.SpeedtestResultSubmitRequest.SpeedRawItem.SpeedRawItemDirection;
import at.alladin.nettest.shared.model.request.SpeedtestResultSubmitRequest.TelephonyInfo;
import at.alladin.nettest.shared.model.request.SpeedtestResultSubmitRequest.WifiInfo;
import at.alladin.nettest.shared.model.response.MeasurementRequestResponse;
import at.alladin.nettest.shared.model.response.MeasurementRequestResponse.TargetMeasurementServer;
import at.alladin.nettest.shared.model.response.SpeedtestResultSubmitResponse;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

import at.alladin.rmbt.shared.Helperfunctions;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author Specure GmbH (lb@specure.com)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements/speed")
public class SpeedMeasurementResource {
	
	private final Logger logger = LoggerFactory.getLogger(SpeedMeasurementResource.class);

	/**
	 * 
	 */
    final static int UNKNOWN = Integer.MIN_VALUE;
    
    /**
     * 
     */
    final static Pattern MCC_MNC_PATTERN = Pattern.compile("\\d{3}-\\d+");
    
	@Inject
	private MessageSource messageSource;
	
	@Inject
	private ControlServerProperties controlServerProperties;
	
	@Inject
	private MeasurementServerService measurementServerService;
	
	@Inject
	private MeasurementService measurementService;
	
	@Inject
	private MeasurementSpeedtestService measurementSpeedtestService;
	
	@Inject
	private ProviderService providerService;
	
	@Inject
	private GeoLocationService geoLocationService;
	
	@Inject
	private ClientService clientService;
	
	@Inject
	private DeviceService deviceService;
	
	@Inject
	private CouchDbRepository<Measurement> measurementRepository;

	@Inject
	private CouchDbRepository<MeasurementServer> measurementServerRepository;

	@Inject
	private SettingsService settingsService;
	
	@Inject
	private TimeService timeService;
	
	@Inject
	private ProfileService profileService;

	@Inject
	private PurgeService purgeService;

	private static ua_parser.Parser parser;
	
	static {
		try {
			parser = new ua_parser.Parser(SpeedMeasurementResource.class.getResourceAsStream("/config/ua_regexes.yaml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSpeedMeasurement(@RequestBody MeasurementRequest request, 
    		HttpServletRequest httpServletRequest, @ApiIgnore Locale locale) {

		final MeasurementRequestResponse response = new MeasurementRequestResponse();
		
		final MeasurementNetworkInfo networkInfo = new MeasurementNetworkInfo();
    	final MeasurementSpeedtest speedtestInfo = new MeasurementSpeedtest();
        final MeasurementClientInfo clientInfo = new MeasurementClientInfo();
        final MeasurementDeviceInfo deviceInfo = new MeasurementDeviceInfo();
        final MeasurementMobileNetworkInfo mobileInfo = new MeasurementMobileNetworkInfo();
        final MeasurementWifiInfo wifiInfo = new MeasurementWifiInfo();
    	final Measurement measurement = new Measurement();
    	
    	measurement.setNetworkInfo(networkInfo);
    	measurement.setSpeedtest(speedtestInfo);
    	measurement.setClientInfo(clientInfo);
    	measurement.setDeviceInfo(deviceInfo);
    	measurement.setMobileNetworkInfo(mobileInfo);
    	measurement.setWifiInfo(wifiInfo);
    	
    	long startTime = System.currentTimeMillis();
    	
        final String clientIpRaw = clientService.getClientIpAddressString(httpServletRequest); //getIP(httpServletRequest);
        final InetAddress clientAddress = InetAddresses.forString(clientIpRaw);
        final String clientIpString = InetAddresses.toAddrString(clientAddress);

        logger.info("New test request from {}", clientIpRaw);
        
        //final String geoIpCountry = GeoIPHelper.lookupCountry(clientAddress);
        geoLocationService.lookupGeoLocation(measurement, clientAddress);
        
        // public_ip_asn
        final Long asn = Helperfunctions.getASN(clientAddress);

        // public_ip_as_name 
        // country_asn (2 digit country code of AS, eg. AT or EU)
        final String asName;
        final String asCountry;
        if (asn == null) {
            asName = null;
            asCountry = null;
        }
        else {
            asName = Helperfunctions.getASName(asn);
            asCountry = Helperfunctions.getAScountry(asn);
        }
        
    	final Settings settings = settingsService.getSettingsForProfile(
    			profileService.matchBasicRequest(request), controlServerProperties.getSettingsKey());
    	
    	final RmbtSettings rmbtSettings = settings.getRmbt();
    	
        response.setNumThreads(rmbtSettings.getNumThreads());
        response.setNumPings(rmbtSettings.getNumPings());
        response.setDuration(rmbtSettings.getDuration());
        response.setClientRemoteIp(clientIpString);
    	
        final String lang = request.getLanguage();
                       
        if (measurementRepository != null) {
            clientInfo.setType(request.getClientType());
            
            final List<String> clientNames = controlServerProperties.getRmbtClientName();
            final List<String> clientVersions = controlServerProperties.getRmbtVersionNumber();
            
            final String clientName = request.getClientName();
            final String clientVersion = request.getVersion();
            
            if (clientNames.contains(clientName) && clientVersions.contains(clientVersion) && request.getClientType() != null) {
                UUID clientUuid = null;
                final String clientUuidString = request.getUuid();
                
                if (clientUuidString.length() != 0) {
                    clientUuid = UUID.fromString(clientUuidString);
                }
                
                String timeZoneId = request.getTimezone();
                
                final long clientTime = request.getTime();
                final Timestamp clientTstamp = java.sql.Timestamp.valueOf(new Timestamp(clientTime).toString());                        
                final GeoLocation location = request.getGeoLocation();
                
                if (location != null) {
                	final List<GeoLocation> locations = new ArrayList<>();
                	locations.add(location);
                	measurement.setLocations(locations);
                }
                
                //Calendar timeWithZone = null;
                
                if (timeZoneId.isEmpty()) {
                    timeZoneId = Helperfunctions.getTimezoneId();
                    //timeWithZone = Helperfunctions.getTimeWithTimeZone(timeZoneId);
                }
                else {
                    //timeWithZone = Helperfunctions.getTimeWithTimeZone(timeZoneId);
                }
                                        
            	//check if client is registered
                clientService.ensureClientRegistered(request.getUuid());
                
                final String testUuid = UUID.randomUUID().toString();
                final String testOpenUuid = UUID.randomUUID().toString();
                
                boolean testServerEncryption = true; // default is true
                
                // hack for android api <= 10 (2.3.x)
                // using encryption with test doesn't work
                if ("Android".equals(request.getPlatform()) && request.getApiLevel() != null) {
                    final String apiLevelString = request.getApiLevel();
                    try {
                        final int apiLevel = Integer.parseInt(apiLevelString);
                        if (apiLevel <= 10) {
                            testServerEncryption = false;
                        }
                    }
                    catch (final NumberFormatException e) { }                            	
                }

                MeasurementServer.Type serverType = MeasurementServer.Type.SPEED_TCP;
                
                try {
                	serverType = MeasurementServer.Type.fromValue(request.getClientName());
                } catch (IllegalArgumentException e) {
					// Not found
				}
                
                final MeasurementServer measurementServer = measurementServerService.getNearestMeasurementServerByProfile(
                		profileService.matchBasicRequest(request), serverType);

                if (measurementServer == null) {
                	// TODO: Not possible?
                	throw new MeasurementServerNotFoundException(messageSource.getMessage("error.measurement_server_not_found", null,
                			"error.measurement server not found", locale));
                }
                
                request.setMeasurementServer(measurementServer);

                final boolean ipv6 = clientAddress instanceof Inet6Address;
                final TargetMeasurementServer server = new TargetMeasurementServer();
                
                server.setAddress(ipv6 ? measurementServer.getWebAddressIpv6() : measurementServer.getWebAddressIpv4());
                server.setName(measurementServer.getName() + " (" + measurementServer.getCity() + ")");
                server.setUuid(measurementServer.getUuid()); // TODO: hide this info?
                
                if (testServerEncryption) {
                	final Integer portSsl = measurementServer.getPortSsl();
                	
                	if (portSsl != null) {
                		server.setPort(portSsl);
                		server.setIsEncrypted(true);
                	} else {
                		server.setPort(measurementServer.getPort());
                		server.setIsEncrypted(false);
                	}
                } else {
                	server.setPort(measurementServer.getPort());
                	server.setIsEncrypted(false);
                }
                
                //set target measurement server in response
                response.setTargetMeasurementServer(server);
                
                //set target measurement server in speed test result
                speedtestInfo.setTargetMeasurementServer(server);
                
                //if (errorList.getLength() == 0) {                
        		speedtestInfo.setTime(timeService.getDateTimeNow());
        
            	clientInfo.setSoftwareVersion(request.getSoftwareVersion());
            	clientInfo.setSoftwareVersionCode(request.getSoftwareVersionCode());
            	                                    
                // uuid
                measurement.setUuid(testUuid);
                // open_test_uuid
                measurement.setOpenTestUuid(testOpenUuid);
                // client_id
                //st.setLong(i++, clientUid);
                
                if (!request.isAnonymous()) {
                	clientInfo.setUuid(clientUuid.toString()); // add client uuid if not anonymous!
                } else {
                	logger.debug("CLIENT IS IN ANONYMOUS MODE!");
                }
                
                // client_name
                clientInfo.setName(clientName);
                // client_version
                clientInfo.setVersion(clientVersion);
                // client_software_version
                clientInfo.setSoftwareVersion(clientVersion);
                // client_language
                clientInfo.setLanguage(lang);
                // client_public_ip
                networkInfo.setClientPublicIp(clientIpString);
                // client_public_ip_anonymized
                networkInfo.setClientPublicIpAnonymized(Helperfunctions.anonymizeIp(clientAddress));

                // now using GeoLocationService.lookupGeoLocation
                // country_geoip (2digit country code derived from public IP of client)
                // networkInfo.setCountryGeoip(geoIpCountry);
                                                    
                // timezone (of client)
                clientInfo.setTimezone(timeZoneId);
                // client_time (local time of client)
                clientInfo.setTime(new DateTime(clientTstamp.getTime(), DateTimeZone.forID(timeZoneId)));

                // duration (requested)
                speedtestInfo.setDuration((long) rmbtSettings.getDuration());
                // num_threads_requested
                speedtestInfo.setNumThreadsRequested((long) rmbtSettings.getNumThreads());

                // status (of test)
                speedtestInfo.setStatus(Status.STARTED);
                // software_revision (of client)
                clientInfo.setSoftwareRevision(request.getSoftwareRevision());
                // client_test_counter (number of tests the client has performed)
                clientInfo.setTestCounter(request.getTestCounter());
                // client_previous_test_status (outcome of previous test)
                clientInfo.setPreviousTestStatus(request.getPreviousTestStatus());

                // feat request IRL (additional client contract information)
                clientInfo.setClientContractInformation(request.getClientContractInformation());
                
                // set position
            	//if (request.getAdvancedPosition() != null) {
            	clientInfo.setPosition(request.getAdvancedPosition());
            	//}
                
                // AS name
                // public_ip_asn
                networkInfo.setPublicIpAsn(asn);
                                                    
                //public_ip_as_name
                networkInfo.setPublicIpAsName(asName);

                // AS country
                //country_asn
                networkInfo.setCountryAsn(asCountry);

                //public_ip_rdns
                String reverseDNS = Helperfunctions.reverseDNSLookup(clientAddress);
                if (reverseDNS != null) {
                	//need to cut off last dot
                	reverseDNS = reverseDNS.replaceFirst("\\.$", "");
                }
                
                logger.debug("rDNS for: {} is: {}", clientAddress, reverseDNS);
                
                networkInfo.setPublicIpRdns(reverseDNS);

                try {
                	final Provider provider = providerService.findByASN(networkInfo.getPublicIpAsn(), networkInfo.getPublicIpRdns());
                	
                	if (provider != null) {
                    	networkInfo.setProvider(provider);
                    }
                } catch (NullPointerException e) {
                	// TODO: ???
                }
                	
                //test token:
                final TestToken testToken = TestTokenUtil.generateTestToken(testUuid, measurementServer.getSecretKey());
                response.setTestToken(testToken.getToken());
                response.setTestWait(testToken.getWait());
                response.setTestUuid(testUuid);
                
                // Guess device info from user agent for websocket test
                final String platform = request.getPlatform();
        		
        		if (request.getClientType() == ClientType.DESKTOP && "RMBTws".equals(platform)) {
        			// Got request from websocket client -> fill in info based on user agent
        			response.setDevice(deviceInfo);
        			final String ua = httpServletRequest.getHeader("user-agent");
        			if (ua != null) {
    					final ua_parser.Client client = parser.parse(ua);
    					logger.debug("Parsed info from ua: {}", client.toString());
    					deviceInfo.setPlatform(platform);
    					if (client.os != null && request.getOsVersion() == null) {
    						final StringBuilder vers = new StringBuilder();
    						if (client.os.family != null) {
    							vers.append(client.os.family);
    						}
    						if (client.os.major != null) {
    							if (vers.length() > 0) {
    								vers.append(" ");
    							}
    							vers.append(client.os.major);
    							
    							if (client.os.minor != null) {
        							vers.append(".");
        							vers.append(client.os.minor);
        							
        							if (client.os.patch != null) {
            							vers.append(".");
            							vers.append(client.os.patch);
            							
            							if (client.os.patchMinor != null) {
                							vers.append(".");
                							vers.append(client.os.patchMinor);
            							}
        							}
    							}
    						}
    						if (vers.length() > 0) {
    							deviceInfo.setOsVersion(vers.toString());
    						}
    					}
    					if (client.userAgent != null) {
    						if (request.getDevice() == null) {
    							deviceInfo.setDevice(client.userAgent.family);
    						}
    						if (request.getModel() == null) {
        						deviceInfo.setModel(client.userAgent.major + "." + client.userAgent.minor + "." + client.userAgent.patch);
        					}
    					}
    					
    					logger.debug("Guessed: {}", deviceInfo);
    					//logger.debug("product: {}", client.device.family);
        			}
        		}
                
                measurement.setToken(testToken.getToken());
                speedtestInfo.setTestSlot(testToken.getTimestamp());

                measurementService.simulatePostgresTrigger(measurement);
                
                measurementRepository.save(purgeService.purgeIfNecessary(measurement, Measurement.class, settings));
            }
        }
        
        final long elapsedTime = System.currentTimeMillis() - startTime;
        logger.info("Test request from {} completed ({} ms)", clientIpRaw, Long.toString(elapsedTime));

        return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * 
	 * @return
	 */
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSpeedMeasurement(@PathVariable String uuid,
    		@RequestParam(defaultValue="false", value="details") Boolean isDetailsRequest,
    		HttpServletRequest httpServletRequest, @ApiIgnore Locale locale) {
		
		final Measurement measurement = measurementService.findMeasurementByUuid(uuid);
		
		if (isDetailsRequest) { // TODO: remove this in a later version!
			return new ResponseEntity<>(measurementSpeedtestService.getSpeedtestDetailResult(locale, measurement, false), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(measurementSpeedtestService.getSpeedtestMainResult(locale, measurement, httpServletRequest), HttpStatus.OK);
	}
	
	/**
	 * 
	 * @return
	 */
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	
	@RequestMapping(value = "/{uuid}/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSpeedMeasurementDetails(@PathVariable String uuid, @RequestParam(defaultValue="false", value="grouped") Boolean grouped, HttpServletRequest httpServletRequest, @ApiIgnore Locale locale) {
		final Measurement measurement = measurementService.findMeasurementByUuid(uuid);
		
		if (grouped) {
			return new ResponseEntity<>(measurementSpeedtestService.getSpeedtestDetailGroupResult(locale, measurement, false), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(measurementSpeedtestService.getSpeedtestDetailResult(locale, measurement, false), HttpStatus.OK);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "lang", dataType = "string", paramType = "query", value = "Accept-Language override") // Locale
	})
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> putSpeedMeasurement(@RequestBody SpeedtestResultSubmitRequest request, 
    		@PathVariable String uuid, HttpServletRequest httpServletRequest, @ApiIgnore Locale locale) {
		logger.debug("{}", request);
		
		logger.info("New test result from {}", clientService.getClientIpAddressString(httpServletRequest));
        
		final SpeedtestResultSubmitResponse response = new SpeedtestResultSubmitResponse();
		final Measurement measurement = measurementService.findMeasurementByUuid(uuid);
			
		response.setOpenTestUuid(measurement.getOpenTestUuid());
		response.setTestUuid(measurement.getUuid());

		if (request.getToken() == null || request.getToken().length() == 0) {
			throw new MeasurementTestTokenInvalidException("Invalid test token.");
		}
		
		final MeasurementServer measurementServer = measurementServerRepository
				.findOneByUuid(measurement.getSpeedtest().getTargetMeasurementServer().getUuid())
				.map(ms -> { return ms; })
				.orElseThrow(() -> new MeasurementServerNotFoundException());
		
		final String[] token = request.getToken().split("_");
		final String data = token[0] + "_" + token[1];
        
        final String hmac = Helperfunctions.calculateHMAC(measurementServer.getSecretKey(), data);
        if (hmac.length() == 0 || token[2].length() == 0 || !hmac.equals(token[2])) {
        	throw new MeasurementTestTokenInvalidException();
        }
                    
        final List<String> clientNames = controlServerProperties.getRmbtClientName();
        final List<String> clientVersions = controlServerProperties.getRmbtVersionNumber();
                                
        if (!clientNames.contains(request.getClientName())) {
        	throw new UnsupportedClientNameException("Unsupported client name: " + request.getClientName());
        }
        
        if (!clientVersions.contains(request.getClientVersion())) {
        	throw new UnsupportedClientVersionException("Unsupported client version: " + request.getClientVersion());
        }
        
        final MeasurementClientInfo clientInfo = measurement.getClientInfo();
        clientInfo.setName(request.getClientName());
        
        final MeasurementSpeedtest speedtestInfo = measurement.getSpeedtest();
        speedtestInfo.setTotalDurationNs((timeService.getTimestampNow() - speedtestInfo.getTime().getMillis()) * 1000);
        speedtestInfo.setSpeedUpload(request.getSpeedUpload());
        speedtestInfo.setSpeedDownload(request.getSpeedDownload());
        speedtestInfo.setPingShortest(request.getPingShortest());
        speedtestInfo.setEncryption(request.getEncryption());
        speedtestInfo.setSourcePorts(request.getSourcePortMap());
        
        final MeasurementDeviceInfo deviceInfo = measurement.getDeviceInfo();
        deviceInfo.setPlatform(request.getPlatform());
        deviceInfo.setOsVersion(request.getOsVersion());
        deviceInfo.setApiLevel(request.getApiLevel());
        deviceInfo.setDevice(request.getDevice());
        deviceInfo.setModel(request.getModel());
        deviceInfo.setProduct(request.getProduct());
        
        final Device device = deviceService.findByCodename(request.getModel());
        if (device != null) {
        	deviceInfo.setFullname(device.getFullname());
        }
                
        final MeasurementNetworkInfo networkInfo = measurement.getNetworkInfo();
        final MeasurementMobileNetworkInfo mobileInfo = measurement.getMobileNetworkInfo();
        
        final TelephonyInfo telephonyInfo = request.getTelephonyInfo();
        if (telephonyInfo != null) {
        	deviceInfo.setPhoneType(telephonyInfo.getPhoneType());
            
        	networkInfo.setDataState(telephonyInfo.getDataState());

        	mobileInfo.setCountry(telephonyInfo.getNetworkCountry());
	        mobileInfo.setSimCountry(telephonyInfo.getNetworkSimCountry());
	        
	        mobileInfo.setOperatorName(telephonyInfo.getNetworkOperatorName());
	        mobileInfo.setSimOperatorName(telephonyInfo.getNetworkSimOperatorName());

	        mobileInfo.setNetworkIsRoaming(telephonyInfo.getNetworkIsRoaming());
	        
	        if (telephonyInfo.getNetworkOperator() != null) {
	        	if (MCC_MNC_PATTERN.matcher(telephonyInfo.getNetworkOperator()).matches()) { // TODO: use MccMnc class
	        		mobileInfo.setOperator(telephonyInfo.getNetworkOperator());
	        	}
	        }
	        
	        if (telephonyInfo.getNetworkSimOperator() != null) { 
	        	if (MCC_MNC_PATTERN.matcher(telephonyInfo.getNetworkSimOperator()).matches()) { // TODO: use MccMnc class
	        		mobileInfo.setSimOperator(telephonyInfo.getNetworkSimOperator());
	        	}
	        }
	        
	        try {
	        	mobileInfo.setMobileProvider(providerService.findByMccMnc(
	        		MccMnc.fromString(mobileInfo.getSimOperator(), null),
	        		MccMnc.fromString(mobileInfo.getOperator(), "000-00"),
	        		speedtestInfo.getTime().toDate())
	        	);
	        }
	        catch (final IllegalMccMncException e) {
	        	e.printStackTrace();
	        }
        }
        
        final WifiInfo requestWifiInfo = request.getWifiInfo();
        if (requestWifiInfo != null) {
	        final MeasurementWifiInfo wifiInfo = measurement.getWifiInfo();
	        wifiInfo.setSsid(requestWifiInfo.getSsid());
	        wifiInfo.setNetworkId(requestWifiInfo.getNetworkId());
	        wifiInfo.setBssid(requestWifiInfo.getBssid());
	        
	        //
	        
	        final Long publicIpAsn = networkInfo.getPublicIpAsn();
	        final String publicIpRdns = networkInfo.getPublicIpRdns();
	        
	        logger.debug("public ip asn: {}, public ip rdns: {}", publicIpAsn, publicIpRdns);
	        
	        if (publicIpAsn != null && publicIpRdns != null) {
	        	networkInfo.setProvider(providerService.findByASN(networkInfo.getPublicIpAsn(), networkInfo.getPublicIpRdns()));
	        }
        }
        
        speedtestInfo.setNumThreads(request.getNumThreads());
        speedtestInfo.setBytesDownload(request.getBytesDownload());
        speedtestInfo.setBytesUpload(request.getBytesUpload());
        speedtestInfo.setDurationDownloadNs(request.getDurationDownloadNs());
        speedtestInfo.setDurationUploadNs(request.getDurationUploadNs());

        speedtestInfo.setTotalBytesDownload(request.getTotalBytesDownload());
        speedtestInfo.setTotalBytesUpload(request.getTotalBytesUpload());
        speedtestInfo.setInterfaceDltestBytesDownload(request.getInterfaceDltestBytesDownload());
        speedtestInfo.setInterfaceDltestBytesUpload(request.getInterfaceDltestBytesUpload());
        speedtestInfo.setInterfaceTotalBytesDownload(request.getInterfaceTotalBytesDownload());
        speedtestInfo.setInterfaceTotalBytesUpload(request.getInterfaceTotalBytesUpload());
        speedtestInfo.setInterfaceUltestBytesDownload(request.getInterfaceUltestBytesDownload());
        speedtestInfo.setInterfaceUltestBytesUpload(request.getInterfaceUltestBytesUpload());
        
        speedtestInfo.setRelativeTimeDlNs(request.getRelativeTimeDlNs());
        speedtestInfo.setRelativeTimeUlNs(request.getRelativeTimeUlNs());
        speedtestInfo.setNumThreadsUl(request.getNumThreadsUl());
        
        clientInfo.setSoftwareVersion(request.getClientSoftwareVersion());
        networkInfo.setNetworkType(request.getNetworkType());
        
        measurement.setTag(request.getTag());
        measurement.setPublishPublicData(request.isPublishPublicData());
        
        if (request.getIpLocal() != null) {
        	final InetAddress ipLocalAddress = InetAddresses.forString(request.getIpLocal());
        	//original address (not filtered)
        	clientInfo.setIpLocal(InetAddresses.toAddrString(ipLocalAddress));
			//anonymized local address
			clientInfo.setIpLocalAnonymized(Helperfunctions.anonymizeIp(ipLocalAddress));
			//type of local ip
			clientInfo.setIpLocalType(Helperfunctions.IpType(ipLocalAddress));
			//public ip
			if (networkInfo.getClientPublicIp() != null) {
				networkInfo.setNatType(Helperfunctions.getNatType(ipLocalAddress, InetAddresses.forString(networkInfo.getClientPublicIp())));
			}
        }

        speedtestInfo.getTargetMeasurementServer().setIp(request.getIpServer());
                                                                               
        measurement.setSourceIp(clientService.getClientIpAddressString(httpServletRequest));
        if (measurement.getSourceIp() != null) {
        	measurement.setSourceIpAnonymized(Helperfunctions.anonymizeIp(InetAddresses.forString(measurement.getSourceIp())));
        }
                                        
        measurement.setExtendedTestStat(request.getExtendedTestStat());
        
//                                        
//        //////////////////////////////////////////////////
//        // advertised speed option:
//        //////////////////////////////////////////////////
//        final Long advSpdOptionId = request.optLong("adv_spd_option_id");
//        if (advSpdOptionId != null) {
//        	final AdvertisedSpeedOptionRepository repo = new AdvertisedSpeedOptionRepository(conn);
//        	final AdvertisedSpeedOption advSpd = repo.getByUid(advSpdOptionId);
//        	if (advSpd != null) {
//        		((LongField) test.getField("adv_spd_option_id")).setValue(advSpdOptionId);
//        		test.getField("adv_spd_option_name").setString(advSpd.getName());
//        	}
//        	
//        	if (request.has("adv_spd_up_kbit")) {
//        		((LongField) test.getField("adv_spd_up_kbit")).setValue(request.getLong("adv_spd_up_kbit"));
//        	}
//        	if (request.has("adv_spd_down_kbit")) {
//        		((LongField) test.getField("adv_spd_down_kbit")).setValue(request.getLong("adv_spd_down_kbit"));
//        	}
//        }
                                                
        final List<MeasurementSpeedRawItem> speedRawListUp = new ArrayList<>();
        final List<MeasurementSpeedRawItem> speedRawListDown = new ArrayList<>();
        final MeasurementSpeedRaw speedRaw = new MeasurementSpeedRaw();
        speedRaw.setDownload(speedRawListDown);
        speedRaw.setUpload(speedRawListUp);
        speedtestInfo.setSpeedRaw(speedRaw);
        
        for (final SpeedRawItem speedItem : request.getSpeedDetail()) {
        	final MeasurementSpeedRawItem speedRawItem = new MeasurementSpeedRawItem();
        	
        	speedRawItem.setBytes(speedItem.getBytes());
        	speedRawItem.setThread(speedItem.getThread());
        	speedRawItem.setTime(speedItem.getTime());
        	
        	if (speedItem.getDirection() == SpeedRawItemDirection.DOWNLOAD) {
        		speedRawListDown.add(speedRawItem);
        	} else {
        		speedRawListUp.add(speedRawItem);
        	}
        }
        
        // pings
        final List<Long> pingValueList = new ArrayList<>();
        for (final Ping ping : request.getPings()) {
        	if (ping.getValueServer() != null && ping.getValueServer() >= 0) {
        		pingValueList.add(ping.getValueServer());
        	}
        }
        
        speedtestInfo.setPings(request.getPings());
        if (pingValueList != null) {
        	speedtestInfo.setPingVariance(MeasurementUtil.calculatePingVariance(pingValueList));
        }
        
        // geo locations
        measurement.setLocations(request.getGeoLocations());
        
        // cell locations
        measurement.setCellLocations(request.getCellLocations());

        // signals
        if (request.getSignals() != null && request.getSignals().size() > 0) {
			int signalStrength = Integer.MAX_VALUE; //measured as RSSI (GSM,UMTS,Wifi)
			int lteRsrp = Integer.MAX_VALUE; // signal strength measured as RSRP
			int lteRsrq = Integer.MAX_VALUE; // signal quality of LTE measured as RSRQ
			int linkSpeed = UNKNOWN;
	
	        for (final Signal signalItem : request.getSignals()) {
	        	lteRsrp = signalItem.getLteRsrp() == null ? UNKNOWN : signalItem.getLteRsrp();
	        	lteRsrq = signalItem.getLteRsrq() == null ? UNKNOWN : signalItem.getLteRsrq();
	        	
	        	final Integer rssi = signalItem.getWifiRssi();
	        	if (rssi != null && rssi == UNKNOWN) {
	        		signalItem.setWifiRssi(null);
	        	}
	        	
	        	final Integer curSignalStrength = signalItem.getSignalStrength();
	        	if (curSignalStrength != null && curSignalStrength == UNKNOWN) {
	        		signalItem.setSignalStrength(null);
	        	}
	        	
	            if (networkInfo.getNetworkType() == 99) {
	            	//wlan
	            	if (rssi != null && rssi < signalStrength && rssi != UNKNOWN) {
	            		signalStrength = rssi;
	            	}
	            }
	            else if (curSignalStrength != null && curSignalStrength < signalStrength && curSignalStrength != UNKNOWN) {
	            	signalStrength = curSignalStrength;
	            }
	          
	            if (signalItem.getWifiLinkSpeed() != null && signalItem.getWifiLinkSpeed() != 0 && 
	            		(linkSpeed == UNKNOWN || signalItem.getWifiLinkSpeed() < linkSpeed)) {
	            	linkSpeed = signalItem.getWifiLinkSpeed();
	            }
	        }
	        
	        if (signalStrength != Integer.MAX_VALUE && signalStrength != UNKNOWN && signalStrength != 0) {
	        	measurement.setSignalStrength(signalStrength);
	        }
	        // set rsrp value (typically LTE)
	        if (lteRsrp != Integer.MAX_VALUE && lteRsrp != UNKNOWN && lteRsrp != 0) {
	        	measurement.setLteRsrp(lteRsrp);
	        }
	        // set rsrq value (LTE)
	        if (lteRsrq != Integer.MAX_VALUE && lteRsrq != UNKNOWN) {
	        	measurement.setLteRsrq(lteRsrq);
			}
		
	        if (linkSpeed != Integer.MAX_VALUE && linkSpeed != UNKNOWN) {
	        	measurement.setWifiLinkSpeed(linkSpeed);
	        }
	        
	        measurement.setSignals(request.getSignals());
        }

        // checks:
        
        if (networkInfo.getNetworkType() <= 0) { // TODO: allow -1 for unknown and 0 for none?
        	throw new MeasurementInvalidNetworkTypeException("Invalid network type");
        }

        if (speedtestInfo.getSpeedDownload() <= 0 || speedtestInfo.getSpeedDownload() > 100000000) {
        	throw new MeasurementInvalidSpeedException("Invalid download speed.");
        }

        if (speedtestInfo.getSpeedUpload() <= 0 || speedtestInfo.getSpeedUpload() > 100000000) {
        	throw new MeasurementInvalidSpeedException("Invalid download speed.");
        }

        if (speedtestInfo.getPingShortest() != null) {
	        if (speedtestInfo.getPingShortest() <= 0 || speedtestInfo.getPingShortest() > 60000000000L) {
	        	throw new MeasurementInvalidSpeedException("Insane ping.");
	        }
        }
        
//        /*
//         * check for different types (e.g.
//         * 2G/3G)
//         */
//        final String sqlAggSignal = "WITH agg AS"
//                + " (SELECT array_agg(DISTINCT nt.group_name ORDER BY nt.group_name) agg"
//                + " FROM signal s"
//                + " JOIN network_type nt ON s.network_type_id=nt.uid WHERE test_id=?)"
//                + " SELECT uid FROM agg JOIN network_type nt ON nt.aggregate=agg";
//        
//        final PreparedStatement psAgg = conn.prepareStatement(sqlAggSignal);
//        psAgg.setLong(1, test.getUid());
//        if (psAgg.execute())
//        {
//            final ResultSet rs = psAgg.getResultSet();
//            if (rs.next())
//            {
//                final int newNetworkType = rs.getInt("uid");
//                if (newNetworkType != 0)
//                    ((IntField) test.getField("network_type")).setValue(newNetworkType);
//            }
//        }
                                                                              
        measurementService.simulatePostgresTrigger(measurement);
        
        final Settings settings = settingsService.getSettingsForProfile(
                profileService.matchBasicRequest(request), controlServerProperties.getSettingsKey());

        measurementRepository.save(purgeService.purgeIfNecessary(measurement, Measurement.class, settings));
        
		return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
