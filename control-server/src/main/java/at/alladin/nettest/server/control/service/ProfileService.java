package at.alladin.nettest.server.control.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import at.alladin.nettest.shared.model.request.BasicRequest;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

import at.alladin.nettest.mango.MangoParser;
import at.alladin.nettest.mango.MangoQuery;
import at.alladin.nettest.shared.model.profiles.Profile;
import at.alladin.nettest.shared.server.helper.GsonHelper;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class ProfileService {

	private final Logger logger = LoggerFactory.getLogger(ProfileService.class);
	
	private Map<String, MangoQuery> profiles = new HashMap<>();

	private final Gson gson = GsonHelper.createRestGsonBuilder(null, null).create();
	
	/**
	 * 
	 */
	@Inject
	private CouchDbRepository<Profile> profileRepository;

	/**
	 * 
	 */
	@PostConstruct
	private void init() {
		for (final Profile profile : profileRepository.findAll()) {
			if (profile.isActive()) {
				profiles.put(profile.getName(), MangoParser.fromJsonElement(gson.toJsonTree(profile)));
				logger.debug("Found profile: {}", profile);
			}
		}
		
	}
	
	/**
	 * refresh profiles
	 */
	public void refresh() {
		profiles.clear();
		init();
	}
	
	/**
	 * returns all profile names that matched basic request
	 * @param basicRequest
	 * @return
	 */
	public List<String> matchBasicRequest(final BasicRequest basicRequest) {
		final List<String> profileList = new ArrayList<>();
		final JsonElement jsonElement = gson.toJsonTree(basicRequest);
		for (final Entry<String, MangoQuery> e : profiles.entrySet()) {
			if (e.getValue().evaluate(jsonElement)) {
				profileList.add(e.getKey());
				logger.debug(e.getKey() + " matching!");
			}
			else {
				logger.debug(e.getKey() + " not matching!");
			}
		}
		
		return profileList;
	}
}
