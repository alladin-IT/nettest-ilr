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

package at.alladin.nettest.shared.model;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class MeasurementSpeedRawItem {

    /**
     * Thread id
     * 
     * (Required)
     * 
     */
    @SerializedName("thread")
    @Expose
    private Integer thread;
    
    /**
     * Time since start
     * 
     * (Required)
     * 
     */
    @SerializedName("time")
    @Expose
    private Long time;
    
    /**
     * Bytes received since start
     * 
     * (Required)
     * 
     */
    @SerializedName("bytes")
    @Expose
    private Long bytes;

    /**
     * 
     * (Required)
     * 
     * @return
     *     The thread
     */
    public Integer getThread() {
        return thread;
    }

    /**
     * 
     * (Required)
     * 
     * @param thread
     *     The thread
     */
    public void setThread(Integer thread) {
        this.thread = thread;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The time
     */
    public Long getTime() {
        return time;
    }

    /**
     * 
     * (Required)
     * 
     * @param time
     *     The time
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The bytes
     */
    public Long getBytes() {
        return bytes;
    }

    /**
     * 
     * (Required)
     * 
     * @param bytes
     *     The bytes
     */
    public void setBytes(Long bytes) {
        this.bytes = bytes;
    }

}