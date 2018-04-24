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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import at.alladin.nettest.server.control.config.cache.ProductionCacheConfiguration;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Configuration
@EnableCaching
public class CacheConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);

	@Bean
	public CacheManager cacheManager() {
	    SimpleCacheManager cacheManager = new SimpleCacheManager();
	    cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("defaultCache"), new ConcurrentMapCache("qos"), new ConcurrentMapCache("settings")));

	    return cacheManager;
	}

	/**
	 * 
	 * @return
	 */
//	@Profile(Constants.SPRING_PROFILE_PRODUCTION) // only in production
//	@Bean
//	public ProductionCacheConfiguration productionCacheConfiguration() { // TODO: test it!
//		logger.info("Adding prodcution cache configuration");
//		return new ProductionCacheConfiguration();
//	}
	
//	/**
//	 * 
//	 * @return
//	 */
//	@Profile("!" + Constants.SPRING_PROFILE_PRODUCTION) // exclude in production
//	@Bean
//	public EmbeddedMemcached embeddedMemcached() {
//		return new EmbeddedMemcached();
//	}
//	
//	/**
//	 * 
//	 * @author Specure GmbH (bp@specure.com)
//	 *
//	 */
//	public static class EmbeddedMemcached {
//		
//		private final Logger logger = LoggerFactory.getLogger(EmbeddedMemcached.class);
//		
//		/**
//		 * 
//		 */
//		private MemcachedExecutable memcachedExecutable;
//		
//		/**
//		 * 
//		 */
//		private MemcachedProcess memcached;
//		
//		/**
//		 * 
//		 */
//		public EmbeddedMemcached() {
//			
//		}
//		
//		/**
//		 * @throws IOException 
//		 * 
//		 */
//		@PostConstruct
//		private void postConstruct() throws IOException {
//			final MemcachedStarter runtime = MemcachedStarter.getDefaultInstance();
//			
//			logger.info("Starting embedded memcached");
//			
//			memcachedExecutable = runtime.prepare(new MemcachedConfig(Version.V1_4_22, 11211));
//			memcached = memcachedExecutable.start();
//		}
//		
//		/**
//		 * 
//		 */
//		@PreDestroy
//		private void preDestroy() {
//			memcached.stop();
//			memcachedExecutable.stop();
//		}
//	}
}
