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

package at.alladin.nettest.spring.data.couchdb.internal;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.spring.data.couchdb.api.ViewResult;
import at.alladin.nettest.spring.data.couchdb.api.ViewResultRow;

/**
 * Transforms Spring Data specific values to CouchDB driver specific values and the other way around. 
 * 
 * @author rwitzel
 */
public class AdapterUtils {

    /**
     * Transforms the view result to a list of keys or values or documents or IDs depending on the given return type. If
     * the return type is null, the view result is not transformed.
     * 
     * @param viewResult the original view result
     * @param returnType
     *            null or "key or "value" or "doc" or "id"
     * @param <E> the type of the elements
     * @return Returns the transformed view result.
     */
    @SuppressWarnings("unchecked")
    public static <E> E transformViewResult(ViewResult viewResult, String returnType) {

        if (returnType == null) {
            return (E) viewResult;
        } else {
            List<Object> list = new ArrayList<Object>();
            for (ViewResultRow row : viewResult.getRows()) {

                Object obj;
                if (returnType.equals("key")) {
                    obj = row.getKey();
                } else if (returnType.equals("value")) {
                    obj = row.getValue();
                } else if (returnType.equals("doc")) {
                    obj = row.getDoc();
                } else if (returnType.equals("id")) {
                    obj = row.getId();
                } else {
                    throw new IllegalArgumentException("not supported return type: " + returnType);
                }
                list.add(obj);
            }
            return (E) list;
        }
    }

}
