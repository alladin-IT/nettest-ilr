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

package at.alladin.rmbt.shared.qos.testscript;

import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import at.alladin.rmbt.shared.qos.TracerouteResult;
import at.alladin.rmbt.util.net.rtp.RealtimeTransportProtocol;

/**
 * 
 * @author alladin-IT GmbH (?@alladin.at)
 *
 */
public class SystemApi {

	/**
	 * 
	 */
	private static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("##0.00");
	
	/**
	 * 
	 */
	static {
		DEFAULT_DECIMAL_FORMAT.setMaximumFractionDigits(2);
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	public int getCount(Object array) {
		if (array != null && array.getClass().isArray()) {
			return Array.getLength(array);
		}

		return 0;
	}

	/**
	 * 
	 * @param array
	 * @return
	 */
	public boolean isEmpty(Object array) {
		return getCount(array) == 0;
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 */
	public boolean isNull(Object o) {
		return o == null;
	}

	/**
	 * 
	 * @param o
	 * @param alternative
	 * @return
	 */
	public Object coalesce(Object o, Object alternative) {
		return o == null ? alternative : o;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public String getPayloadType(int value) {
		return RealtimeTransportProtocol.PayloadType.getByCodecValue(value).name();
	}

	/**
	 * 
	 * @param path
	 * @return
	 * @throws JSONException
	 */
	public String parseTraceroute(String path) throws JSONException { // TODO: is this one used?
		final JSONArray traceRoute = new JSONArray(path);
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < traceRoute.length(); i++) {
			final JSONObject e = traceRoute.getJSONObject(i);
			sb.append(anonymizeIp(e.getString("host")));
			sb.append("  time=");
			try {
				sb.append(DEFAULT_DECIMAL_FORMAT.format((float)e.getLong("time") / 1000000f));
				sb.append("ms\n");
			}
			catch (Exception ex) {
				sb.append(e.getLong("time"));
				sb.append("ns\n");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public String parseTraceroute(ArrayList<TracerouteResult.PathElement> path) { // TODO: is this one used?
		StringBuilder sb = new StringBuilder();
		for (TracerouteResult.PathElement e : path) {
			sb.append(anonymizeIp(e.getHost()));
			sb.append("  time=");
			try {
				sb.append(DEFAULT_DECIMAL_FORMAT.format((float)e.getTime() / 1000000f));
				sb.append("ms\n");
			}
			catch (Exception ex) {
				sb.append(e.getTime());
				sb.append("ns\n");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param ip
	 * @return
	 */
	public String anonymizeIp(String ip) { // TODO: can this also be a rdns name?
		if ("*".equals(ip)) {
			return ip;
		}
		
		try {
			final InetAddress addr = InetAddress.getByName(ip);
			
			if (addr instanceof Inet4Address) {
				return ip.substring(0, ip.lastIndexOf('.')) + ".x";
			} else if (addr instanceof Inet6Address) {
				if (ip.endsWith("::")) {
					return ip + "x";
				} else {
					final String[] splitv6 = ip.split(":");
					return splitv6[0] + ":" + splitv6[1] + ":" + splitv6[2] + ":" + splitv6[3] + "::x";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "*";
	}
	
	/**
	 * 
	 * @param prefix
	 * @param suffix
	 * @param length
	 * @return
	 */
	public static String getRandomUrl(String prefix, String suffix, int length) {
		final Random rnd = new Random();
		char[] digits = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        
        final StringBuilder randomUrl = new StringBuilder();
        randomUrl.append(prefix);
        
        for (int i = 0; i < length; i++) {
        	randomUrl.append(digits[rnd.nextInt(16)]);
        }
        
        randomUrl.append(suffix);

        return randomUrl.toString();
	}
	
	/*public static void main(String[] args) {
		System.out.println(anonymizeIp("10.10.10.10"));
		System.out.println(anonymizeIp("10.10.10.0"));
		System.out.println(anonymizeIp("192.168.5.1"));
		
		System.out.println(anonymizeIp("2a01:4f8:10a:2315:88:99:181:238"));
		System.out.println(anonymizeIp("2a01:4f8:10a:2315::"));
		System.out.println(anonymizeIp("2a01:4f8:10a:2315:1111:2222:3333:4444"));
		
		System.out.println(anonymizeIp("qwdqdqw"));
		System.out.println(anonymizeIp("10.10.10.10.10"));
	}*/
}
