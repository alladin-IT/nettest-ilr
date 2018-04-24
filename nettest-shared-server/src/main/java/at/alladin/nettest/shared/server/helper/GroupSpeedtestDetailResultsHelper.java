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

package at.alladin.nettest.shared.server.helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import at.alladin.nettest.shared.helper.GsonBasicHelper;
import at.alladin.nettest.shared.model.GeoLocation;
import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.shared.model.Settings.SpeedtestDetailGroup;
import at.alladin.nettest.shared.model.Settings.SpeedtestDetailGroup.SpeedtestDetailGroupEntry;
import at.alladin.nettest.shared.model.Settings.SpeedtestDetailGroup.SpeedtestDetailGroupEntry.FormatEnum;
import at.alladin.nettest.shared.model.response.SpeedtestDetailGroupResultResponse;
import at.alladin.nettest.shared.model.response.SpeedtestDetailGroupResultResponse.SpeedtestDetailResponseGroup;
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse.SpeedtestDetailItem;
import at.alladin.nettest.shared.model.response.SpeedtestDetailResultResponse.SpeedtestTimeDetailItem;

import at.alladin.rmbt.shared.Helperfunctions;




/**
 * Helper to group requested values of a given JSON
 * @author fk
 *
 */
public class GroupSpeedtestDetailResultsHelper {
	
	private static final Locale STRING_FORMAT_LOCALE = Locale.US;
	
	private static final String RMBT_PLATFORM_STRING = "RMBTws";
	
	/**
	 * Groups the results according to the groupStructure param
	 * Will move all values that are defined in the groupStructure from their current location into the location defined by the groupStructure
	 * If none of the defined values of a group within the groupStructure are valid, the groupStructure will NOT be added at all
	 * If an exception is thrown during this process, the original json String is returned
	 * @param json, the JSON result to be grouped
	 * @param groupStructure, the structure to be applied to the json String as obtained w/ServerResource.getSetting(speedtestDetailGroups) TODO: make this more specific
	 * @return a JSONObject that has all key/value pairs that are defined in the groupStructure moved to the correct place, all other values remain unchanged
	 */
	public static JsonObject groupJsonResult(String json, JsonArray groupStructure){
		final Gson gson = GsonBasicHelper.getDateTimeGsonBuilder().create();
		final JsonObject jsonObj = gson.fromJson(json, JsonObject.class);

		jsonObj.add("groups", new JsonArray());
		for (int i = 0; i < groupStructure.size(); i++) {
			extractGroupValues(jsonObj, groupStructure.get(i).getAsJsonObject());
		}
		return jsonObj;
	}
	
	public static SpeedtestDetailGroupResultResponse groupResult(final Measurement measurement, final JsonArray groupStructure, final Locale locale, 
			final int geoAccuracyDetailLimit, final Gson gson) {
		return groupResult(measurement, groupStructure, locale, geoAccuracyDetailLimit, gson, false);
	}
	
	/**
	 * Gets the grouped results according to the groupStructure param
	 * Items still need to be translated afterwards (the titles of the entries will be the translation keys to look for)
	 * If a unit is present, it still needs to be translated and appended to the value
	 * If the value starts with "key_" it needs be translated
	 * The key_location element needs custom handling! (used for the geo location)
	 * @param measurement the object whose results need be grouped
	 * @param groupStructure the structure of the groups as in couchdb speedtestDetailGroups
	 * @param locale needed for number formatting
	 * @param geoAccuracyDetailLimit the corresponding setting (in rmbt.geoAccuracyDetailLimit) needed for the geolocation entries
	 * @param includeKeys will include the internal keys in the result (e.g. speed_parameter_group)
	 * @return
	 */
	public static SpeedtestDetailGroupResultResponse groupResult(final Measurement measurement, final JsonArray groupStructure, final Locale locale, 
			final int geoAccuracyDetailLimit, final Gson gson, final boolean includeKeys) {
		final Format format = new DecimalFormat("#.00");
		final JsonArray groupedResultsJson = GroupSpeedtestDetailResultsHelper.groupJsonResult(gson.toJson(measurement, Measurement.class), groupStructure).getAsJsonArray("groups");
    	
    	//Fill in the corresponding Response with translated and formatted values
		final SpeedtestDetailGroupResultResponse groupResponse = new SpeedtestDetailGroupResultResponse();
		groupResponse.setSpeedtestDetailGroups(new ArrayList<>());
		
		//get a list of all SpeedtestDetailGroups to be put into the response
		final List<SpeedtestDetailGroup> groups = new ArrayList<>();
		
		final Format tzFormat = new DecimalFormat("+0.##;-0.##", new DecimalFormatSymbols(locale));
		
		for(int i = 0; i < groupedResultsJson.size(); i++){
			groups.add(gson.fromJson(groupedResultsJson.get(i), SpeedtestDetailGroup.class));
		}
		
		for (final SpeedtestDetailGroup sdg : groups) {
			//create a corresponding responseGroup w/formatted and i18ed values
			final SpeedtestDetailResponseGroup responseGroup = new SpeedtestDetailResponseGroup();

			responseGroup.setTitle("key_" + sdg.getKey());
			responseGroup.setIcon(sdg.getIcon());
			
			responseGroup.setEntries(new ArrayList<>());
			
			for (SpeedtestDetailGroupEntry entry : sdg.getValues()) {
				final String key = entry.getKey();
				final FormatEnum formatEnum = entry.getFormat();
				final String unit = entry.getUnit();
				String val = entry.getValue();
				
				//fill the item accordingly
				final SpeedtestDetailItem item = new SpeedtestDetailItem();
				
				item.setTitle(entry.getTranslationKey());//getLocalizedMessage(entry.getTranslationKey(), locale));
				
				if (includeKeys) {
					item.setKey(key);
				}
				
				//do formatting
				//special cases get their own formatting (not ideal...)
				if(key.endsWith("network_type")){
					item.setValue(Helperfunctions.getNetworkTypeName(Integer.parseInt(val)));
				} else if(key.endsWith("time")){	//The time key will additionally set the timezone for formatting reasons
					final SpeedtestTimeDetailItem timeItem = new SpeedtestTimeDetailItem();
					
					timeItem.setKey(item.getKey());
					timeItem.setTitle(item.getTitle());
					timeItem.setValue(entry.getValue());

					final DateTime mst = measurement.getSpeedtest().getTime();
					
					timeItem.setTime(mst.getMillis());
					timeItem.setTimezone(mst.getZone().getID());
					
					responseGroup.getEntries().add(timeItem);
					
					//and write the timezone into the item
					final TimeZone tz = TimeZone.getTimeZone(timeItem.getTimezone());
					final float offset = tz.getOffset(timeItem.getTime()) / 1000f / 60f / 60f;
					
					item.setTitle("key_timezone");//getLocalizedMessage("key_timezone", locale));
					item.setValue(String.format("UTC%sh", tzFormat.format(offset)));
					
				} else if(key.endsWith("interface_total_bytes_download")) {
					if (measurement.getSpeedtest().getInterfaceTotalBytesDownload() <= 0 && measurement.getSpeedtest().getInterfaceTotalBytesUpload() <= 0) {
						continue;
					}
					//if this is the first of the two total if bytes entries
					val = formatResultValueString(Long.toString(measurement.getSpeedtest().getInterfaceTotalBytesDownload() + measurement.getSpeedtest().getInterfaceTotalBytesUpload())
							, formatEnum, format);
					item.setUnit(unit);
				} else if(key.endsWith("total_bytes_download")){
					if (measurement.getSpeedtest().getTotalBytesDownload() <= 0 && measurement.getSpeedtest().getTotalBytesUpload() <= 0) {
						continue;
					}
					//if this is the first of the two total if bytes entries
					val = formatResultValueString(Long.toString(measurement.getSpeedtest().getTotalBytesDownload() + measurement.getSpeedtest().getTotalBytesUpload()),
							formatEnum, format);
					item.setUnit(unit);
					
				} else if(key.endsWith("interface_total_bytes_upload") || key.endsWith("total_bytes_upload")) {
					//we only want one entry for both dl and ul of interface total bytes and total bytes
					continue;
				} else if(key.endsWith("encryption")){
					if(val == null || val.isEmpty()){
						continue;
					}
					val = "NONE".equals(entry.getValue()) ? "key_encryption_false" : "key_encryption_true";
				} else if(key.endsWith("open_test_uuid")){
					item.setValue("O" + val);
				} else if (key.endsWith("platform")) {
					if (RMBT_PLATFORM_STRING.equals(val)) {
						item.setValue("key_platform_rmbtws");
					}
				} else if (key.endsWith("bytes_download") || key.endsWith("bytes_upload")) {
					double dVal = -1;
					try {
						dVal = Double.parseDouble(val);
					} catch (NumberFormatException ex) {
						continue;
					}
					if (dVal <= 0) {
						continue;
					}
					if(formatEnum != null){
						val = formatResultValueString(val, formatEnum, format);
					}
					if(unit != null){
						item.setUnit(unit);
					}
				} else {
					//default formatting
					if(formatEnum != null){
						val = formatResultValueString(val, formatEnum, format);
					}
					if(unit != null){
						item.setUnit(unit);
					}
				}
				
				if(item.getValue() == null){
					item.setValue(val);
				}
				//and add that item to the responseGroup
				responseGroup.getEntries().add(item);
			}
			//group specific exceptions land here
			if (sdg.getKey().equals("device_information_group")) {
				final List<GeoLocation> locations = measurement.getLocations();
				
				if (locations != null && locations.size() > 0) {
					final SpeedtestDetailItem item = new SpeedtestDetailItem();
					
					item.setTitle("key_location");//getLocalizedMessage("key_location", locale));
					
					try{
						item.setValue(getGeoLocation(geoAccuracyDetailLimit, locale, locations.get(0)));
					} catch(JSONException ex) {
						ex.printStackTrace();
					}
					
					responseGroup.getEntries().add(item);
				}
			}
			groupResponse.getSpeedtestDetailGroups().add(responseGroup);
		} 
		return groupResponse;
	}
	
	/**
	 * Helper method to extract all values specified in the groupDefinition from the json JSONObject
	 * places them into the defined groups instead
	 * @param json the JSONObject containing all the information to be grouped
	 * @param groupDefinition the definition of a single group to be extracted by this method
	 * @return the same json with the specified values moved into the specified groups
	 */
	private static JsonObject extractGroupValues(JsonObject json, JsonObject groupDefinition){
		boolean firstValue = true;
		JsonObject currentGroup = new JsonObject();
		final JsonArray valuesArray = groupDefinition.getAsJsonArray("values");
		
		for(int i = 0; i < valuesArray.size(); i++){
			final String[] keyPath = valuesArray.get(i).getAsJsonObject().get("key").getAsString().split("\\.");
			if(keyPath.length == 0){
				continue;
			}
			final String lastKey = keyPath[keyPath.length - 1];
			//the object used to navigate to the specified key
			JsonObject navigationObj = json;
			for(String s : keyPath){
				if(navigationObj.has(s) && navigationObj.get(s).isJsonObject()){
					navigationObj = navigationObj.getAsJsonObject(s);
				}
			}
			if(!navigationObj.has(lastKey)){
				continue;
			}else{
				if(firstValue){
					appendGroupStartDefinition(currentGroup, groupDefinition);
					firstValue = false;
				}
				final JsonObject valueEntry = new JsonObject();
				valueEntry.add("key", new JsonPrimitive(lastKey));
				valueEntry.add("value", navigationObj.get(lastKey));
				//aditionally put everything else we find in the structure into the json obj (meant for things like format strings or units
				for(Map.Entry<String, JsonElement> elem : valuesArray.get(i).getAsJsonObject().entrySet()){
					if(elem.getKey().equals("key")){
						continue;
					}
					valueEntry.add(elem.getKey(), elem.getValue());
				}
				//the default translation key is "key_" + the lastKey
				if(!valueEntry.has("translation_key")){
//					JsonPrimitive trans = new JsonPrimitive("key_" + lastKey);
					valueEntry.add("translation_key", new JsonPrimitive("key_" + lastKey));
				}
				cleanKeyRemove(json, navigationObj, keyPath);
				currentGroup.getAsJsonArray("values").add(valueEntry);
			}
		}
		if(!firstValue){
			json.getAsJsonArray("groups").add(currentGroup);
		}
		return json;
	}
	
	/**
	 * Helper method to remove not only the specified entry but the complete JSONObject if that was the last entry of that JSONObject
	 * @param fullJsonObj the complete json object
	 * @param navigationObj the json object where the key to-be-deleted is located
	 * @param keyPath full path of the to-be-deleted key
	 * @return the fullJsonObj w/out the specified key and w/out empty JSONObjs
	 */
	private static JsonObject cleanKeyRemove(JsonObject fullJsonObj, JsonObject navigationObj, String[] keyPath){
		navigationObj.remove(keyPath[keyPath.length -1]);
		if(!navigationObj.entrySet().isEmpty()){
			return fullJsonObj;
		}
		navigationObj = fullJsonObj;
		for(int i = 0; i < keyPath.length - 2; i++){
			navigationObj = navigationObj.has(keyPath[i]) ? navigationObj.getAsJsonObject(keyPath[i]) : navigationObj;
		}
		cleanKeyRemove(fullJsonObj, navigationObj, Arrays.copyOf(keyPath, keyPath.length - 1));
		return fullJsonObj;
	}
	
	/**
	 * Helper method to append the group definition from the groupStructure (e.g. to append the key and icon of the group)
	 * Simply appends everything it finds apart from the values themselves
	 * @param json the json to append to
	 * @param groupToAppend the group data to be appended
	 * @return the original json with the starting group info appended
	 */
	private static JsonObject appendGroupStartDefinition(JsonObject jsonObj, JsonObject groupToAppend){
		for(Map.Entry<String, JsonElement> elem : groupToAppend.entrySet()){
			if(elem.getKey().equals("values")){
				continue;
			}
			jsonObj.add(elem.getKey(), elem.getValue());
		}
		jsonObj.add("values", new JsonArray());
		return jsonObj;
	}
	
	private static String formatResultValueString(final String value, final FormatEnum formatEnum, final Format format) {
		try {
			return format.format(Double.parseDouble(value) / formatEnum.getDivider());
		} catch (NumberFormatException | NullPointerException ex) {
			ex.printStackTrace();
			// do nothing with it, just return original value
		}
		
		return value;
	}
	
	private static String getGeoLocation(final int geoAccuracyDetailLimit, Locale locale, GeoLocation location) throws JSONException {
        // geo-location
    	
    	final Double latitude = location.getLatitude();
    	final Double longitude = location.getLongitude();
    	final Double accuracy = location.getAccuracy();
    	final Double altitude = location.getAltitude();
    	
        if (location != null && latitude != null && longitude != null && accuracy != null) {
            if (accuracy < geoAccuracyDetailLimit) {//	settings.getRmbt().getGeoAccuracyDetailLimit()) {
                
            	final StringBuilder geoString = new StringBuilder(Helperfunctions.geoToString(latitude, longitude));
                geoString.append(" (");
                
                String provider = location.getProvider();
                
                if (provider != null) {
                	
                	provider = provider.toUpperCase();
                	String key = null;
                	
                	switch(provider) {
	                	case "GPS":
	            			key  = "key_geo_source_gps";
	            			break;
	                	case "BROWSER":
	            			key  = "key_geo_source_browser";
	            			break;
	            		case "NETWORK":
                		default:
                			key = "key_geo_source_network";
                	}
                	
                    geoString.append(key);
                    geoString.append(", ");
                }
                
                geoString.append(String.format(STRING_FORMAT_LOCALE, "+/- %.0f m", accuracy));

                if (altitude != null) {
                	geoString.append(String.format(STRING_FORMAT_LOCALE, ", alt: %.0f m", altitude));
                }

                geoString.append(")");
                
                return geoString.toString();                
            }
            
            // country derived from location
//            final Field countryLocationField = test.getField("country_location"); 
//            if (!countryLocationField.isNull()) {
//                json.put("country_location", countryLocationField.toString());
//            }
        }
        
        return null;
    }

}
