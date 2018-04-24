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

import static at.alladin.nettest.server.control.config.support.YamlConfigurationHelper.loadYamlConfigurationConfiguration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import at.alladin.nettest.server.control.config.ControlServerProperties;
import at.alladin.nettest.shared.model.Device;
import at.alladin.nettest.shared.model.MeasurementServer;
import at.alladin.nettest.shared.model.NetworkType;
import at.alladin.nettest.shared.model.Provider;
import at.alladin.nettest.shared.model.Settings;
import at.alladin.nettest.shared.model.Translation;
import at.alladin.nettest.shared.model.qos.QosMeasurementObjective;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

import at.alladin.nettest.shared.model.profiles.Profile;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class DevelopmentDataLoader {

	private final Logger logger = LoggerFactory.getLogger(DevelopmentDataLoader.class);
	
	/**
	 * 
	 */
	@Inject
	private ControlServerProperties controlServerProperties;
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Settings> settingsRepository;

	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Device> deviceRepository;

	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Profile> profileRepository;

	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Provider> providerRepository;
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<MeasurementServer> measurementServerRepository;
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<QosMeasurementObjective> qosMeasurementObjectiveRepository;
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Translation> translationRepository;

	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<NetworkType> networkTypeRepository;
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private Resource getDevDatabaseYamlResource(String name) {
		return new ClassPathResource("db/dev-db-" + name + ".yml");
	}
	
	/**
	 * 
	 */
	@PostConstruct
	private void postConstruct() {
		logger.debug("Filling settings database");
		
		if (settingsRepository.count() == 0) {
			final Settings settings = loadYamlConfigurationConfiguration(Settings.class, getDevDatabaseYamlResource("settings"));
			
			settings.setId(controlServerProperties.getSettingsKey());
			
			settingsRepository.save(settings);
		}
		
		logger.debug("Filling configuration database");
		
		if (deviceRepository.count() == 0) {
			final DeviceDevConfig deviceDevConfig = loadYamlConfigurationConfiguration(DeviceDevConfig.class, getDevDatabaseYamlResource("devices"));
			
			deviceDevConfig.getDevices().parallelStream().forEach(device -> {
				device.setId("device__" + device.getCodename());
				
				deviceRepository.save(device);
			});
		}
		
		if (providerRepository.count() == 0) {
			final ProviderDevConfig providerDevConfig = loadYamlConfigurationConfiguration(ProviderDevConfig.class, getDevDatabaseYamlResource("providers"));
			
			providerDevConfig.getProviders().parallelStream().forEach(provider -> {
				provider.setId("provider__" + provider.getName());
				
				providerRepository.save(provider);
			});
		}
		
		if (measurementServerRepository.count() == 0) {
			final MeasurementServerDevConfig msMap = loadYamlConfigurationConfiguration(MeasurementServerDevConfig.class, getDevDatabaseYamlResource("measurement-servers"));
			
			msMap.getMeasurementServers().parallelStream().forEach(msObject -> {
				msObject.setId("measurement_server__" + msObject.getWebAddress());
				
				measurementServerRepository.save(msObject);
			});
		}
		
		// Qos
		
		if (qosMeasurementObjectiveRepository.count() == 0) {
			final QosMeasurementObjectiveDevConfig qosMeasurementObjectiveDevConfig = loadYamlConfigurationConfiguration(QosMeasurementObjectiveDevConfig.class, getDevDatabaseYamlResource("qos-measurement-objectives"));
			
			qosMeasurementObjectiveDevConfig.getObjectives().parallelStream().forEach(objective -> {
				objective.setId(
					"qos_measurement_objective__" + 
					objective.getObjectiveId() + "_" + 
					objective.getType().toString() + "_" + 
					objective.getMeasurementClass() + "_" + 
					objective.getConcurrencyGroup()
				);
				
				// cast all params to string (TODO: find better solution...)
				for (String key : objective.getParams().keySet()) {
					objective.getParams().put(key, objective.getParams().get(key).toString());
				}
				
				qosMeasurementObjectiveRepository.save(objective);
			});
		}
		
		// Translations
		if (translationRepository.count() == 0) {
			final TranslationDevConfig translationDevConfig = loadYamlConfigurationConfiguration(TranslationDevConfig.class, getDevDatabaseYamlResource("translations"));
			
			translationDevConfig.getTranslationLanguages().parallelStream().forEach(translation -> {
				translation.setId("language_" + translation.getLang());
				
				translationRepository.save(translation);
			});
		}
		
		if (networkTypeRepository.count() == 0) {
			final NetworkTypeDevConfig ntMap = loadYamlConfigurationConfiguration(NetworkTypeDevConfig.class, getDevDatabaseYamlResource("network-types"));
			
			ntMap.getNetworkTypes().parallelStream().forEach(ntObject -> {
				ntObject.setId("network_type_" + ntObject.getName());
				
				networkTypeRepository.save(ntObject);
			});
		}

		if (profileRepository.count() == 0) {
			final ProfileDevConfig profileMap = loadYamlConfigurationConfiguration(ProfileDevConfig.class, getDevDatabaseYamlResource("profiles"));
			
			profileMap.getProfiles().parallelStream().forEach(profile -> {
				profile.setId("profile_" + profile.getName());
				
				profileRepository.save(profile);
			});
		}
	}
	
	/**
	 * 
	 * @author Specure GmbH (bp@specure.com)
	 *
	 */
	public static class DeviceDevConfig {
		
		/**
		 * 
		 */
		private final List<Device> devices = new ArrayList<>();
		
		/**
		 * 
		 * @return
		 */
		public List<Device> getDevices() {
			return devices;
		}
	}
	
	/**
	 * 
	 * @author Specure GmbH (bp@specure.com)
	 *
	 */
	public static class ProviderDevConfig {
		
		/**
		 * 
		 */
		private final List<Provider> providers = new ArrayList<>();
		
		/**
		 * 
		 * @return
		 */
		public List<Provider> getProviders() {
			return providers;
		}
	}
	
	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	public static class MeasurementServerDevConfig {
		
		/**
		 * 
		 */
		private final List<MeasurementServer> measurementServers = new ArrayList<>();
		
		/**
		 * 
		 * @return
		 */
		public List<MeasurementServer> getMeasurementServers() {
			return measurementServers;
		}
	}

	/**
	 * 
	 * @author lb@specure.com
	 *
	 */
	public static class NetworkTypeDevConfig {
		
		/**
		 * 
		 */
		private final List<NetworkType> networkTypes = new ArrayList<>();
		
		/**
		 * 
		 * @return
		 */
		public List<NetworkType> getNetworkTypes() {
			return networkTypes;
		}
	}

	/**
	 * 
	 * @author Specure GmbH (bp@specure.com)
	 *
	 */
	public static class QosMeasurementObjectiveDevConfig {
		
		/**
		 * 
		 */
		private final List<QosMeasurementObjective> objectives = new ArrayList<>();
		
		/**
		 * 
		 * @return
		 */
		public List<QosMeasurementObjective> getObjectives() {
			return objectives;
		}
	}
	
	/**
	 * 
	 * @author Specure GmbH (bp@specure.com)
	 *
	 */
	public static class TranslationDevConfig {
		
		/**
		 * 
		 */
		private final List<Translation> translationLanguages = new ArrayList<>();
		
		/**
		 * 
		 * @return
		 */
		public List<Translation> getTranslationLanguages() {
			return translationLanguages;
		}
	}

	/**
	 * 
	 * @author lb@alladin.at
	 *
	 */
	public static class ProfileDevConfig {
		
		/**
		 * 
		 */
		private final List<Profile> profiles = new ArrayList<>();
		
		/**
		 * 
		 * @return
		 */
		public List<Profile> getProfiles() {
			return profiles;
		}
	}
}
