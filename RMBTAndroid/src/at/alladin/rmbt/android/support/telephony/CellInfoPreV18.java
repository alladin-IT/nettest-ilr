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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

/**
 * 
 * @author lb
 *
 */
public class CellInfoPreV18 implements CellInfoSupport {
	private static final String DEBUG_TAG = CellInfoPreV18.class.getCanonicalName();

	final GsmCellLocation cellLocation;
	
	public CellInfoPreV18(GsmCellLocation cellLocation) {
		this.cellLocation = cellLocation;
	}

	@Override
	public int getCellId() {
		return cellLocation.getCid();
	}

	@Override
	public int getAreaCode() {
		return cellLocation.getLac();
	}

	@Override
	public int getPrimaryScramblingCode() {
        final Class<?> cellClass = cellLocation.getClass();
        int tmpCode = -1;        
        final Method pscMethod;
        try
        {
            pscMethod = cellClass.getMethod("getPsc");
            final Integer result = (Integer) pscMethod.invoke(cellLocation, (Object[]) null);
            tmpCode = result.intValue();
        }
        catch (final SecurityException e)
        {
            Log.i(DEBUG_TAG, "CellLocationItem scramblingCode failed Security");
        }
        catch (final NoSuchMethodException e)
        {
            Log.i(DEBUG_TAG, "CellLocationItem scramblingCode failed NoSuchMethod");
        }
        catch (final IllegalArgumentException e)
        {
            Log.i(DEBUG_TAG, "CellLocationItem scramblingCode failed IllegalArgument");
        }
        catch (final IllegalAccessException e)
        {
            Log.i(DEBUG_TAG, "CellLocationItem scramblingCode failed IllegalAccess");
        }
        catch (final InvocationTargetException e)
        {
            Log.i(DEBUG_TAG, "CellLocationItem scramblingCode failed InvocationTarget");
        }
        
		return tmpCode;
	}

	@Override
	public int getPhysicalCellId() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isRegistered() {
		return true;
	}

	@Override
	public CellInfoType getCellInfoType() {
		return CellInfoType.GSM;
	}

	@Override
	public String toString() {
		return "CellInfoPreV18 [getCellId()=" + getCellId()
				+ ", getAreaCode()=" + getAreaCode()
				+ ", getPrimaryScramblingCode()=" + getPrimaryScramblingCode()
				+ ", getPhysicalCellId()=" + getPhysicalCellId()
				+ ", isRegistered()=" + isRegistered() + ", getCellInfoType()="
				+ getCellInfoType() + "]";
	}
}
