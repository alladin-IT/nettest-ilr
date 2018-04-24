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

import android.annotation.TargetApi;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

@TargetApi(18)
public class CellInfoV18 implements CellInfoSupport {

	final CellInfo cellInfo;
	final CellInfoType cellInfoType;
	
	public CellInfoV18(CellInfo cellInfo) {
		this.cellInfo = cellInfo;
		if (cellInfo instanceof CellInfoGsm) {
			cellInfoType = CellInfoType.GSM;
		}
		else if (cellInfo instanceof CellInfoLte) {
			cellInfoType = CellInfoType.LTE;
		}
		else if (cellInfo instanceof CellInfoWcdma) {
			cellInfoType = CellInfoType.WCDMA;
		}
		else if (cellInfo instanceof CellInfoCdma) {
			cellInfoType = CellInfoType.CDMA;
		}
		else {
			cellInfoType = CellInfoType.UNKNOWN;
		}
	}

	@Override
	public int getCellId() {
		switch (cellInfoType) {
		case GSM:
			final CellIdentityGsm cellId = ((CellInfoGsm) cellInfo).getCellIdentity();
			return cellId != null ? cellId.getCid() : Integer.MAX_VALUE;
			
		case LTE:
			final CellIdentityLte cellIdLte = ((CellInfoLte) cellInfo).getCellIdentity();
			return cellIdLte != null ? cellIdLte.getCi() : Integer.MAX_VALUE;
			
		case WCDMA:
			final CellIdentityWcdma cellIdWcdma = ((CellInfoWcdma) cellInfo).getCellIdentity();
			return cellIdWcdma != null ? cellIdWcdma.getCid() : Integer.MAX_VALUE;
			
		default:
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public int getAreaCode() {
		switch (cellInfoType) {
		case GSM:
			final CellIdentityGsm cellId = ((CellInfoGsm) cellInfo).getCellIdentity();
			return cellId != null ? cellId.getLac() : Integer.MAX_VALUE;
			
		case LTE:
			final CellIdentityLte cellIdLte = ((CellInfoLte) cellInfo).getCellIdentity();
			return cellIdLte != null ? cellIdLte.getTac() : Integer.MAX_VALUE;

		case WCDMA:
			final CellIdentityWcdma cellIdWcdma = ((CellInfoWcdma) cellInfo).getCellIdentity();
			return cellIdWcdma != null ? cellIdWcdma.getLac() : Integer.MAX_VALUE;

		default:
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public int getPrimaryScramblingCode() {
		switch (cellInfoType) {
		case WCDMA:
			final CellIdentityWcdma cellIdWcdma = ((CellInfoWcdma) cellInfo).getCellIdentity();
			return cellIdWcdma != null ? cellIdWcdma.getPsc() : Integer.MAX_VALUE;

		default:
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public int getPhysicalCellId() {
		switch (cellInfoType) {
		case LTE:
			final CellIdentityLte cellIdLte = ((CellInfoLte) cellInfo).getCellIdentity();
			return cellIdLte != null ? cellIdLte.getPci() : Integer.MAX_VALUE;

		default:
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public boolean isRegistered() {
		return cellInfo.isRegistered();
	}

	@Override
	public CellInfoType getCellInfoType() {
		return cellInfoType;
	}

	@Override
	public String toString() {
		return "CellInfoV18 [getCellId()=" + getCellId() + ", getAreaCode()="
				+ getAreaCode() + ", getPrimaryScramblingCode()="
				+ getPrimaryScramblingCode() + ", getPhysicalCellId()="
				+ getPhysicalCellId() + ", isRegistered()=" + isRegistered()
				+ ", getCellInfoType()=" + getCellInfoType() + "]";
	}
}
