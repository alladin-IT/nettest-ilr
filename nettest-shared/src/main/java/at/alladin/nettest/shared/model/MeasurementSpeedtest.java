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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.model.response.MeasurementRequestResponse.TargetMeasurementServer;

/**
 * 
 * @author lb@specure.com
 *
 */
@Generated("org.jsonschema2pojo")
public class MeasurementSpeedtest {

    /**
     * NOW() during insert = test request/registration
     * (Required)
     * 
     */
    @SerializedName("time")
    @Expose
    private DateTime time;
    
    /**
     * Upload speed in kbit
     * 
     * (Required)
     * 
     */
    @SerializedName("speed_upload")
    @Expose
    private long speedUpload;
    
    /**
     * Download speed in kbit
     * 
     * (Required)
     * 
     */
    @SerializedName("speed_download")
    @Expose
    private long speedDownload;
    
    /**
     * Shortest ping
     */
    @SerializedName("ping_shortest")
    @Expose
    private Long pingShortest;
    
    /**
     * Variance of pings
     */
    @SerializedName("ping_variance")
    @Expose
    private Double pingVariance;
    
    /**
     * Encryption used
     * 
     * (Required)
     * 
     */
    @SerializedName("encryption")
    @Expose
    private String encryption;
    
    /**
     * the nominal duration (previously: 'duration')
     * 
     * (Required)
     * 
     */
    @SerializedName("nominal_duration")
    @Expose
    private long duration;
    
    /**
     * total speed test duration (previously: 'speed_test_duration', and not in ns)
     */
    @SerializedName("total_test_duration_ns")
    @Expose
    private long totalDurationNs;
    
    /**
     * set by the trigger trigger_test(), can have following values: 'FINISHED', 'STARTED', 'UPDATE ERROR', 'ERROR'. Maybe enum is an option here?
     * 
     * (Required)
     * 
     */
    @SerializedName("status")
    @Expose
    private MeasurementSpeedtest.Status status;
    
    /**
     * Bytes downloaded
     * 
     * (Required)
     * 
     */
    @SerializedName("bytes_download")
    @Expose
    private long bytesDownload;
    
    /**
     * Bytes uploaded
     * 
     * (Required)
     * 
     */
    @SerializedName("bytes_upload")
    @Expose
    private long bytesUpload;
    
    /**
     * renamed from nsec_download
     * 
     * (Required)
     * 
     */
    @SerializedName("duration_download_ns")
    @Expose
    private long durationDownloadNs;
    
    /**
     * renamed from nsec_upload
     * 
     * (Required)
     * 
     */
    @SerializedName("duration_upload_ns")
    @Expose
    private long durationUploadNs;
    
    /**
     * Logarithm of upload speed
     */
    @SerializedName("speed_upload_log")
    @Expose
    private double speedUploadLog;
    
    /**
     * Logarithm of download speed
     */
    @SerializedName("speed_download_log")
    @Expose
    private double speedDownloadLog;
    
    /**
     * Total bytes downloaded
     * 
     * (Required)
     * 
     */
    @SerializedName("total_bytes_download")
    @Expose
    private long totalBytesDownload;
    
    /**
     * Total bytes uploaded
     * 
     * (Required)
     * 
     */
    @SerializedName("total_bytes_upload")
    @Expose
    private long totalBytesUpload;
    
    /**
     * Total bytes downloaded as reported by the interface
     * renamed from test_if_bytes_download
     * 
     * (Required)
     * 
     */
    @SerializedName(value="interface_total_bytes_download", alternate="test_if_bytes_download")
    @Expose
    private Long interfaceTotalBytesDownload;
    
    /**
     * Total bytes uploaded as reported by the interface
     * renamed from test_if_bytes_upload
     * 
     * (Required)
     * 
     */
    @SerializedName(value="interface_total_bytes_upload", alternate="test_if_bytes_upload")
    @Expose
    private Long interfaceTotalBytesUpload;
    
    /**
     * Bytes downloaded during downlink test as reported by the interface
     * renamed from testdl_if_bytes_download
     * 
     * (Required)
     * 
     */
    @SerializedName(value="interface_dltest_bytes_download", alternate="testdl_if_bytes_download")
    @Expose
    private Long interfaceDltestBytesDownload;
    
    /**
     * Bytes uploaded during downlink test as reported by the interface
     * renamed from testdl_if_bytes_upload
     * 
     * (Required)
     * 
     */
    @SerializedName(value="interface_dltest_bytes_upload", alternate="testdl_if_bytes_upload")
    @Expose
    private Long interfaceDltestBytesUpload;
    
    /**
     * Bytes downloaded during uplink test as reported by the interface
     * renamed from testul_if_bytes_download
     * 
     * (Required)
     * 
     */
    @SerializedName(value="interface_ultest_bytes_download", alternate="testul_if_bytes_download")
    @Expose
    private Long interfaceUltestBytesDownload;
    
    /**
     * Bytes uploaded during uplink test as reported by the interface
     * renamed from testul_if_bytes_upload
     * 
     * (Required)
     * 
     */
    @SerializedName(value="interface_ultest_bytes_upload", alternate="testul_if_bytes_upload")
    @Expose
    private Long interfaceUltestBytesUpload;
    
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("test_slot")
    @Expose
    private Long testSlot;
    
    /**
     * Logarithm of shortest ping
     */
    @SerializedName("ping_shortest_log")
    @Expose
    private Double pingShortestLog;
    
    /**
     * Was ndt run
     * 
     * (Required)
     * 
     */
    @SerializedName("run_ndt")
    @Expose
    private Boolean runNdt;
    
    /**
     * Median of pings
     */
    @SerializedName("ping_median")
    @Expose
    private Long pingMedian;
    
    /**
     * Logarithm of ping median
     */
    @SerializedName("ping_median_log")
    @Expose
    private Double pingMedianLog;
    
    /**
     * Relative start time of downlink test in ns
     * renamed from time_dl_ns
     * 
     * (Required)
     * 
     */
    @SerializedName("relative_time_dl_ns")
    @Expose
    private Long relativeTimeDlNs;
    
    /**
     * Relative start time of uplink test in ns
     * renamed from time_ul_ns
     * 
     * (Required)
     * 
     */
    @SerializedName("relative_time_ul_ns")
    @Expose
    private Long relativeTimeUlNs;
    
    /**
     * number of threads requested by control-server
     * 
     * (Required)
     * 
     */
    @SerializedName("num_threads_requested")
    @Expose
    private Long numThreadsRequested;
    
    /**
     * number of threads used in downlink throughput test (uplink may differ)
     * 
     * (Required)
     * 
     */
    @SerializedName("num_threads")
    @Expose
    private Long numThreads;
    
    /**
     * number of threads used in uplink test
     * not sure where it comes from. cannot find in code neither in trigger. about 1000 tests have a not null value, more than 300K test have a null value
     */
    @SerializedName("num_threads_ul")
    @Expose
    private Long numThreadsUl;
        
    /**
     * holds the target measurement server information
     * 
     * (Required)
     * 
     */
    @SerializedName("target_measurement_server")
    @Expose
    private TargetMeasurementServer targetMeasurementServer;

	/**
	 * Holds ping data
	 * replaces ping table
	 * 
	 * (Required)
	 * 
	 */
	@SerializedName("pings")
	@Expose
	private List<Ping> pings = new ArrayList<>();

	/**
	 * Holds only raw speed data
	 * replaces test_speed table, holds only raw speed data
	 * 
	 */
	@SerializedName("speed_raw")
	@Expose
	private MeasurementSpeedRaw speedRaw;
	
	/**
	 * Source port mappings
	 */
	@SerializedName("source_ports")
	@Expose
	private MeasurementSpeedtestPortMap sourcePorts;

    /**
     * NOW() during insert = test request/registriation
     * 
     * (Required)
     * 
     * @return
     *     The time
     */
    public DateTime getTime() {
        return time;
    }

    /**
     * NOW() during insert = test request/registriation
     * (Required)
     * 
     * @param time
     *     The time
     */
    public void setTime(DateTime time) {
        this.time = time;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The speedUpload
     */
    public long getSpeedUpload() {
        return speedUpload;
    }

    /**
     * 
     * (Required)
     * 
     * @param speedUpload
     *     The speed_upload
     */
    public void setSpeedUpload(long speedUpload) {
        this.speedUpload = speedUpload;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The speedDownload
     */
    public long getSpeedDownload() {
        return speedDownload;
    }

    /**
     * 
     * (Required)
     * 
     * @param speedDownload
     *     The speed_download
     */
    public void setSpeedDownload(long speedDownload) {
        this.speedDownload = speedDownload;
    }

    /**
     * 
     * @return
     *     The pingShortest
     */
    public Long getPingShortest() {
        return pingShortest;
    }

    /**
     * 
     * @param pingShortest
     *     The ping_shortest
     */
    public void setPingShortest(Long pingShortest) {
        this.pingShortest = pingShortest;
    }

    /**
     * 
     * @return
     *     The pingVariance
     */
    public double getPingVariance() {
        return pingVariance;
    }

    /**
     * 
     * @param pingVariance
     *     The ping_variance
     */
    public void setPingVariance(Double pingVariance) {
        this.pingVariance = pingVariance;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The encryption
     */
    public String getEncryption() {
        return encryption;
    }

    /**
     * 
     * (Required)
     * 
     * @param encryption
     *     The encryption
     */
    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    /**
     * nominal test duration
     * (Required)
     * 
     * @return
     *     The duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * nominal test duration
     * (Required)
     * 
     * @param duration
     *     The duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * speed_test_duration
     * @return
     */
    public long getTotalDurationNs() {
		return totalDurationNs;
	}

    /**
     * speed_test_duration
     * @param totalDuration
     */
	public void setTotalDurationNs(long totalDuration) {
		this.totalDurationNs = totalDuration;
	}

	/**
     * set by the trigger trigger_test(), can have following values: 'FINISHED', 'STARTED', 'UPDATE ERROR', 'ERROR'. Maybe enum is an option here?
     * (Required)
     * 
     * @return
     *     The status
     */
    public MeasurementSpeedtest.Status getStatus() {
        return status;
    }

    /**
     * set by the trigger trigger_test(), can have following values: 'FINISHED', 'STARTED', 'UPDATE ERROR', 'ERROR'. Maybe enum is an option here?
     * (Required)
     * 
     * @param status
     *     The status
     */
    public void setStatus(MeasurementSpeedtest.Status status) {
        this.status = status;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The bytesDownload
     */
    public long getBytesDownload() {
        return bytesDownload;
    }

    /**
     * 
     * (Required)
     * 
     * @param bytesDownload
     *     The bytes_download
     */
    public void setBytesDownload(long bytesDownload) {
        this.bytesDownload = bytesDownload;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The bytesUpload
     */
    public long getBytesUpload() {
        return bytesUpload;
    }

    /**
     * 
     * (Required)
     * 
     * @param bytesUpload
     *     The bytes_upload
     */
    public void setBytesUpload(long bytesUpload) {
        this.bytesUpload = bytesUpload;
    }

    /**
     * renamed from nsec_download
     * (Required)
     * 
     * @return
     *     The durationDownloadNs
     */
    public long getDurationDownloadNs() {
        return durationDownloadNs;
    }

    /**
     * renamed from nsec_download
     * (Required)
     * 
     * @param durationDownloadNs
     *     The duration_download_ns
     */
    public void setDurationDownloadNs(long durationDownloadNs) {
        this.durationDownloadNs = durationDownloadNs;
    }

    /**
     * renamed from nsec_upload
     * (Required)
     * 
     * @return
     *     The durationUploadNs
     */
    public long getDurationUploadNs() {
        return durationUploadNs;
    }

    /**
     * renamed from nsec_upload
     * (Required)
     * 
     * @param durationUploadNs
     *     The duration_upload_ns
     */
    public void setDurationUploadNs(long durationUploadNs) {
        this.durationUploadNs = durationUploadNs;
    }

    /**
     * 
     * @return
     *     The speedUploadLog
     */
    public double getSpeedUploadLog() {
        return speedUploadLog;
    }

    /**
     * 
     * @param speedUploadLog
     *     The speed_upload_log
     */
    public void setSpeedUploadLog(double speedUploadLog) {
        this.speedUploadLog = speedUploadLog;
    }

    /**
     * 
     * @return
     *     The speedDownloadLog
     */
    public double getSpeedDownloadLog() {
        return speedDownloadLog;
    }

    /**
     * 
     * @param speedDownloadLog
     *     The speed_download_log
     */
    public void setSpeedDownloadLog(double speedDownloadLog) {
        this.speedDownloadLog = speedDownloadLog;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The totalBytesDownload
     */
    public long getTotalBytesDownload() {
        return totalBytesDownload;
    }

    /**
     * 
     * (Required)
     * 
     * @param totalBytesDownload
     *     The total_bytes_download
     */
    public void setTotalBytesDownload(long totalBytesDownload) {
        this.totalBytesDownload = totalBytesDownload;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The totalBytesUpload
     */
    public long getTotalBytesUpload() {
        return totalBytesUpload;
    }

    /**
     * 
     * (Required)
     * 
     * @param totalBytesUpload
     *     The total_bytes_upload
     */
    public void setTotalBytesUpload(long totalBytesUpload) {
        this.totalBytesUpload = totalBytesUpload;
    }

    /**
     * renamed from test_if_bytes_download
     * (Required)
     * 
     * @return
     *     The interfaceTotalBytesDownload
     */
    public long getInterfaceTotalBytesDownload() {
        return interfaceTotalBytesDownload;
    }

    /**
     * renamed from test_if_bytes_download
     * (Required)
     * 
     * @param interfaceTotalBytesDownload
     *     The interface_total_bytes_download
     */
    public void setInterfaceTotalBytesDownload(Long interfaceTotalBytesDownload) {
        this.interfaceTotalBytesDownload = interfaceTotalBytesDownload;
    }

    /**
     * renamed from test_if_bytes_upload
     * (Required)
     * 
     * @return
     *     The interfaceTotalBytesUpload
     */
    public long getInterfaceTotalBytesUpload() {
        return interfaceTotalBytesUpload;
    }

    /**
     * renamed from test_if_bytes_upload
     * (Required)
     * 
     * @param interfaceTotalBytesUpload
     *     The interface_total_bytes_upload
     */
    public void setInterfaceTotalBytesUpload(Long interfaceTotalBytesUpload) {
        this.interfaceTotalBytesUpload = interfaceTotalBytesUpload;
    }

    /**
     * renamed from testdl_if_bytes_download
     * (Required)
     * 
     * @return
     *     The interfaceDltestBytesDownload
     */
    public long getInterfaceDltestBytesDownload() {
        return interfaceDltestBytesDownload;
    }

    /**
     * renamed from testdl_if_bytes_download
     * (Required)
     * 
     * @param interfaceDltestBytesDownload
     *     The interface_dltest_bytes_download
     */
    public void setInterfaceDltestBytesDownload(Long interfaceDltestBytesDownload) {
        this.interfaceDltestBytesDownload = interfaceDltestBytesDownload;
    }

    /**
     * renamed from testdl_if_bytes_upload
     * (Required)
     * 
     * @return
     *     The interfaceDltestBytesUpload
     */
    public Long getInterfaceDltestBytesUpload() {
        return interfaceDltestBytesUpload;
    }

    /**
     * renamed from testdl_if_bytes_upload
     * (Required)
     * 
     * @param interfaceDltestBytesUpload
     *     The interface_dltest_bytes_upload
     */
    public void setInterfaceDltestBytesUpload(Long interfaceDltestBytesUpload) {
        this.interfaceDltestBytesUpload = interfaceDltestBytesUpload;
    }

    /**
     * renamed from testul_if_bytes_download
     * (Required)
     * 
     * @return
     *     The interfaceUltestBytesDownload
     */
    public Long getInterfaceUltestBytesDownload() {
        return interfaceUltestBytesDownload;
    }

    /**
     * renamed from testul_if_bytes_download
     * (Required)
     * 
     * @param interfaceUltestBytesDownload
     *     The interface_ultest_bytes_download
     */
    public void setInterfaceUltestBytesDownload(Long interfaceUltestBytesDownload) {
        this.interfaceUltestBytesDownload = interfaceUltestBytesDownload;
    }

    /**
     * renamed from testul_if_bytes_upload
     * (Required)
     * 
     * @return
     *     The interfaceUltestBytesUpload
     */
    public long getInterfaceUltestBytesUpload() {
        return interfaceUltestBytesUpload;
    }

    /**
     * renamed from testul_if_bytes_upload
     * (Required)
     * 
     * @param interfaceUltestBytesUpload
     *     The interface_ultest_bytes_upload
     */
    public void setInterfaceUltestBytesUpload(Long interfaceUltestBytesUpload) {
        this.interfaceUltestBytesUpload = interfaceUltestBytesUpload;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The testSlot
     */
    public long getTestSlot() {
        return testSlot;
    }

    /**
     * 
     * (Required)
     * 
     * @param testSlot
     *     The test_slot
     */
    public void setTestSlot(long testSlot) {
        this.testSlot = testSlot;
    }

    /**
     * 
     * @return
     *     The pingShortestLog
     */
    public double getPingShortestLog() {
        return pingShortestLog;
    }

    /**
     * 
     * @param pingShortestLog
     *     The ping_shortest_log
     */
    public void setPingShortestLog(Double pingShortestLog) {
        this.pingShortestLog = pingShortestLog;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The runNdt
     */
    public Boolean getRunNdt() {
        return runNdt;
    }

    /**
     * 
     * (Required)
     * 
     * @param runNdt
     *     The run_ndt
     */
    public void setRunNdt(Boolean runNdt) {
        this.runNdt = runNdt;
    }

    /**
     * 
     * @return
     *     The pingMedian
     */
    public Long getPingMedian() {
        return pingMedian;
    }

    /**
     * 
     * @param pingMedian
     *     The ping_median
     */
    public void setPingMedian(Long pingMedian) {
        this.pingMedian = pingMedian;
    }

    /**
     * 
     * @return
     *     The pingMedianLog
     */
    public double getPingMedianLog() {
        return pingMedianLog;
    }

    /**
     * 
     * @param pingMedianLog
     *     The ping_median_log
     */
    public void setPingMedianLog(Double pingMedianLog) {
        this.pingMedianLog = pingMedianLog;
    }

    /**
     * renamed from time_dl_ns
     * (Required)
     * 
     * @return
     *     The relativeTimeDlNs
     */
    public Long getRelativeTimeDlNs() {
        return relativeTimeDlNs;
    }

    /**
     * renamed from time_dl_ns
     * (Required)
     * 
     * @param relativeTimeDlNs
     *     The relative_time_dl_ns
     */
    public void setRelativeTimeDlNs(Long relativeTimeDlNs) {
        this.relativeTimeDlNs = relativeTimeDlNs;
    }

    /**
     * renamed from time_ul_ns
     * (Required)
     * 
     * @return
     *     The relativeTimeUlNs
     */
    public long getRelativeTimeUlNs() {
        return relativeTimeUlNs;
    }

    /**
     * renamed from time_ul_ns
     * (Required)
     * 
     * @param relativeTimeUlNs
     *     The relative_time_ul_ns
     */
    public void setRelativeTimeUlNs(Long relativeTimeUlNs) {
        this.relativeTimeUlNs = relativeTimeUlNs;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The numThreadsRequested
     */
    public long getNumThreadsRequested() {
        return numThreadsRequested;
    }

    /**
     * 
     * (Required)
     * 
     * @param numThreadsRequested
     *     The num_threads_requested
     */
    public void setNumThreadsRequested(Long numThreadsRequested) {
        this.numThreadsRequested = numThreadsRequested;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The numThreads
     */
    public long getNumThreads() {
        return numThreads;
    }

    /**
     * 
     * (Required)
     * 
     * @param numThreads
     *     The num_threads
     */
    public void setNumThreads(long numThreads) {
        this.numThreads = numThreads;
    }

    /**
     * not sure where it comes from. cannot find in code neither in trigger. about 1000 tests have a not null value, more than 300K test have a null value
     * 
     * @return
     *     The numThreadsUl
     */
    public Long getNumThreadsUl() {
        return numThreadsUl;
    }

    /**
     * not sure where it comes from. cannot find in code neither in trigger. about 1000 tests have a not null value, more than 300K test have a null value
     * 
     * @param numThreadsUl
     *     The num_threads_ul
     */
    public void setNumThreadsUl(Long numThreadsUl) {
        this.numThreadsUl = numThreadsUl;
    }
    
    public TargetMeasurementServer getTargetMeasurementServer() {
		return targetMeasurementServer;
	}

	public void setTargetMeasurementServer(TargetMeasurementServer targetMeasurementServer) {
		this.targetMeasurementServer = targetMeasurementServer;
	}

	public List<Ping> getPings() {
		return pings;
	}

	public void setPings(List<Ping> pings) {
		this.pings = pings;
	}

	public MeasurementSpeedRaw getSpeedRaw() {
		return speedRaw;
	}

	public void setSpeedRaw(MeasurementSpeedRaw speedRaw) {
		this.speedRaw = speedRaw;
	}

	public MeasurementSpeedtestPortMap getSourcePorts() {
		return sourcePorts;
	}

	public void setSourcePorts(MeasurementSpeedtestPortMap sourcePorts) {
		this.sourcePorts = sourcePorts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (bytesDownload ^ (bytesDownload >>> 32));
		result = prime * result + (int) (bytesUpload ^ (bytesUpload >>> 32));
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + (int) (durationDownloadNs ^ (durationDownloadNs >>> 32));
		result = prime * result + (int) (durationUploadNs ^ (durationUploadNs >>> 32));
		result = prime * result + ((encryption == null) ? 0 : encryption.hashCode());
		result = prime * result + (int) (interfaceDltestBytesDownload ^ (interfaceDltestBytesDownload >>> 32));
		result = prime * result + (int) (interfaceDltestBytesUpload ^ (interfaceDltestBytesUpload >>> 32));
		result = prime * result + (int) (interfaceTotalBytesDownload ^ (interfaceTotalBytesDownload >>> 32));
		result = prime * result + (int) (interfaceTotalBytesUpload ^ (interfaceTotalBytesUpload >>> 32));
		result = prime * result + (int) (interfaceUltestBytesDownload ^ (interfaceUltestBytesDownload >>> 32));
		result = prime * result + (int) (interfaceUltestBytesUpload ^ (interfaceUltestBytesUpload >>> 32));
		result = prime * result + (int) (numThreads ^ (numThreads >>> 32));
		result = prime * result + (int) (numThreadsRequested ^ (numThreadsRequested >>> 32));
		result = prime * result + ((numThreadsUl == null) ? 0 : numThreadsUl.hashCode());
		result = prime * result + (int) (pingMedian ^ (pingMedian >>> 32));
		long temp;
		temp = Double.doubleToLongBits(pingMedianLog);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((pingShortest == null) ? 0 : pingShortest.hashCode());
		temp = Double.doubleToLongBits(pingShortestLog);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((pingVariance == null) ? 0 : pingVariance.hashCode());
		result = prime * result + ((pings == null) ? 0 : pings.hashCode());
		result = prime * result + (int) (relativeTimeDlNs ^ (relativeTimeDlNs >>> 32));
		result = prime * result + (int) (relativeTimeUlNs ^ (relativeTimeUlNs >>> 32));
		result = prime * result + ((runNdt == null) ? 0 : runNdt.hashCode());
		result = prime * result + ((sourcePorts == null) ? 0 : sourcePorts.hashCode());
		result = prime * result + (int) (speedDownload ^ (speedDownload >>> 32));
		temp = Double.doubleToLongBits(speedDownloadLog);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((speedRaw == null) ? 0 : speedRaw.hashCode());
		result = prime * result + (int) (speedUpload ^ (speedUpload >>> 32));
		temp = Double.doubleToLongBits(speedUploadLog);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((targetMeasurementServer == null) ? 0 : targetMeasurementServer.hashCode());
		result = prime * result + (int) (testSlot ^ (testSlot >>> 32));
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + (int) (totalBytesDownload ^ (totalBytesDownload >>> 32));
		result = prime * result + (int) (totalBytesUpload ^ (totalBytesUpload >>> 32));
		result = prime * result + (int) (totalDurationNs ^ (totalDurationNs >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeasurementSpeedtest other = (MeasurementSpeedtest) obj;
		if (bytesDownload != other.bytesDownload)
			return false;
		if (bytesUpload != other.bytesUpload)
			return false;
		if (duration != other.duration)
			return false;
		if (durationDownloadNs != other.durationDownloadNs)
			return false;
		if (durationUploadNs != other.durationUploadNs)
			return false;
		if (encryption == null) {
			if (other.encryption != null)
				return false;
		} else if (!encryption.equals(other.encryption))
			return false;
		if (interfaceDltestBytesDownload != other.interfaceDltestBytesDownload)
			return false;
		if (interfaceDltestBytesUpload != other.interfaceDltestBytesUpload)
			return false;
		if (interfaceTotalBytesDownload != other.interfaceTotalBytesDownload)
			return false;
		if (interfaceTotalBytesUpload != other.interfaceTotalBytesUpload)
			return false;
		if (interfaceUltestBytesDownload != other.interfaceUltestBytesDownload)
			return false;
		if (interfaceUltestBytesUpload != other.interfaceUltestBytesUpload)
			return false;
		if (numThreads != other.numThreads)
			return false;
		if (numThreadsRequested != other.numThreadsRequested)
			return false;
		if (numThreadsUl == null) {
			if (other.numThreadsUl != null)
				return false;
		} else if (!numThreadsUl.equals(other.numThreadsUl))
			return false;
		if (pingMedian != other.pingMedian)
			return false;
		if (Double.doubleToLongBits(pingMedianLog) != Double.doubleToLongBits(other.pingMedianLog))
			return false;
		if (pingShortest == null) {
			if (other.pingShortest != null)
				return false;
		} else if (!pingShortest.equals(other.pingShortest))
			return false;
		if (Double.doubleToLongBits(pingShortestLog) != Double.doubleToLongBits(other.pingShortestLog))
			return false;
		if (pingVariance == null) {
			if (other.pingVariance != null)
				return false;
		} else if (!pingVariance.equals(other.pingVariance))
			return false;
		if (pings == null) {
			if (other.pings != null)
				return false;
		} else if (!pings.equals(other.pings))
			return false;
		if (relativeTimeDlNs != other.relativeTimeDlNs)
			return false;
		if (relativeTimeUlNs != other.relativeTimeUlNs)
			return false;
		if (runNdt == null) {
			if (other.runNdt != null)
				return false;
		} else if (!runNdt.equals(other.runNdt))
			return false;
		if (sourcePorts == null) {
			if (other.sourcePorts != null)
				return false;
		} else if (!sourcePorts.equals(other.sourcePorts))
			return false;
		if (speedDownload != other.speedDownload)
			return false;
		if (Double.doubleToLongBits(speedDownloadLog) != Double.doubleToLongBits(other.speedDownloadLog))
			return false;
		if (speedRaw == null) {
			if (other.speedRaw != null)
				return false;
		} else if (!speedRaw.equals(other.speedRaw))
			return false;
		if (speedUpload != other.speedUpload)
			return false;
		if (Double.doubleToLongBits(speedUploadLog) != Double.doubleToLongBits(other.speedUploadLog))
			return false;
		if (status != other.status)
			return false;
		if (targetMeasurementServer == null) {
			if (other.targetMeasurementServer != null)
				return false;
		} else if (!targetMeasurementServer.equals(other.targetMeasurementServer))
			return false;
		if (testSlot != other.testSlot)
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (totalBytesDownload != other.totalBytesDownload)
			return false;
		if (totalBytesUpload != other.totalBytesUpload)
			return false;
		if (totalDurationNs != other.totalDurationNs)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MeasurementSpeedtest [time=" + time + ", speedUpload=" + speedUpload + ", speedDownload="
				+ speedDownload + ", pingShortest=" + pingShortest + ", pingVariance=" + pingVariance + ", encryption="
				+ encryption + ", duration=" + duration + ", status=" + status + ", bytesDownload=" + bytesDownload
				+ ", bytesUpload=" + bytesUpload + ", durationDownloadNs=" + durationDownloadNs + ", durationUploadNs="
				+ durationUploadNs + ", speedUploadLog=" + speedUploadLog + ", speedDownloadLog=" + speedDownloadLog
				+ ", totalBytesDownload=" + totalBytesDownload + ", totalBytesUpload=" + totalBytesUpload
				+ ", interfaceTotalBytesDownload=" + interfaceTotalBytesDownload + ", interfaceTotalBytesUpload="
				+ interfaceTotalBytesUpload + ", interfaceDltestBytesDownload=" + interfaceDltestBytesDownload
				+ ", interfaceDltestBytesUpload=" + interfaceDltestBytesUpload + ", interfaceUltestBytesDownload="
				+ interfaceUltestBytesDownload + ", interfaceUltestBytesUpload=" + interfaceUltestBytesUpload
				+ ", testSlot=" + testSlot + ", pingShortestLog=" + pingShortestLog + ", runNdt=" + runNdt
				+ ", pingMedian=" + pingMedian + ", pingMedianLog=" + pingMedianLog + ", relativeTimeDlNs="
				+ relativeTimeDlNs + ", relativeTimeUlNs=" + relativeTimeUlNs + ", numThreadsRequested="
				+ numThreadsRequested + ", numThreads=" + numThreads + ", numThreadsUl=" + numThreadsUl
				+ ", targetMeasurementServer=" + targetMeasurementServer + ", pings=" + pings + ", speedRaw=" + speedRaw
				+ "]";
	}

	@Generated("org.jsonschema2pojo")
    public static enum Status {

        @SerializedName("FINISHED")
        FINISHED("FINISHED"),
        @SerializedName("STARTED")
        STARTED("STARTED"),
        @SerializedName("UPDATE ERROR")
        UPDATE_ERROR("UPDATE ERROR"),
        @SerializedName("ERROR")
        ERROR("ERROR");
        private final String value;
        private final static Map<String, MeasurementSpeedtest.Status> CONSTANTS = new HashMap<String, MeasurementSpeedtest.Status>();

        static {
            for (MeasurementSpeedtest.Status c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static MeasurementSpeedtest.Status fromValue(String value) {
            MeasurementSpeedtest.Status constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}