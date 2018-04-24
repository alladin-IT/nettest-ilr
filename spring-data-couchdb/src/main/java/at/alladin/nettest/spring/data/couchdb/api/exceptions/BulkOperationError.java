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

/**
 * Part of {@link BulkOperationException}.
 * 
 * @author rwitzel
 */
public class BulkOperationError {

    private String id;

    private String rev;

    private String error;

    private String reason;

    public BulkOperationError() {
        super();
    }

    public BulkOperationError(String id, String rev, String error, String reason) {
        super();
        this.id = id;
        this.rev = rev;
        this.error = error;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "BulkOperationError [id=" + id + ", rev=" + rev + ", error=" + error + ", reason=" + reason + "]";
    }
}
