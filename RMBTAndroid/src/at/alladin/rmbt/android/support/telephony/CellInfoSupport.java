/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

package at.alladin.rmbt.android.support.telephony;

/**
 * 
 * @author lb
 *
 */
public interface CellInfoSupport {

	public enum CellInfoType {
		CDMA,
		GSM,
		LTE,
		WCDMA,
		UNKNOWN
	}
	
	/**
	 * 
	 * @return
	 */
	CellInfoType getCellInfoType();
	
	/**
	 * 
	 * @return
	 */
	int getCellId();

	/**
	 * 
	 * @return
	 */
	int getAreaCode();

	/**
	 * UMTS only
	 * @return
	 */
	int getPrimaryScramblingCode();
	
	/**
	 * 4G only
	 * @return
	 */
	int getPhysicalCellId();
	
	/**
	 * 
	 * @return
	 */
	boolean isRegistered();
}
