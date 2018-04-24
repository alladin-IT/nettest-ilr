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

package at.alladin.rmbt.android.main;

/**
 * 
 * @author lb
 *
 */
public class FeatureConfig {

	/**
	 * if set to true the flag "publish_public_data" can be set by a preference and will be sent to the control server
	 */
	@Deprecated
	public static boolean TEST_USE_PERSONAL_DATA_FUZZING = false;
	
	/**
	 * show ndt info popup on start
	 */
	@Deprecated
	public static boolean SHOW_NDT_INFO = false;
	
	/**
	 * enable (true) or disable (false) opendata (=statistics, map dots, ...)
	 */
	@Deprecated
	public static boolean USE_OPENDATA = true;
}
