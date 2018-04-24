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

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

/**
 * This {@link CrudRepository} for CouchDB databases allows you to query views.
 * 
 * @author rwitzel
 * @param <T> See type parameter in {@link CrudRepository}
 * @param <ID> See type parameter in {@link CrudRepository}
 */
public interface CouchDbCrudRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

    /**
     * Queries the database with the given parameters.
     * 
     * @param viewParams the query parameters
     * @param <R> the return type, depends on {@link ViewParams#getReturnType()}.
     * @return Returns the result of the query. The type of the return value depends on {@link ViewParams#getReturnType()}.
     */
    <R> R find(ViewParams viewParams);
}
