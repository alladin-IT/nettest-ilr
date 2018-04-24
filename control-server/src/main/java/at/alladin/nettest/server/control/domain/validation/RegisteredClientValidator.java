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

package at.alladin.nettest.server.control.domain.validation;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

import at.alladin.nettest.server.control.domain.validation.annotation.RegisteredClient;
import at.alladin.nettest.shared.model.Client;
import at.alladin.nettest.spring.data.couchdb.api.CouchDbCrudRepository;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
public class RegisteredClientValidator implements ConstraintValidator<RegisteredClient, Client> {

	/**
	 * 
	 */
	@Inject
	private CouchDbCrudRepository<Client, String> clientRepository;
	
	/*
	 * (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(RegisteredClient constraintAnnotation) {
		
	}

	/*
	 * (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Client client, ConstraintValidatorContext context) {
		if (client == null) {
			return true;
		}
		
		final String clientUuid = client.getUuid();
		
		if (!StringUtils.hasLength(clientUuid)) {
			return false;
		}
		
		final Client dbClient = clientRepository.findOne(client.getUuid());
		
		if (dbClient == null || !dbClient.getTermsAndConditionsAccepted()) {
			return false;
		}
		
		return true;
	}
}
