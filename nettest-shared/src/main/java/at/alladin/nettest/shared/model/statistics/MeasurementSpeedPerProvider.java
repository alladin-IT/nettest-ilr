/*******************************************************************************
 * Copyright 2016-2017 alladin-IT GmbH
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

package at.alladin.nettest.shared.model.statistics;

import com.google.gson.annotations.SerializedName;

public class MeasurementSpeedPerProvider {
	
	@SerializedName("measurements")
	private long measurementsAmount;
	
	@SerializedName("provider_name")
	private String providerName;
	
	@SerializedName("avarage_download_speed_kbits")
	private long avarageDownloadSpeed;
	
	@SerializedName("avarage_upload_speed_kbits")
	private long avarageUploadSpeed;

	public long getMeasurementsAmount() {
		return measurementsAmount;
	}

	public void setMeasurementsAmount(long amount) {
		this.measurementsAmount = amount;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public long getAvarageDownloadSpeed() {
		return avarageDownloadSpeed;
	}

	public void setAvarageDownloadSpeed(long avarageDownloadSpeed) {
		this.avarageDownloadSpeed = avarageDownloadSpeed;
	}

	public long getAvarageUploadSpeed() {
		return avarageUploadSpeed;
	}

	public void setAvarageUploadSpeed(long avarageUploadSpeed) {
		this.avarageUploadSpeed = avarageUploadSpeed;
	}
}
