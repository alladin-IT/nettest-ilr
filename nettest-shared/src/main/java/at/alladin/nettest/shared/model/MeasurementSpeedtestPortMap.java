package at.alladin.nettest.shared.model;

import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeasurementSpeedtestPortMap {

	/**
	 * Source port (Client) map (mapped by thread)
	 */
	@SerializedName("source_port_map_dl")
	@Expose
	private Map<Integer, Integer> sourcePortDownloadMap;

	
	/**
	 * Source port (Client) map (mapped by thread)
	 */
	@SerializedName("source_port_map_ul")
	@Expose
	private Map<Integer, Integer> sourcePortUploadMap;

	public Map<Integer, Integer> getSourcePortDownloadMap() {
		return sourcePortDownloadMap;
	}

	public void setSourcePortDownloadMap(Map<Integer, Integer> sourcePortDownloadMap) {
		this.sourcePortDownloadMap = sourcePortDownloadMap;
	}

	public Map<Integer, Integer> getSourcePortUploadMap() {
		return sourcePortUploadMap;
	}

	public void setSourcePortUploadMap(Map<Integer, Integer> sourcePortUploadMap) {
		this.sourcePortUploadMap = sourcePortUploadMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourcePortDownloadMap == null) ? 0 : sourcePortDownloadMap.hashCode());
		result = prime * result + ((sourcePortUploadMap == null) ? 0 : sourcePortUploadMap.hashCode());
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
		MeasurementSpeedtestPortMap other = (MeasurementSpeedtestPortMap) obj;
		if (sourcePortDownloadMap == null) {
			if (other.sourcePortDownloadMap != null)
				return false;
		} else if (!sourcePortDownloadMap.equals(other.sourcePortDownloadMap))
			return false;
		if (sourcePortUploadMap == null) {
			if (other.sourcePortUploadMap != null)
				return false;
		} else if (!sourcePortUploadMap.equals(other.sourcePortUploadMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MeasurementSpeedtestPortMap [sourcePortDownloadMap=" + sourcePortDownloadMap + ", sourcePortUploadMap="
				+ sourcePortUploadMap + "]";
	}
}
