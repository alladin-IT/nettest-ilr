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

package at.alladin.nettest.spring.data.couchdb.support;

import at.alladin.nettest.spring.data.couchdb.api.EntityInformation;

/**
 * This {@link EntityInformation} is only for entities with ID of type <i>String</i>.
 * <p>
 * It looks up the ID and the revision in the {@link #propId} resp. {@link #propRev}.
 * 
 * @author rwitzel
 *
 * @param <T> the type of the handled entities
 */
public class SimpleEntityInformation<T> extends GenericEntityInformation<T, String> {
    
	/**
	 * 
	 */
    private String propId;
    
    /**
     * 
     */
    private String propRev;
    
    /**
     * 
     * @param type
     * @param propId
     * @param propRev
     */
    public SimpleEntityInformation(Class<T> type, String propId, String propRev) {
        super(type, String.class);
        this.propId = propId;
        this.propRev = propRev;
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.nettest.spring.data.couchdb.support.GenericEntityInformation#toCouchId(java.io.Serializable)
     */
    @Override
    public String toCouchId(String id) {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.nettest.spring.data.couchdb.support.GenericEntityInformation#getCouchId(java.lang.Object)
     */
    @Override
    public String getCouchId(T entity) {
        return (String) getPropertyValue(entity, true, propId);
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.nettest.spring.data.couchdb.support.GenericEntityInformation#getRev(java.lang.Object)
     */
    @Override
    public String getRev(T entity) {
        return (String) getPropertyValue(entity, true, propRev);
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.nettest.spring.data.couchdb.support.GenericEntityInformation#setId(java.lang.Object, java.lang.String)
     */
    @Override
    public void setId(T entity, String couchId) {
        setPropertyValue(entity, couchId, null, propId);
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.nettest.spring.data.couchdb.support.GenericEntityInformation#setRev(java.lang.Object, java.lang.String)
     */
    @Override
    public void setRev(T entity, String rev) {
        setPropertyValue(entity, rev, null, propRev);
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.nettest.spring.data.couchdb.support.GenericEntityInformation#isNew(java.lang.Object)
     */
    @Override
    public boolean isNew(T entity) {
        return getRev(entity) == null;
    }
}
