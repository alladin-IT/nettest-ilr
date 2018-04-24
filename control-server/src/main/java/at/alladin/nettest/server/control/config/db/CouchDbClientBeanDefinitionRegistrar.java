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

package at.alladin.nettest.server.control.config.db;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;

import at.alladin.nettest.server.control.config.Constants;
import at.alladin.nettest.server.control.config.support.YamlConfigurationHelper;
import at.alladin.nettest.server.control.config.support.YamlConfigurationHelper.YamlConfigurationLoadingException;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class CouchDbClientBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	private final Logger logger = LoggerFactory.getLogger(CouchDbClientBeanDefinitionRegistrar.class);
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		CouchDbYamlProperties databaseProperties;
		
		try {
			Resource databaseConfigResource = null;
			
			final Path externalDatabaseConfigPath = Paths.get(Constants.EXTERNAL_CONFIG_LOCATION, Constants.DATABASES_YAML);
			if (Files.exists(externalDatabaseConfigPath, LinkOption.NOFOLLOW_LINKS)) {
				databaseConfigResource = new FileSystemResource(externalDatabaseConfigPath.toFile());
				logger.info("Loading databases.yml from path '{}'", externalDatabaseConfigPath.toString());
			} else {
				databaseConfigResource = new ClassPathResource(Constants.DATABASES_YAML_CLASSPATH);
				logger.info("Loading databases.yml from classpath '{}'", Constants.DATABASES_YAML_CLASSPATH);
			}
			
			databaseProperties = YamlConfigurationHelper.loadYamlConfigurationConfiguration(
				CouchDbYamlProperties.class, databaseConfigResource
			);
		} catch (YamlConfigurationLoadingException e) {
			logger.error("Couldn't load database configuration", e);
			return;
		}
		
		databaseProperties.getDatabases().forEach((databaseName, properties) -> {
			//if ("localhost".equals(properties.getHost()) && System.getProperty("os.name").contains("OS X")) {
			//	properties.setHost("192.168.99.100"); // docker workaround on osx
			//}

			if ("localhost".equals(properties.getHost()) && System.getProperty("user.dir").contains("/home/lb/projects/nettest-android-server/control-server")) {
				properties.setPort(5985);
			}
						
			final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CouchDbClient.class);
			
			builder.addConstructorArgValue(properties);
			builder.setDestroyMethodName("shutdown");
			builder.addPropertyReference("gsonBuilder", "couchDbGsonBuilder");
			
			final String beanName = databaseName + Constants.DB_CLIENT_NAME_SUFFIX;
			
			registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
			
			logger.info("CouchDB configuration: Creating bean {} for database {}", beanName, databaseName);
			
			if (logger.isDebugEnabled()) {
				logger.debug("'{}' config: {}", databaseName, properties);
			}
		});
	}
}
