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

package at.alladin.nettest.shared.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;
import at.alladin.nettest.shared.CouchDbEntity;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class Translation extends CouchDbEntity {

	private static final long serialVersionUID = -974410986221241587L;

	/**
	 * 
	 */
	@Expose
    private String lang;
    
    /**
     * 
     */
	@Expose
	private Map<String, String> translations = new HashMap<>();


	/**
	 * 
	 * @return
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * 
	 * @param lang
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getTranslations() {
		return translations;
	}

	/**
	 * 
	 * @param translations
	 */
	public void setTranslations(Map<String, String> translations) {
		this.translations = translations;
	}
}
