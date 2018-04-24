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
import java.util.List;

/**
 * This class allows access to the ID and the revision of entities of a specific type.
 * 
 * @author rwitzel
 * @param <T>
 *            the type of the handled entities
 * @param <ID>
 *            the type of the ID of the handled entities
 */
public interface EntityInformation<T, ID extends Serializable> extends org.springframework.data.repository.core.EntityInformation<T, ID> {

    /**
     * Converts the given ID to a CouchDB identifier.
     * 
     * @param id the ID of the entity 
     * @return Returns the transformed identifier.
     */
    String toCouchId(ID id);

    /**
     * 
     * @param iter
     * @return
     */
    List<String> toCouchIds(Iterable<ID> iter);

    /**
     * @param entity the entity
     * @return Returns the CouchDb identifier of the entity.
     */
    String getCouchId(T entity);

    /**
     * Sets the identifier of the entity, i.e. transforms the given CouchDb identifier if necessary.
     * 
     * @param entity the entity
     * @param couchId the CouchID for the entity
     */
    void setId(T entity, String couchId);

    /**
     * @param entity the entity
     * @return Returns the revision of the given entity.
     */
    String getRev(T entity);

    /**
     * Sets the revision in the given entity.
     * 
     * @param entity the entity
     * @param rev the new revision for the entity 
     */
    void setRev(T entity, String rev);
}
