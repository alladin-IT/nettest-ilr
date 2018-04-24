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

import java.util.ArrayList;
import java.util.List;

/**
 * This POJO represents the result of a view query, see CouchDB 1.7.0 API reference <a
 * href="http://docs.couchdb.org/en/latest/api/ddoc/views.html#api-ddoc-view">10.5.4.
 * /db/_design/design-doc/_view/view-name</a>.
 *
 * @author rwitzel
 */
public class ViewResult {

    protected long totalRows;

    protected long updateSeq;

    protected int offset;

    protected List<ViewResultRow> rows;

    public long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
    }

    public long getUpdateSeq() {
        return updateSeq;
    }

    public void setUpdateSeq(long updateSeq) {
        this.updateSeq = updateSeq;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<ViewResultRow> getRows() {
        if (rows == null) {
            rows = new ArrayList<ViewResultRow>();
        }
        return rows;
    }

    public void setRows(List<ViewResultRow> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "ViewResult [totalRows=" + totalRows + ", updateSeq=" + updateSeq + ", offset=" + offset + ", rows="
                + rows + "]";
    }
}
