package at.alladin.nettest.server.control.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import at.alladin.nettest.shared.CouchDbEntity;
import at.alladin.nettest.shared.model.Settings;

/**
 * 
 * @author lb
 *
 */
@Service
public class PurgeService {

	private final Logger logger = LoggerFactory.getLogger(PurgeService.class);
	
	@Inject
	private GsonBuilder couchDbGsonBuilder;
	
	private Gson gson;
	
	@PostConstruct
	private void init() {
		this.gson = couchDbGsonBuilder.create();
	}
	
	public <T extends CouchDbEntity> T purgeIfNecessary(final T object, final Class<T> clazz, final Settings settings) {
		if (settings.getPurgeSettings() != null 
				&& settings.getPurgeSettings().getEnabled() 
				&& settings.getPurgeSettings().getFields().containsKey(object.getDoctype())) {
			
			final JsonElement json = gson.toJsonTree(object);
			if (json.isJsonObject()) {
				for (final String fullKey : settings.getPurgeSettings().getFields().get(object.getDoctype())) {
					final String[] keys = fullKey.split("\\.");
					JsonElement child = json.getAsJsonObject().get(keys[0]);
					JsonElement parent = json.getAsJsonObject(); 
					if (child != null) {
						for (int i = 1; i < keys.length; i++) {
							if (child.isJsonObject()) {
								parent = child.getAsJsonObject();
								child = child.getAsJsonObject().get(keys[i]);
							}
							
							if (child == null) {
								break;
							}
						}
					}
					
					if (child != null) {
						logger.debug("Found {}. Removing from json.", fullKey);
						//remove desired object
						((JsonObject) parent).remove(keys[keys.length-1]);
					}
					else {
						logger.debug("Could not find {}. Skipping.", fullKey);
					}
				}
			}
			
			logger.debug("After purge: {}", json);
			
			return gson.fromJson(json, clazz);
			
		}
		
		return object;
	}
}
