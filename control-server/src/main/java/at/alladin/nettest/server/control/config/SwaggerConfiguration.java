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

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Configuration
@EnableSwagger2
@Profile("!" + Constants.SPRING_PROFILE_PRODUCTION) // exclude in production
public class SwaggerConfiguration {

    private final Logger logger = LoggerFactory.getLogger(SwaggerConfiguration.class);

    /**
     * 
     */
    private static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";
    
    /**
     * 
     */
    private static final List<ResponseMessage> GLOBAL_RESPONSE_MESSAGES = Arrays.asList(new ResponseMessage[] {
    	new ResponseMessageBuilder().code(403).message("Forbidden").responseModel(new ModelRef("JsonError")).build(),
    	new ResponseMessageBuilder().code(409).message("Conflict").responseModel(new ModelRef("JsonError")).build(),
    	new ResponseMessageBuilder().code(500).message("Internal Server Error").responseModel(new ModelRef("JsonError")).build(),
    });

    /**
     * 
     * @return
     */
    @Bean
    public Docket controlServerV1Api() {
    	logger.debug("Starting Swagger");
        
        final StopWatch watch = new StopWatch();
        watch.start();
    	
    	final Docket docket = new Docket(DocumentationType.SWAGGER_2)
    			.useDefaultResponseMessages(false)  
    			.globalResponseMessage(RequestMethod.GET, GLOBAL_RESPONSE_MESSAGES)
    			.globalResponseMessage(RequestMethod.POST, GLOBAL_RESPONSE_MESSAGES)
    			.globalResponseMessage(RequestMethod.PUT, GLOBAL_RESPONSE_MESSAGES)

    			//.groupName("control server")
	    		.apiInfo(apiInfo())
	    		.genericModelSubstitutes(ResponseEntity.class)
	            .forCodeGeneration(true)
	            .genericModelSubstitutes(ResponseEntity.class)
	            //.ignoredParameterTypes(Pageable.class)
	            .directModelSubstitute(java.time.LocalDate.class, String.class)
	            .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
	            .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
	    		.select()
	    		.paths(regex(DEFAULT_INCLUDE_PATTERN))
	    		.build();
    	
    	watch.stop();
        
        logger.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
        
        return docket;
    }
    
    /**
     * 
     * @return
     */
    private ApiInfo apiInfo() {
    	return new ApiInfoBuilder()
                .title("Control Server")
                .description("Control Server")
                .termsOfServiceUrl("...")
                //.contact("...")
                .license("...")
                .licenseUrl("...")
                .version("1.0.0")
                .build();
    }
}
