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

package at.alladin.rmbt.android.test;

import android.view.View;

public interface ChangeableSpeedTestStatus {
	
    void setResultDownString(final String s, final Object flag);
    
	void setResultUpString(final String s, final Object flag);
	
    void setResultInitString(final String s, final Object flag);
    
	void setResultPingString(final String s, final Object flag);

	String getResultInitString();
	
	String getResultPingString();
	
	String getResultDownString();
	
	String getResultUpString();

	void setView(final View container);
	
	void setForceHideProgressBar(boolean isVisible);
	
	boolean isForceHideProgressBar();
}
