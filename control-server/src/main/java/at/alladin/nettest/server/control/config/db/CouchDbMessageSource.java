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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ResourceBundleMessageSource;

import at.alladin.nettest.server.control.config.ControlServerProperties;
import at.alladin.nettest.shared.model.Translation;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class CouchDbMessageSource extends ResourceBundleMessageSource {

	private final Logger logger = LoggerFactory.getLogger(CouchDbMessageSource.class);
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Translation> translationRepository;
	
	/**
	 * 
	 */
	@Inject
	private ControlServerProperties controlServerProperties;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.support.ResourceBundleMessageSource#doGetBundle(java.lang.String, java.util.Locale)
	 */
	@Override
	protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
		return ResourceBundle.getBundle(basename, locale, getBundleClassLoader(), new CouchDbMessageSourceControl());
	}
	
	/**
	 * 
	 * @author Specure GmbH (bp@specure.com)
	 *
	 */
	private class CouchDbMessageSourceControl extends ResourceBundle.Control {

		/*
		 * (non-Javadoc)
		 * @see java.util.ResourceBundle.Control#newBundle(java.lang.String, java.util.Locale, java.lang.String, java.lang.ClassLoader, boolean)
		 */
		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException, IOException {

			if (!"java.properties".equals(format)) { // process only for one format
				return null;
			}
			
			logger.debug("Loading translation for locale '{}' from database", locale);
			
			final Locale defaultLocale = Locale.forLanguageTag(controlServerProperties.getDefaultLocale());
			final Locale localeToUse = Locale.ROOT.equals(locale) ? defaultLocale : locale;
			
			String bundleName = toBundleName(baseName, localeToUse); // fall back to default locale for root locale

			logger.debug("Using bundle name '{}'", bundleName);
			
			ResourceBundle bundle = null;
			
			Translation translation = translationRepository.findOne(bundleName);
			if (translation != null) {
				bundle = new MapResourceBundle<String>(translation.getTranslations(), localeToUse.equals(defaultLocale));
			} else {
				if (localeToUse.equals(defaultLocale)) { // default locale was not found -> fall back to English
					logger.error("Default locale '{}' was not found in the database, falling back to Locale.ENGLISH", defaultLocale);
					bundleName = toBundleName(baseName, Locale.ENGLISH);
					translation = translationRepository.findOne(bundleName);
					if (translation != null) {
						bundle = new MapResourceBundle<String>(translation.getTranslations(), true);
					} else {
						logger.error("Locale.ENGLISH was not found in the database");
						bundle = new MapResourceBundle<String>(new HashMap<>(), true);
					}
				}
			}
				
			return bundle;
		}

//		@Override
//		public Locale getFallbackLocale(String baseName, Locale locale) {
//		}
//
//		@Override
//		public long getTimeToLive(String baseName, Locale locale) {
//		}
//
//		@Override
//		public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
//		}
	}
	
	/**
	 * 
	 * @author Specure GmbH (bp@specure.com)
	 *
	 */
	public class MapResourceBundle<V> extends ResourceBundle {
		
		/**
		 * 
		 */
		private Map<String, V> keyValueMap;
		
		/**
		 * 
		 */
		private boolean isRoot;
		
		/**
		 * 
		 * @param keyValueMap
		 * @param isRoot
		 * @throws IOException
		 */
	    public MapResourceBundle(Map<String, V> keyValueMap, boolean isRoot) throws IOException {
	        this.keyValueMap = keyValueMap;
	        this.isRoot = isRoot;
	    }

	    /*
	     * (non-Javadoc)
	     * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	     */
	    public Object handleGetObject(String key) {
	        if (key == null) {
	            throw new NullPointerException();
	        }
	        
	        final Object ret = keyValueMap.get(key);

	        if (isRoot) {
	            return ret != null ? ret : "<< missing translation for key '" + key + "' >>";
	        } else {
	        	return ret;
	        }
	    }

	    /*
	     * (non-Javadoc)
	     * @see java.util.ResourceBundle#getKeys()
	     */
	    public Enumeration<String> getKeys() {
	        return new MapResourceBundleEnumeration<>(keyValueMap.keySet(), (parent != null) ? parent.getKeys() : null);
	    }

	    /*
	     * (non-Javadoc)
	     * @see java.util.ResourceBundle#handleKeySet()
	     */
	    protected Set<String> handleKeySet() {
	        return keyValueMap.keySet();
	    }

	    /**
	     * 
	     * @author Specure GmbH (bp@specure.com)
	     *
	     * @param <T>
	     */
	    private class MapResourceBundleEnumeration<T> implements Enumeration<T> {

	    	/**
	    	 * 
	    	 */
	        private final Set<T> keySet;
	        
	        /**
	         * 
	         */
	        private final Iterator<T> keySetIterator;
	        
	        /**
	         * 
	         */
	        private final Enumeration<T> parentEnumeration;

	        /**
	         * 
	         */
	        T nextElement = null;

	        /**
	         * 
	         * @param keySet
	         * @param parentEnumeration
	         */
	        public MapResourceBundleEnumeration(Set<T> keySet, Enumeration<T> parentEnumeration) {
	            this.keySet = keySet;
	            this.keySetIterator = keySet.iterator();
	            this.parentEnumeration = parentEnumeration;
	        }

	        /*
	         * (non-Javadoc)
	         * @see java.util.Enumeration#hasMoreElements()
	         */
	        public boolean hasMoreElements() {
	            if (nextElement == null) {
	                if (keySetIterator.hasNext()) {
	                    nextElement = keySetIterator.next();
	                } else if (parentEnumeration != null) {
	                    while (nextElement == null && parentEnumeration.hasMoreElements()) {
	                        nextElement = parentEnumeration.nextElement();
	                        
	                        if (keySet.contains(nextElement)) {
	                            nextElement = null;
	                        }
	                    }
	                }
	            }
	            
	            return nextElement != null;
	        }

	        /*
	         * (non-Javadoc)
	         * @see java.util.Enumeration#nextElement()
	         */
	        public T nextElement() {
	            if (hasMoreElements()) {
	                final T result = nextElement;
	                nextElement = null;
	                return result;
	            } else {
	                throw new NoSuchElementException();
	            }
	        }
	    }
	}
}
