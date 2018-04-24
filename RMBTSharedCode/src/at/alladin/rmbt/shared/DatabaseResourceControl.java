/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

package at.alladin.rmbt.shared;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
class DatabaseResourceControl extends ResourceBundle.Control {

	/**
	 * 
	 */
	private final Connection connection;
	
	/**
	 * 
	 */
	public DatabaseResourceControl(Connection connection) {
		this.connection = connection;
	}
	
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
		
		System.out.println("Loading translation for locale '{" + locale + "}' from database");
		
		final Locale localeToUse = Locale.ROOT.equals(locale) ? Locale.getDefault() : locale;
		
		String bundleName = toBundleName(baseName, localeToUse); // fall back to default locale for root locale

		System.out.println("Using bundle name '{" + bundleName + "}'");
		
		ResourceBundle bundle = null;
		
		final Gson gson = new GsonBuilder().create();
		
		try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT json->>'translations' as json FROM ha_translation WHERE lang = ?")) {
		
			preparedStatement.setString(1, localeToUse.toString());
			
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				Map<String, String> translationMap = gson.fromJson(resultSet.getString("json"), new TypeToken<HashMap<String, String>>(){}.getType());
				if (translationMap != null) {
					bundle = new MapResourceBundle<String>(translationMap, localeToUse.equals(Locale.getDefault()));
				} else {
					if (localeToUse.equals(Locale.getDefault())) { // default locale was not found -> fall back to English
						System.out.println("Default locale '{" + Locale.getDefault() + "}' was not found in the database, falling back to Locale.ENGLISH");
						bundleName = toBundleName(baseName, Locale.ENGLISH);
						
						resultSet.close();
						
						preparedStatement.clearParameters();
						preparedStatement.setString(1, Locale.getDefault().toString());
						
						resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							translationMap = gson.fromJson(resultSet.getString("json"), new TypeToken<HashMap<String, String>>(){}.getType());
							if (translationMap != null) {
								bundle = new MapResourceBundle<String>(translationMap, true);
							} else {
								System.out.println("Locale.ENGLISH was not found in the database");
								bundle = new MapResourceBundle<String>(new HashMap<String, String>(), true);
							}
						}
					}
				}
				
				// why does it get called 2 times for English? TODO: don't load anything for root locale?
				//System.out.println("TRANSLATION MAP");
				//System.out.println(translationMap);
			}

			resultSet.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
			
		return bundle;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.ResourceBundle.Control#getTimeToLive(java.lang.String, java.util.Locale)
	 */
	@Override
	public long getTimeToLive(String baseName, Locale locale) {
		//return super.getTimeToLive(baseName, locale);
		return 5 * 60 * 1000; // TODO: improve this, currently it reloads every 5min
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.ResourceBundle.Control#needsReload(java.lang.String, java.util.Locale, java.lang.String, java.lang.ClassLoader, java.util.ResourceBundle, long)
	 */
	@Override
	public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
		//System.out.println("NEEDS RELOAD?");
		return true; // TODO: improve this, currently it reloads every 5min
	}
}

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
class MapResourceBundle<V> extends ResourceBundle {
	
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
        
        //System.out.println("root: " + isRoot + " -> map: [" + keyValueMap + "]");
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
