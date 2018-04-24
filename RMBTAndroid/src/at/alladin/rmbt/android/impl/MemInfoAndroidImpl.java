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

package at.alladin.rmbt.android.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.alladin.rmbt.util.tools.MemInfo;
import at.alladin.rmbt.util.tools.ToolUtils;

public class MemInfoAndroidImpl implements MemInfo {
	
	public final static int UNKNOWN = -1;
	
	private final static Pattern MEMINFO_PATTERN = Pattern.compile("([a-zA-Z]*)\\:\\s*([0-9]*)\\s*([a-zA-Z]*)");

	private Map<String, Long> memoryMap;
	
	public Map<String, Long> getMemoryMap() {
		if (memoryMap == null) {
			update();
		}
		
		return memoryMap;
	}

	public long getTotalMem() {
		if (memoryMap == null) {
			update();
			return UNKNOWN;
		}
		
		return memoryMap.get("MemTotal");
	}

	public long getFreeMem() {
		if (memoryMap == null) {
			update();
			return UNKNOWN;
		}
		else {
			return memoryMap.get("MemFree");			
		}
	}

	public synchronized void update() {
		memoryMap = new HashMap<String, Long>();
		String memInfo = ToolUtils.readFromProc("/proc/meminfo");
		Matcher m = MEMINFO_PATTERN.matcher(memInfo);
		while (m.find()) {
			String type = m.group(1);
			long size;
			try {
				size = Long.parseLong(m.group(2));
			}
			catch (Exception e) {
				size = UNKNOWN;
			}
			
			memoryMap.put(type, size);
		}
	}

}
