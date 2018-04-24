/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

package at.alladin.rmbt.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import at.alladin.nettest.shared.model.Client;
import at.alladin.nettest.shared.model.Client.ClientType;
import at.alladin.nettest.shared.model.request.BasicRequest;
import at.alladin.nettest.shared.model.request.MeasurementRequest;
import at.alladin.nettest.shared.model.request.SettingsRequest;
import at.alladin.nettest.shared.model.request.SpeedtestResultSubmitRequest;
import at.alladin.nettest.shared.model.response.SettingsResponse;

import at.alladin.rmbt.client.db.model.ClientLocalSettings;
import at.alladin.rmbt.client.db.model.ClientRunnerOptions;
import at.alladin.rmbt.client.db.model.ClientRunnerOptions.Server;
import at.alladin.rmbt.client.db.model.ClientRunnerOptions.ServerOptions;
import at.alladin.rmbt.client.helper.Config;
import at.alladin.rmbt.client.helper.ControlServerConnection;
import at.alladin.rmbt.client.helper.RevisionHelper;
import at.alladin.rmbt.client.helper.TestStatus;
import at.alladin.rmbt.client.impl.TracerouteServiceImpl;
import at.alladin.rmbt.client.v2.task.QoSTestEnum;
import at.alladin.rmbt.client.v2.task.result.QoSResultCollector;
import at.alladin.rmbt.client.v2.task.service.TestSettings;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class RMBTClientRunner
{
    
	public final static String CONFIG_ENV_VAR = "ANT_JAVA_CLIENT_CONFIG";
	
	private final static String DEFAULT_SETTINGS_FILE = "settings.yml";
	
    /**
     * @param args
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static void main(final String[] args) throws IOException, InterruptedException, KeyManagementException,
            NoSuchAlgorithmException
    {    	
        final OptionParser parser = new OptionParser()
        {
            {
                acceptsAll(Arrays.asList("?", "help"), "show help");
                
                acceptsAll(Arrays.asList("h", "control-host"), "control server IP or hostname (required)")
                		.withRequiredArg().ofType(String.class);
                
                acceptsAll(Arrays.asList("p", "control-port"), "control server port (required)")
                		.withRequiredArg().ofType(Integer.class);

                acceptsAll(Arrays.asList("H", "test-host"), "speedtest server IP or hostname")
        				.withRequiredArg().ofType(String.class);
        
                acceptsAll(Arrays.asList("P", "test-port"), "speedtest server port")
        				.withRequiredArg().ofType(Integer.class);

                acceptsAll(Arrays.asList("s", "ssl"), "use SSL/TLS");
                
                acceptsAll(Arrays.asList("ssl-no-verify"), "turn off SSL/TLS certificate validation");
                
                acceptsAll(Arrays.asList("t", "threads"), "number of threads (required when dev-mode)")
                		.withRequiredArg().ofType(Integer.class);
                
                acceptsAll(Arrays.asList("d", "duration"), "test duration in seconds (required when dev-mode)")
                		.withRequiredArg().ofType(Integer.class);

                acceptsAll(Arrays.asList("i", "pings"), "number of pings (required when dev-mode)")
        				.withRequiredArg().ofType(Integer.class);

                acceptsAll(Arrays.asList("no-qos"), "do not run QoS tests");

                acceptsAll(Arrays.asList("u", "uuid"), "client UUID").withRequiredArg().ofType(String.class);
                
                acceptsAll(Arrays.asList("c", "config"), "config file path").withRequiredArg().ofType(String.class);
                
                //TODO: implement NDT again later?
                //acceptsAll(Arrays.asList("n", "ndt"), "run NDT after RMBT");
                //acceptsAll(Arrays.asList("ndt-host"), "NDT host to use").withRequiredArg().ofType(String.class);
            }
        };
        
        System.out.println(String.format("=============== RMBTClient %s ===============",
                RevisionHelper.getVerboseRevision()));
        
        OptionSet options;
        try
        {
            options = parser.parse(args);
        }
        catch (final OptionException e)
        {
            System.out.println(String.format("error while parsing command line options: %s", e.getLocalizedMessage()));
            System.exit(1);
            return;
        }
        
        if (options.has("?")) {
            System.out.println();
            parser.printHelpOn(System.out);
            System.exit(1);
            return;
        }
        
        //if config file is provided ignore everything else
        if (options.has("c")) {
        	try (final FileInputStream fis = new FileInputStream((String)options.valueOf("c"))) {
        		final ClientRunnerOptions runnerOptions = new Yaml().loadAs(fis, ClientRunnerOptions.class);
        		runTest(runnerOptions);
        		System.exit(0);
        	}
        }
        
        //check if config env variable is set
        try {
        	if (System.getenv(CONFIG_ENV_VAR) != null) {
            	try (final FileInputStream fis = new FileInputStream(System.getenv(CONFIG_ENV_VAR))) {
            		final ClientRunnerOptions runnerOptions = new Yaml().loadAs(fis, ClientRunnerOptions.class);
            		runTest(runnerOptions);
            		System.exit(0);
            	}        		
        	}
        }
        catch (final Exception e) {}
        
        //continue if no config file is provided
        
        final String[] requiredArgs = { "h", "p" };
        
        if (options.has("ssl-no-verify")) {
            SSLContext.setDefault(RMBTClient.getSSLContext(null, null));
        }
        else {
            SSLContext.setDefault(RMBTClient.getSSLContext("at/alladin/rmbt/crt/ca.pem",
                    "at/alladin/rmbt/crt/controlserver.pem"));
        }
        
        
        String uuid = null;
        
        if (options.has("u")) {
        	try {
            	uuid = (String) options.valueOf("u");
        		UUID.fromString(uuid);
        	}
        	catch (Exception e) {
        		System.err.println(String.format("Error parsing UUID %s: %s", uuid, e.getLocalizedMessage()));
        		System.exit(1);
        		return;
        	}
        }
        
        boolean reqArgMissing = false;
        
        if (!options.has("?")) {
            for (final String arg : requiredArgs)
                if (!options.has(arg))
                {
                    reqArgMissing = true;
                    System.out.println(String.format("ERROR: required argument '%s' is missing", arg));
                }
        }
        
        if (reqArgMissing) {
            System.out.println();
            parser.printHelpOn(System.out);
            System.exit(1);
            return;
        }
                
        final String host = (String) options.valueOf("h");
        final int port = (Integer) options.valueOf("p");
        final boolean encryption = options.has("s") ? true : false;
        
        final JSONObject additionalValues = new JSONObject();
        try
        {
            additionalValues.put("ndt", options.has("n"));
            additionalValues.put("plattform", "CLI");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        
        int numThreads = 0;
        int duration = 0;
        int numPings = 10;
        
        if (options.has("t")) {
            numThreads = (Integer) options.valueOf("t");
        }
        if (options.has("d")) {
            duration = (Integer) options.valueOf("d");
        }
        if (options.has("i")) {
        	numPings = (Integer) options.valueOf("i");
        }
        
        final ClientRunnerOptions runnerOptions = new ClientRunnerOptions();
        runnerOptions.setClientUuid(uuid);
        runnerOptions.setDuration(duration);
        runnerOptions.setThreads(numThreads);
        runnerOptions.setSslNoVerify(options.has("ssl-no-verify"));
        runnerOptions.setHasQos(!options.has("no-qos"));
        runnerOptions.setUseSsl(encryption);
        runnerOptions.setPings(numPings);
        
        runnerOptions.setServer(new Server());
        runnerOptions.getServer().setControl(new ServerOptions());
        runnerOptions.getServer().getControl().setHost(host);
        runnerOptions.getServer().getControl().setPort(port);
        
        if (options.has("H") && options.has("P")) {
        	runnerOptions.getServer().setSpeedtest(new ServerOptions());
        	runnerOptions.getServer().getSpeedtest().setHost((String) options.valueOf("H"));
        	runnerOptions.getServer().getSpeedtest().setPort((Integer) options.valueOf("P"));
        }
        
        runTest(runnerOptions);
    }
    
    public static void runTest(final ClientRunnerOptions runnerOptions) throws InterruptedException, IOException {
    	final RMBTClient client = prepareClient(runnerOptions);
    	runTest(client, runnerOptions);
    }
    
    public static void runTest(final RMBTClient client, final ClientRunnerOptions runnerOptions) throws InterruptedException {
        if (client != null)
        {
            final TestResult result = client.runTest();
            
            if (result != null)
            {
            	System.out.println("Sending speedtest results...");
                client.sendResult(generateResultSubmitRequest(client.getTestUuid()));
            }
            
            client.shutdown();
            
			System.out.println("TestStatus after speed test: " + client.getStatus());
            
            if (runnerOptions.getHasQos()) {
				try {
					System.out.print("Starting QoS test...");
	            	TestSettings nnTestSettings = new TestSettings(client.getControlConnection().getStartTimeNs());
	            	nnTestSettings.setTracerouteServiceClazz(TracerouteServiceImpl.class);
	            	nnTestSettings.setUseSsl(true); //TODO: use param?
					QualityOfServiceTest nnTest = new QualityOfServiceTest(client, nnTestSettings);
					QoSResultCollector nnResult = nnTest.call();
	            	System.out.println("finished.");
					if (nnResult != null && nnTest.getStatus().equals(QoSTestEnum.QOS_FINISHED)) {
						client.sendQoSResult(nnResult);
						System.out.println("finished");
					}
					else {
						System.out.println("Error during QoS test.");
					}
	            	
				} catch (Exception e) {
					e.printStackTrace();
				}                            	                    	
	
				System.out.println("TestStatus after QoS test: " + client.getStatus());
            }
            else {
            	System.out.println("Skipping QoS test.");
            }
			
            if (client.getStatus() != TestStatus.SPEEDTEST_END && client.getStatus() != TestStatus.QOS_END) {
                System.out.println("ERROR: " + client.getErrorMsg());
            }
            else
            {
                /*if (options.has("n"))
                {
                	
                    System.out.println("\n\nStarting NDT...");
                    
                    String ndtHost = null;
                    //if (options.has("ndt-host"))
                    //	ndtHost = (String) options.valueOf("ndt-host");
                    
                    final NDTRunner ndtRunner = new NDTRunner(ndtHost);
                    ndtRunner.runNDT(NdtTests.NETWORK_WIRED, ndtRunner.new UiServices()
                    {
                        @Override
                        public void appendString(String str, int viewId)
                        {
                            super.appendString(str, viewId);
//                            if (viewId == MAIN_VIEW)
                            System.out.println(str);
                        }
                        
                        @Override
                        public void sendResults()
                        {
                            System.out.println("sending NDT results...");
                            client.getControlConnection().sendNDTResult(this, null);
                        }
                    });
                    
                    System.out.println("NDT finished.");
                } */
            }
        }
    }
    
    /**
     * sends a settings request and prepares an instance of RMBTClient
     * @param runnerOptions
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static RMBTClient prepareClient(final ClientRunnerOptions runnerOptions) throws InterruptedException, IOException {
        final RMBTClient client;

        String overrideHost = null;
        int overridePort = 0;        
        if (runnerOptions.getServer().getSpeedtest() != null) {
        	overrideHost = runnerOptions.getServer().getSpeedtest().getHost();
        	overridePort = runnerOptions.getServer().getSpeedtest().getPort() != null ?
        			runnerOptions.getServer().getSpeedtest().getPort() : 0;
        }
        int overrideDuration = runnerOptions.getDuration() != null ? runnerOptions.getDuration() : 0;
        int overrideThreads = runnerOptions.getThreads() != null ? runnerOptions.getThreads() : 0;
        int overridePings = runnerOptions.getPings() != null ? runnerOptions.getPings() : 0;

        RMBTTestParameter overrideParams = null;
        
        if (overrideHost != null || overrideDuration > 0 || overrideThreads > 0 || overridePings > 0) {
        	overrideParams = new RMBTTestParameter(overrideHost, overridePort, 
        			false, overrideDuration, overrideThreads, overridePings);
        }

        final boolean encryption = runnerOptions.getUseSsl();
        final String host = runnerOptions.getServer().getControl().getHost();
        final int port = runnerOptions.getServer().getControl().getPort();
        
        final ClientLocalSettings localSettings = loadLocalSettingsFile(runnerOptions);
        localSettings.setTermsAndConditionsAccepted(true); //TODO: better solution in future? let the user accept T&C?
        localSettings.setTermsAndConditionsAcceptedVersion(1);
        
        String uuid = runnerOptions.getClientUuid() != null ? runnerOptions.getClientUuid() : localSettings.getClientUuid();

    	final ControlServerConnection conn = new ControlServerConnection(encryption, host, null, port);
    	final SettingsRequest request = fillBasicInfo(SettingsRequest.class);
    	request.setClient(new Client());
    	request.getClient().setClientType(ClientType.DESKTOP);
    	request.getClient().setUuid(uuid);
    	request.getClient().setTermsAndConditionsAccepted(localSettings.isTermsAndConditionsAccepted());
    	request.getClient().setTermsAndConditionsAcceptedVersion(localSettings.getTermsAndConditionsAcceptedVersion());
    	final SettingsResponse response = conn.requestSettings(request);
        
    	uuid = response.getClient().getUuid();
    	localSettings.setClientUuid(uuid);
    	saveLocalSettingsFile(runnerOptions, localSettings);
    	
    	final ArrayList<String> geoInfo = null; //[sic!]
    	
        client = RMBTClient.getInstance(host, null, port, encryption, geoInfo, uuid,
                ClientType.DESKTOP, Config.RMBT_CLIENT_NAME, Config.RMBT_VERSION_NUMBER, overrideParams, fillBasicInfo(MeasurementRequest.class));
        
        return client;
    }

    public static <T extends BasicRequest> T fillBasicInfo(Class<T> basicRequestClazz) {

        final T basicRequest;
        try {
            basicRequest = basicRequestClazz.newInstance();

            basicRequest.setPlatform(System.getProperty("os.name"));
            basicRequest.setOsVersion(System.getProperty("os.version"));
            //basicRequest.setDevice(android.os.Build.DEVICE);
            //basicRequest.setModel(android.os.Build.MODEL);
            //basicRequest.setProduct(android.os.Build.PRODUCT);
            basicRequest.setLanguage(Locale.getDefault().getLanguage());
            basicRequest.setTimezone(TimeZone.getDefault().getID());
            basicRequest.setSoftwareRevision(RevisionHelper.getVerboseRevision());
            basicRequest.setSoftwareVersionName("1.0");
            basicRequest.setClientType(ClientType.DESKTOP);

            return basicRequest;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static SpeedtestResultSubmitRequest generateResultSubmitRequest(final String testUuid) {
    	final SpeedtestResultSubmitRequest resultDetailsInfo = new SpeedtestResultSubmitRequest();
    	resultDetailsInfo.setUuid(testUuid == null ? UUID.randomUUID().toString() : testUuid);
        resultDetailsInfo.setPlatform(System.getProperty("os.name"));        
        resultDetailsInfo.setOsVersion(System.getProperty("os.version"));
        resultDetailsInfo.setNetworkType(97);
        
        return resultDetailsInfo;
    }
    
    public static ClientLocalSettings loadLocalSettingsFile(final ClientRunnerOptions options) throws FileNotFoundException {
    	final String path = options.getSettingsPath() != null ? options.getSettingsPath() : DEFAULT_SETTINGS_FILE;
    	final File f = new File(path);
    	if (f.exists()) {
    		return (ClientLocalSettings) new Yaml().load(new FileReader(f));
    	}    	
    	return new ClientLocalSettings();
    }
    
    public static void saveLocalSettingsFile(final ClientRunnerOptions options, final ClientLocalSettings settings) throws IOException {
    	final String path = options.getSettingsPath() != null ? options.getSettingsPath() : DEFAULT_SETTINGS_FILE;
        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    	new Yaml(yamlOptions).dump(settings, new FileWriter(new File(path)));
    }
}
