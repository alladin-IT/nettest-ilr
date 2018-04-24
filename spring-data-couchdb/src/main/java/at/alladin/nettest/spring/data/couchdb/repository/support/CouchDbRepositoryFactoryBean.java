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

package at.alladin.nettest.spring.data.couchdb.repository.support;

import java.io.Serializable;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 * @param <T>
 * @param <S>
 * @param <ID>
 */
public class CouchDbRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends RepositoryFactoryBeanSupport<T, S, ID> {

	/**
	 * 
	 */
	public CouchDbRepositoryFactoryBean() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#createRepositoryFactory()
	 */
	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {
		return new CouchDbRepositoryFactory();
	}
}
