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
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.inject.Inject;
//
//import org.lightcouch.Changes;
//import org.lightcouch.ChangesResult;
//import org.lightcouch.CouchDbClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.gson.JsonObject;
//
///**
// * 
// * @author Specure GmbH (bp@specure.com)
// *
// */
////@Component
//public class ConfigurationCache {
//
//	private Logger logger = LoggerFactory.getLogger(ConfigurationCache.class);
//	
//	// TODO: should serve as in-memory cache for configuration values
//	// TODO: settings from db should also be cached -> probably in other class
//	
//	/**
//	 * 
//	 */
//	@Inject
//	private CouchDbClient configurationDbClient;
//	
//	/**
//	 * 
//	 */
//	private Changes changes;
//	
//	/**
//	 * 
//	 */
//	public ConfigurationCache() {
//		
//	}
//	
//	/**
//	 * 
//	 */
//	@PostConstruct
//	private void postConstruct() {
//		final String since = configurationDbClient.context().info().getUpdateSeq();
//		
//		final Changes changes = configurationDbClient.changes()
//			.includeDocs(true)
//			.since(since)
//			.heartBeat(10000) // ?
//			//.filter(filter)
//			.continuousChanges();
//		
//		new Thread() { // TODO: use Spring @Async for that...
//			
//			@Override
//			public void run() {
//				logger.info("change thread started");
//				
//				while (changes.hasNext()) {
//					final ChangesResult.Row feed = changes.next();
//				
//					final String docId = feed.getId();
//					final JsonObject doc = feed.getDoc();
//					
//					logger.info("CONF CACHE: got change: {} -> {}", docId, doc);
//					
//					// TODO: update in-memory cache
//				}
//				
//				changes.stop();
//			}
//		}.start();
//	}
//	
//	/**
//	 * 
//	 */
//	@PreDestroy
//	private void preDestroy() {
//		stopListeningForChanges();
//	}
//	
//	/**
//	 * 
//	 */
//	public void stopListeningForChanges() {
//		if (changes != null) {
//			changes.stop();
//		}
//	}
//}
