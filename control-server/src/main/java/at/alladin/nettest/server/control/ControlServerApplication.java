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

package at.alladin.nettest.server.control;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import at.alladin.nettest.server.control.config.Constants;
import at.alladin.nettest.server.control.config.ControlServerProperties;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@SpringBootApplication
@EnableConfigurationProperties({
	ControlServerProperties.class
})
public class ControlServerApplication extends SpringBootServletInitializer {

	private static final Logger logger = LoggerFactory.getLogger(ControlServerApplication.class);
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.boot.context.web.SpringBootServletInitializer#configure(org.springframework.boot.builder.SpringApplicationBuilder)
	 */
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.profiles(getWebApplicationProfile())
        	.properties(getProperties())
            .sources(ControlServerApplication.class);
    }
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final SpringApplication app = new SpringApplicationBuilder(ControlServerApplication.class)
				.properties(getProperties())
				.application();
		
		addCommandLineDefaultProfileIfNotSet(app, args);
		
		final Environment environment = app.run(args).getEnvironment();
		
		logger.info(
			"Access URLs:\n----------------------------------------------------------\n\t" +
            "Local: \t\thttp://127.0.0.1:{}\n\t" +
            "External: \thttp://{}:{}\n----------------------------------------------------------",
            environment.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            environment.getProperty("server.port"));
	}
	
	/**
	 * 
	 */
	private static void addCommandLineDefaultProfileIfNotSet(SpringApplication app, String[] args) {
		final PropertySource<?> propertySource = new SimpleCommandLinePropertySource(args);
		
		if (!propertySource.containsProperty(Constants.SPRING_PROFILES_ACTIVE_KEY)
				&& !System.getenv().containsKey(Constants.SPRING_PROFILES_ACTIVE_STRING)) {
			
            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
	}
	
	/**
	 * 
	 * @return
	 */
	private static String getProperties() {
		return "spring.config.location=file:" + Constants.EXTERNAL_CONFIG_LOCATION;
	}
	
	/**
	 * 
	 * @return
	 */
	private static String getWebApplicationProfile() {
		final String profile = System.getProperty(Constants.SPRING_PROFILES_ACTIVE_KEY);
        if (profile != null) {
            logger.info("Running with Spring profile(s) : {}", profile);
            return profile;
        }

        logger.warn("No Spring profile configured, running with default configuration");
        return Constants.SPRING_PROFILE_DEVELOPMENT;
	}
}
