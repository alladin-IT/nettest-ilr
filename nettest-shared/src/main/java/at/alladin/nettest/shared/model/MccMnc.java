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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.alladin.nettest.shared.model.exception.IllegalMccMncException;

/**
 * @author leo
 *
 */
public class MccMnc {
	
	private final static Pattern MCC_MNC_PATTERN = Pattern.compile("(\\d{3})-?(\\d{2,3})");
	
	private int mcc;
	private int mnc;
	
	public MccMnc() {
	}

	public MccMnc(int mcc, int mnc) {
		this.mcc = mcc;
		this.mnc = mnc;
	}
	
	public static MccMnc fromString(CharSequence mccMnc) throws IllegalMccMncException {
		return MccMnc.fromString(mccMnc, null);
	}
	
	public static MccMnc fromString(CharSequence mccMnc, CharSequence fallback) throws IllegalMccMncException {
		if (mccMnc == null && fallback == null) {
			throw new IllegalMccMncException("mccMnc cannot be null");
		}
		
		final Matcher matcher = MCC_MNC_PATTERN.matcher(mccMnc == null ? fallback : mccMnc);
		if (! matcher.matches())
			throw new IllegalMccMncException("format invalid");
		try {
			return new MccMnc(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
		} catch (NumberFormatException e) {
			throw new IllegalMccMncException(e);
		}
	}

	public int getMcc() {
		return mcc;
	}

	public void setMcc(int mcc) {
		this.mcc = mcc;
	}

	public int getMnc() {
		return mnc;
	}

	public void setMnc(int mnc) {
		this.mnc = mnc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mcc;
		result = prime * result + mnc;
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
		MccMnc other = (MccMnc) obj;
		if (mcc != other.mcc)
			return false;
		if (mnc != other.mnc)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "%03d-%02d", mcc, mnc);
	}
	
	
}
