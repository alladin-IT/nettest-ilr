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

package at.alladin.nettest.spring.data.couchdb.api.exceptions;

import java.util.List;

/**
 * Thrown if an error happens during a bulk operation.
 * 
 * @author rwitzel
 */
public class BulkOperationException extends RuntimeException {

    private static final long serialVersionUID = 8946510245961827774L;

    private List<BulkOperationError> errors;

    public BulkOperationException(String message, List<BulkOperationError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<BulkOperationError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "BulkOperationException [errors=" + errors + ", getMessage()=" + getMessage() + "]";
    }

}
