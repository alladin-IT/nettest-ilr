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

import java.util.List;

import android.telephony.TelephonyManager;

/**
 * 
 * @author lb
 *
 */
public abstract class TelephonyManagerSupport {
	
	protected final TelephonyManager telephonyManager;
	
	public TelephonyManagerSupport(final TelephonyManager telephonyManager) {
		this.telephonyManager = telephonyManager;
	}
	
	public abstract List<CellInfoSupport> getAllCellInfo();
}
