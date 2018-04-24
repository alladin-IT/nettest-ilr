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

package at.alladin.nettest.spring.data.couchdb.api;

import java.lang.reflect.Proxy;

import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import at.alladin.nettest.spring.data.couchdb.internal.QueryMethodHandler;
import at.alladin.nettest.spring.data.couchdb.internal.ViewParamsMerger;

/**
 * Creates {@link CouchDbCrudRepository repositories} that automatically implement a specific interface. Comparable to
 * Spring Data's {@link RepositoryFactorySupport}.
 * 
 * @author rwitzel
 */
public class CouchDbCrudRepositoryFactory {

    private ViewParamsMerger viewParamsMerger;

    public CouchDbCrudRepositoryFactory() {
        this(new ViewParamsMerger());
    }

    public CouchDbCrudRepositoryFactory(ViewParamsMerger viewParamsMerger) {
        super();
        this.viewParamsMerger = viewParamsMerger;
    }

    /**
     * Creates a repository that implements the given interface.
     * 
     * @param underlyingRepository
     *            the underlying repository
     * @param customRepository
     *            An object that implements custom finder methods. Can be null.
     * @param repositoryType
     *            the interface of the returned repository
     * @param <T> the return type
     * @return Returns the created repository.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T createRepository(CouchDbCrudRepository underlyingRepository, Object customRepository,
            Class<?> repositoryType) {

        QueryMethodHandler handler = new QueryMethodHandler(underlyingRepository, customRepository,
                viewParamsMerger);
        return (T) Proxy.newProxyInstance(repositoryType.getClassLoader(), new Class<?>[] { repositoryType }, handler);
    }

}
