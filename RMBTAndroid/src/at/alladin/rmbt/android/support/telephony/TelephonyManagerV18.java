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

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

/**
 * 
 * @author lb
 *
 */
@TargetApi(18)
public class TelephonyManagerV18 extends TelephonyManagerSupport {

	public TelephonyManagerV18(TelephonyManager telephonyManager) {
		super(telephonyManager);
	}

	@Override
	public List<CellInfoSupport> getAllCellInfo() {
		final List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
		final List<CellInfoSupport> wrappedList = new ArrayList<CellInfoSupport>();
		
		if (cellInfoList != null) {			
			for (CellInfo c : cellInfoList) {
				wrappedList.add(new CellInfoV18(c));
			}
		}
		else {
			//if getAllCellInfo is not supported (see api doc), fall back to cell location
			final CellLocation cellLocation = telephonyManager.getCellLocation();
			if (cellLocation != null && cellLocation instanceof GsmCellLocation) {
				wrappedList.add(new CellInfoPreV18((GsmCellLocation) cellLocation));
			}
		}
		
		return wrappedList;
	}
}
