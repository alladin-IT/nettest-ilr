/*******************************************************************************
 * Copyright 2016-2017 alladin-IT GmbH
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

package at.alladin.rmbt.android.views.network;

/**
 * Created by lb on 28.02.17.
 */

public interface NetworkInfoView {
    void setNetworkName(final String networkName);
    void setSignalStrength(final String signalStrength);
    void setNetworkType(final String networkType);

    String getNetworkName();
    String getSignalStrength();
    String getNetworkType();
}
