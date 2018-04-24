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

package at.alladin.nettest.server.control.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import at.alladin.nettest.server.control.config.support.MethodLoggingAspect;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class LoggingConfiguration {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(LoggingConfiguration.class);
	
	/**
	 * 
	 * @return
	 */
	@Bean
	@Profile("!" + Constants.SPRING_PROFILE_PRODUCTION) // exclude in production
    public MethodLoggingAspect loggingAspect() {
        return new MethodLoggingAspect();
    }
}
