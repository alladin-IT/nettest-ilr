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

package at.alladin.nettest.server.control.config.support;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class YamlConfigurationHelper {

	/**
	 * 
	 */
	private YamlConfigurationHelper() {
		
	}

	public static <T> T loadYamlConfigurationConfiguration(Class<T> configurationClass, Resource... resources) {
		try {
		
			final YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
			yaml.setResources(resources);
			
			final PropertiesConfigurationFactory<T> f = new PropertiesConfigurationFactory<>(configurationClass);
			
			//f.setIgnoreInvalidFields(true);
			f.setIgnoreUnknownFields(true);
			
			//f.setProperties(yaml.getObject());
			final MutablePropertySources mutablePropertySources = new MutablePropertySources();
			mutablePropertySources.addFirst(new PropertiesPropertySource(configurationClass.getName(), yaml.getObject()));
			
			f.setPropertySources(mutablePropertySources);
			
			return f.getObject();
		} catch (Exception ex) {
			throw new YamlConfigurationLoadingException("Could not load yaml configuration for object " + configurationClass.getSimpleName(), ex);
		}
	}
	
	/**
	 * 
	 * @author Specure GmbH (bp@specure.com)
	 *
	 */
	public static class YamlConfigurationLoadingException extends RuntimeException {

		private static final long serialVersionUID = 6128932686983807357L;

		/**
		 * 
		 */
		public YamlConfigurationLoadingException() {

		}

		/**
		 * 
		 * @param message
		 * @param cause
		 * @param enableSuppression
		 * @param writableStackTrace
		 */
		public YamlConfigurationLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		/**
		 * 
		 * @param message
		 * @param cause
		 */
		public YamlConfigurationLoadingException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * 
		 * @param message
		 */
		public YamlConfigurationLoadingException(String message) {
			super(message);
		}

		/**
		 * 
		 * @param cause
		 */
		public YamlConfigurationLoadingException(Throwable cause) {
			super(cause);
		}
	}
}
