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

package at.alladin.nettest.spring.data.couchdb.lightcouch;

import org.lightcouch.View;

import at.alladin.nettest.spring.data.couchdb.api.ViewParams;
import at.alladin.nettest.spring.data.couchdb.api.exceptions.UnsupportedViewParameterException;

/**
 * This internal class assists in configuring a {@link View}.
 * 
 * @author rwitzel
 */
public class LightCouchViewConfigurer {

    /**
     * Configures LightCouch's {@link View} with the given {@link ViewParams}.
     * 
     * @param view the LightCouch object to be configured
     * @param params the parameters that shall be used 
     */
    public void configure(View view, ViewParams params) {
        if (params.getAttachments() != null) {
            throw new UnsupportedViewParameterException("attachments");
        }
        if (params.getAttEncodingInfo() != null) {
            throw new UnsupportedViewParameterException("att_encoding_info");
        }
        if (params.getConflicts() != null) {
            throw new UnsupportedViewParameterException("conflicts");
        }
        if (params.getDescending() != null) {
            view.descending(params.getDescending());
        }
        if (params.getGroup() != null) {
            view.group(params.getGroup());
        }
        if (params.getIncludeDocs() != null) {
            view.includeDocs(params.getIncludeDocs());
        }
        if (params.getInclusiveEnd() != null) {
            view.inclusiveEnd(params.getInclusiveEnd());
        }
        if (params.getReduce() != null) {
            view.reduce(params.getReduce());
        }
        if (params.getUpdateSeq() != null) {
            view.updateSeq(params.getUpdateSeq());
        }
        if (params.getEndKey() != null) {
            view.endKey(params.getEndKey());
        }
        if (params.getEndKeyDocId() != null) {
            view.endKeyDocId(params.getEndKeyDocId());
        }
        if (params.getGroupLevel() != null) {
            view.groupLevel(params.getGroupLevel());
        }
        if (params.getKey() != null) {
            view.key(params.getKey());
        }
        if (params.getLimit() != null) {
            view.limit(params.getLimit());
        }
        if (params.getSkip() != null) {
            view.skip(params.getSkip());
        }
        if (params.getStale() != null) {
            view.stale(params.getStale());
        }
        if (params.getStartKey() != null) {
            view.startKey(params.getStartKey());
        }
        if (params.getStartKeyDocId() != null) {
            view.startKeyDocId(params.getStartKeyDocId());
        }
        if (params.getView() != null) {
            view.skip(params.getSkip());
        }
    }

}
