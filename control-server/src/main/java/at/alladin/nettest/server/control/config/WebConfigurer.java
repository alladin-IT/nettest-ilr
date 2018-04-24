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

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import at.alladin.nettest.server.control.config.db.CouchDbMessageSource;
import at.alladin.nettest.server.control.config.support.AcceptHeaderGetParamOverrideLocaleResolver;
import at.alladin.nettest.shared.server.helper.GsonHelper;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Configuration
public class WebConfigurer extends WebMvcConfigurerAdapter {
	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(WebConfigurer.class);

	/**
	 * 
	 */
	@Inject
	private Environment env;
	
	/**
	 * 
	 * @return
	 */
	@Bean
	public LocaleResolver localeResolver() {
	    return new AcceptHeaderGetParamOverrideLocaleResolver();
	}
	
	/**
	 * 
	 * @return
	 */
	@Bean
    public MessageSource messageSource() {
		final ResourceBundleMessageSource messageSource = new CouchDbMessageSource();

		messageSource.setBasename("language");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(false);
        
        return messageSource;
    }
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry)
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
			.addMapping("/api/**")
				.allowedOrigins("*")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "PATH", "OPTIONS")
				.allowedHeaders("Content-Type", "Accept", "Accept-Language")
				.allowCredentials(false)
				.maxAge(60);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#configureMessageConverters(java.util.List)
	 */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(createGsonHttpMessageConverter());
    }
    
    /**
     * 
     * @return
     */
    private GsonHttpMessageConverter createGsonHttpMessageConverter() {
    	final Gson gson = GsonHelper.createRestGsonBuilderWithSwagger(env, Constants.SPRING_PROFILE_DEVELOPMENT).create();
    	
        final GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
        gsonConverter.setGson(gson);

        return gsonConverter;
    }
    
    @Bean
    public GsonBuilder restGsonBuilder() {
    	return GsonHelper.createRestGsonBuilder(env, Constants.SPRING_PROFILE_DEVELOPMENT); 
    }
}
