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

package at.alladin.nettest.shared.server.helper;

import java.lang.reflect.Type;

import org.springframework.core.env.Environment;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import at.alladin.nettest.shared.server.config.support.AnnotationBasedGsonExclusionStrategy;
import at.alladin.nettest.shared.server.helper.json.QosAbstractResultJsonDeserializer;
import at.alladin.nettest.shared.annotation.ExcludeFromDb;
import at.alladin.nettest.shared.annotation.ExcludeFromRest;
import at.alladin.nettest.shared.annotation.ExcludeFromOpenTest;
import at.alladin.nettest.shared.helper.GsonBasicHelper;

import at.alladin.rmbt.shared.qos.AbstractResult;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.ResourceListing;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

public class GsonHelper{

	/**
	 * 
	 */
	private static final JsonDeserializer<AbstractResult> QOS_ABSTRACT_RESULT_JSON_DESERIALIZER = new QosAbstractResultJsonDeserializer();
	
	
	/**
	 * 
	 * @param env
	 * @param springDevProfile pass the Spring development profile (as in Constants.SPRING_PROFILE_DEVELOPMENT), returns a gsonBuilder with prettyPrint enabled if in development profile
	 * @return
	 */
	public static GsonBuilder createRestGsonBuilder(Environment env, String springDevProfile) {
		final GsonBuilder gsonBuilder = getDefaultGsonBuilder();
		
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		gsonBuilder.setExclusionStrategies(new AnnotationBasedGsonExclusionStrategy<ExcludeFromRest>(ExcludeFromRest.class));
		
		// enable pretty print if in development profile
		if (env != null && springDevProfile != null) {
	        if (env.acceptsProfiles(springDevProfile)) {
	        	gsonBuilder.setPrettyPrinting();
	        }
		}
		
		return gsonBuilder;
	}
	
	/**
	 * 
	 * @param env
	 * @param springDevProfile pass the Spring development profile (as in Constants.SPRING_PROFILE_DEVELOPMENT), returns a gsonBuilder with prettyPrint and Swagger enabled if in development profile
	 * @return
	 */
	public static GsonBuilder createRestGsonBuilderWithSwagger(Environment env, String springDevProfile) {
		final GsonBuilder gsonBuilder = getDefaultGsonBuilder();
		
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		gsonBuilder.setExclusionStrategies(new AnnotationBasedGsonExclusionStrategy<ExcludeFromRest>(ExcludeFromRest.class));
		
		// enable pretty print if in development profile
        if (env.acceptsProfiles(springDevProfile)) {
        	gsonBuilder.setPrettyPrinting();
        	
        	// make swagger work with gson
        	makeSwaggerWorkWithGson(gsonBuilder);
        }
		
		return gsonBuilder;
	}
	
	/**
	 * 
	 * @return
	 */
	public static GsonBuilder createDatabaseGsonBuilder() {
		final GsonBuilder gsonBuilder = getDefaultGsonBuilder();
		
		gsonBuilder.setExclusionStrategies(new AnnotationBasedGsonExclusionStrategy<ExcludeFromDb>(ExcludeFromDb.class));
		
		return gsonBuilder;
	}
	
	/**
	 * Creates a GsonBuilder that auotmatically excludes all fields not destined for the opentest db
	 * relies on the correct assignment of the ExcludeFromOpenTest annotation
	 * @return a GsonBuilder with the correct setup to parse data for the opentest db
	 */
	public static GsonBuilder createOpenTestGsonBuilder() {
		final GsonBuilder gsonBuilder = getDefaultGsonBuilder();
		
		//TODO: do we need to only serialise exposed fields?
		//gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		gsonBuilder.setExclusionStrategies(new AnnotationBasedGsonExclusionStrategy<ExcludeFromOpenTest>(ExcludeFromOpenTest.class));
		
		return gsonBuilder;
	}
	
	/**
	 * 
	 * @return
	 */
	public static GsonBuilder createMethodLoggerGsonBuilder() {
		final GsonBuilder gsonBuilder = getDefaultGsonBuilder();
		
		gsonBuilder.setExclusionStrategies(new AnnotationBasedGsonExclusionStrategy<ExcludeFromRest>(ExcludeFromRest.class));
		gsonBuilder.setPrettyPrinting();
		
		return gsonBuilder;
	}
	
	public static GsonBuilder createAdminToolGsonBuilder(Environment env, String springDevProfile) {
		final GsonBuilder gsonBuilder = getDefaultGsonBuilder();
		
		//gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		gsonBuilder.disableHtmlEscaping();
		
		// enable pretty print if in development profile
        if (env.acceptsProfiles(springDevProfile)) {
        	gsonBuilder.setPrettyPrinting();
        }
		
		return gsonBuilder;
	}
	
	/**
	 * 
	 * @return
	 */
	private static GsonBuilder getDefaultGsonBuilder() {
		final GsonBuilder gsonBuilder = GsonBasicHelper.getDateTimeGsonBuilder();
		
    	// register additional type adapters
    	gsonBuilder.registerTypeAdapter(AbstractResult.class, QOS_ABSTRACT_RESULT_JSON_DESERIALIZER);
		
		return gsonBuilder;
	}
	
	/////
	
	/**
     * 
     * @param gsonBuilder
     */
	private static void makeSwaggerWorkWithGson(GsonBuilder gsonBuilder) {
		final Gson swaggerGson = new GsonBuilder()
				.setPrettyPrinting()
				.setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
				.create();
		
		// register joda datetime
    	Converters.registerDateTime(gsonBuilder);
		
		// https://github.com/springfox/springfox/issues/867
    	//gsonBuilder.registerTypeAdapter(SwaggerResource.class, new SwaggerResourceSerializer());
    	//gsonBuilder.registerTypeAdapter(ResourceListing.class, new SwaggerResourceListingJsonSerializer());
    	//gsonBuilder.registerTypeAdapter(SecurityConfiguration.class, new SwaggerSecurityConfigurationSerializer());
    	//gsonBuilder.registerTypeAdapter(UiConfiguration.class, new SwaggerUiConfigurationSerializer());
    	//gsonBuilder.registerTypeAdapter(ApiListing.class, new SwaggerApiListingJsonSerializer());
    	//gsonBuilder.registerTypeAdapter(Json.class, new SwaggerJsonSerializer());
		
		gsonBuilder.registerTypeAdapter(SwaggerResource.class, getSwaggerJsonSerializer(swaggerGson, SwaggerResource.class));
    	gsonBuilder.registerTypeAdapter(ResourceListing.class, getSwaggerJsonSerializer(swaggerGson, ResourceListing.class));
    	gsonBuilder.registerTypeAdapter(SecurityConfiguration.class, getSwaggerJsonSerializer(swaggerGson, SecurityConfiguration.class));
    	gsonBuilder.registerTypeAdapter(UiConfiguration.class, getSwaggerJsonSerializer(swaggerGson, UiConfiguration.class));
    	gsonBuilder.registerTypeAdapter(ApiListing.class, getSwaggerJsonSerializer(swaggerGson, ApiListing.class));
    	gsonBuilder.registerTypeAdapter(Json.class, new JsonSerializer<Json>() {
    		
    		/*
        	 * (non-Javadoc)
        	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
        	 */
    		@Override
    		public JsonElement serialize(Json src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonParser().parse(src.value());
    		}
    	});
	}
	
	/**
	 * 
	 * @param swaggerGson
	 * @param typeClass
	 * @return
	 */
	private static <T> JsonSerializer<T> getSwaggerJsonSerializer(final Gson swaggerGson, Class<T> typeClass) {
		return new JsonSerializer<T>() {
    		
    		/*
        	 * (non-Javadoc)
        	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
        	 */
    		@Override
    		public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
                return swaggerGson.toJsonTree(src);
    		}
    	};
	}
}
