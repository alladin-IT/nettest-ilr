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

package at.alladin.nettest.server.control.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import at.alladin.nettest.server.control.config.ControlServerProperties;
import at.alladin.nettest.shared.model.Settings;

import at.alladin.nettest.shared.server.helper.ClassificationHelper;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationItem;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationType;

@Service
public class ClassificationService {

	@Inject
	private SettingsService settingsService;
	
	@Inject
	private ControlServerProperties controlServerProperties;
	
	private ClassificationHelper classificationHelper;
	
	private boolean needsInit = true;
	
//	@PostConstruct
	public void init() {
        final Settings settings = settingsService.getSettings(controlServerProperties.getSettingsKey());
        classificationHelper = ClassificationHelper.initInstance(settings);
    }

    public int classify(final int[] threshold, final long value)
    {
    	if(needsInit) {
    		needsInit = false;
    		this.init();
    	}
    	return classificationHelper.classify(threshold, value);
    }

    public int classify(final ClassificationType type, final long value) {
    	if(needsInit) {
    		needsInit = false;
    		this.init();
    	}
    	return classificationHelper.classify(type, value);
    }
    
//    public ClassificationItem classifyColor(final ClassificationType type, final long value) {
//    	return classificationHelper.classifyColor(type, value, null);
//    }
    
    /**
     * 
     * @param type
     * @param value
     * @param networkType the type to be classified (the networktype of the first elem in the signals array of a measurment, or else the network_info.network_type)
     * @return
     */
    public ClassificationItem classifyColor(final ClassificationType type, final long value, final Integer networkType) {
    	if(needsInit) {
    		needsInit = false;
    		this.init();
    	}
    	return classificationHelper.classifyColor(type, value, networkType);
    }

}
