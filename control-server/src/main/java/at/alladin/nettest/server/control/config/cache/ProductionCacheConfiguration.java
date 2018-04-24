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

package at.alladin.nettest.server.control.config.cache;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cache.CacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.DependsOn;
//
//import com.google.code.ssm.Cache;
//import com.google.code.ssm.CacheFactory;
//import com.google.code.ssm.aop.CacheBase;
//import com.google.code.ssm.config.DefaultAddressProvider;
//import com.google.code.ssm.providers.spymemcached.MemcacheClientFactoryImpl;
//import com.google.code.ssm.providers.spymemcached.SpymemcachedConfiguration;
//import com.google.code.ssm.spring.SSMCache;
//import com.google.code.ssm.spring.SSMCacheManager;
//
///**
// * 
// * @author Specure GmbH (bp@specure.com)
// *
// */
//public class ProductionCacheConfiguration {
//	
//	@SuppressWarnings("unused")
//	private final Logger logger = LoggerFactory.getLogger(ProductionCacheConfiguration.class);
//	
//	/**
//	 * 
//	 * @return
//	 */
//	@Bean
//    public Cache defaultCache() {
//        try {
//            return cacheFactory().getObject();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//	
//	/**
//	 * 
//	 * @return
//	 */
//	@Bean
//	public CacheManager cacheManager() {
//		final Set<SSMCache> cacheSet = new HashSet<>();
//		cacheSet.add(new SSMCache(defaultCache(), 300, true));
//		
//		final SSMCacheManager ssmCacheManager = new SSMCacheManager();
//		ssmCacheManager.setCaches(cacheSet);
//
//		return ssmCacheManager;
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	@Bean
//	@DependsOn("cacheBase")
//    public CacheFactory cacheFactory() {
//        final CacheFactory cacheFactory = new CacheFactory();
//        cacheFactory.setCacheName("defaultCache");
//        cacheFactory.setCacheClientFactory(new MemcacheClientFactoryImpl());
//        
//        String address = "localhost:11211";
//        
//        cacheFactory.setAddressProvider(new DefaultAddressProvider(address));
//        
//        final SpymemcachedConfiguration configuration = new SpymemcachedConfiguration();
//        configuration.setConsistentHashing(true);
//        configuration.setUseBinaryProtocol(true);
//        
//        cacheFactory.setConfiguration(configuration);
//        
//        return cacheFactory;
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	@Bean
//	public CacheBase cacheBase() {
//		final CacheBase cacheBase = new CacheBase();
//		
//		return cacheBase;
//	}
//}
