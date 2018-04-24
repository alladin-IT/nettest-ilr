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

/**
 * The row in a {@link ViewResult}.
 * 
 * @author rwitzel
 */
public class ViewResultRow {

    /**
     * The CouchDB ID of the document.
     */
    protected String id;

    protected Object key;

    protected Object value;

    protected Object doc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public <K> K getKey() {
        return (K) key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    public <V> V getValue() {
        return (V) value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public <D> D getDoc() {
        return (D) doc;
    }

    public void setDoc(Object doc) {
        this.doc = doc;
    }

    @Override
    public String toString() {
        return "ViewResultRow [id=" + id + ", key=" + key + ", value=" + value + ", doc=" + doc + "]";
    }
    
}
