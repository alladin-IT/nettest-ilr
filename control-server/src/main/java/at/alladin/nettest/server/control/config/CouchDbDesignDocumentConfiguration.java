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

import javax.annotation.PostConstruct;

import org.lightcouch.CouchDbClient;
import org.lightcouch.DesignDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import at.alladin.nettest.server.control.config.db.CouchDbYamlProperties;
import at.alladin.nettest.server.control.config.db.CustomCouchDbProperties;
import at.alladin.nettest.server.control.config.support.YamlConfigurationHelper;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@Configuration
@AutoConfigureAfter({CouchDbConfiguration.class})
public class CouchDbDesignDocumentConfiguration implements ApplicationContextAware {
	
	private final Logger logger = LoggerFactory.getLogger(CouchDbDesignDocumentConfiguration.class);
	
	/**
	 * 
	 */
	private ApplicationContext applicationContext;
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;		
	}
	
	/**
	 * 
	 */
	@PostConstruct
	private void postConstruct() {
		// TODO: is there a better method than to load the databases.yml again?
		final CouchDbYamlProperties databaseProperties = YamlConfigurationHelper.loadYamlConfigurationConfiguration(
			CouchDbYamlProperties.class, new ClassPathResource(Constants.DATABASES_YAML_CLASSPATH)
		);
		
		applicationContext.getBeansOfType(CouchDbClient.class).forEach((beanName, dbClient) -> {
			// synchronize design documents of this couchDB client with the database
			
			final String dbClientDbName = beanName.substring(0, beanName.lastIndexOf(Constants.DB_CLIENT_NAME_SUFFIX));
			final CustomCouchDbProperties properties = databaseProperties.getDatabases().get(dbClientDbName);
			
			properties.getDesignDocuments().forEach(designDocumentName -> {
				logger.info("Synchronizing design document '{}' of '{}' with database", designDocumentName, beanName);

				final DesignDocument designDocument = dbClient.design().getFromDesk(designDocumentName);
				dbClient.design().synchronizeWithDb(designDocument);
			});
		});
	}
}
