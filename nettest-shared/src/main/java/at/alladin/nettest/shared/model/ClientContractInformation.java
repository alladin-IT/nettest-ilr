package at.alladin.nettest.shared.model;

import com.google.gson.annotations.Expose;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class ClientContractInformation {
	
	/**
	 * 
	 */
	@Expose
	private long downloadKbps;
	
	/**
	 * 
	 */
	@Expose
	private long uploadKbps;
	
	/**
	 * 
	 */
	@Expose
	private String contractName;

	/**
	 * 
	 * @return
	 */
	public long getDownloadKbps() {
		return downloadKbps;
	}
	
	/**
	 * 
	 * @param downloadKbps
	 */
	public void setDownloadKbps(long downloadKbps) {
		this.downloadKbps = downloadKbps;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getUploadKbps() {
		return uploadKbps;
	}
	
	/**
	 * 
	 * @param uploadKbps
	 */
	public void setUploadKbps(long uploadKbps) {
		this.uploadKbps = uploadKbps;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getContractName() {
		return contractName;
	}

	/**
	 * 
	 * @param contractName
	 */
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MeasurementContractInformation [downloadKbps=" + downloadKbps + ", uploadKbps=" + uploadKbps
				+ ", contractName=" + contractName + "]";
	}
}
