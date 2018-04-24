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

package at.alladin.rmbt.shared;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author
 *
 */
public class SettingsHelper {
	
	/**
	 * 
	 */
	public final static String DEFAULT_SETTINGS_ID = "settings__nettest.alladin.at";
	
	/**
	 * 
	 * @param conn db connection
	 * @param key settings key (supports nested keys like: 'parent_key.child_key') 
	 * @return
	 */
    public static String getSetting(final Connection conn, final String key) {
        return getSetting(conn, key, null);
    }

	/**
	 * 
	 * @param conn db connection
	 * @param key settings key (supports nested keys like: 'parent_key.child_key')
	 * @param settingsId the settings id; if NULL the first entry will be selected
	 * @return
	 */
    // TODO: add caching!    
    public static String getSetting(Connection conn, String key, String settingsId) {
        if (conn == null || key == null) {
            return null;
        }

        final String sql = 
        	"SELECT json->'" + key.replace(".", "'->'") + "' as value"
            + " FROM ha_settings"
            + (settingsId != null ? " WHERE id = ?" : "");
        
        try (final PreparedStatement st = conn.prepareStatement(sql)) {
        	if (settingsId != null) {
        		st.setString(1, settingsId);
        	}
            
            try (final ResultSet rs = st.executeQuery()) {
                if (rs != null && rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
