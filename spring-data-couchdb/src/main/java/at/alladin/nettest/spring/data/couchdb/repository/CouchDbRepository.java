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

package at.alladin.nettest.spring.data.couchdb.repository;

import java.util.List;
import java.util.Optional;

import org.lightcouch.CouchDbClient;
import org.lightcouch.View;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import at.alladin.nettest.spring.data.couchdb.api.ViewParams;

/**
 * 
 * @author Specure GmbH (bp@specure.com)
 *
 */
@NoRepositoryBean
public interface CouchDbRepository<T> extends PagingAndSortingRepository<T, String> /*, QueryByExampleExecutor<T>*/ {
    
    /**
     * 
     * @param uuid
     * @return
     */
    Optional<T> findOneByUuid(String uuid);
    
	/**
     * Queries the database with the given parameters.
     * 
     * @param viewParams the query parameters
     * @param <R> the return type, depends on {@link ViewParams#getReturnType()}.
     * @return Returns the result of the query. The type of the return value depends on {@link ViewParams#getReturnType()}.
     */
    <R> R find(ViewParams viewParams);

    /**
     * 
     * @param viewParams
     * @param pageable
     * @return
     */
    <R> R find(ViewParams viewParams, Pageable pageable);
    
    /**
     * 
     * @param viewParams
     * @return
     */
    List<T> findByView(ViewParams viewParams);
    
    /**
     * 
     * @param viewParams
     * @param pageable
     * @return
     */
    Page<T> findByView(ViewParams viewParams, Pageable pageable);
    
    CouchDbClient getCouchDbClient();
    View getView(String view);

    
}
