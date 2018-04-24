package at.alladin.nettest.shared.server.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import at.alladin.nettest.shared.model.Measurement;
import at.alladin.nettest.shared.model.Settings;
import at.alladin.nettest.shared.model.Settings.ColorThresholds;
import at.alladin.nettest.shared.model.Settings.ThresholdsPerTechnology;


public class ClassificationHelper {

	private static ClassificationHelper instance;
	
    public static ClassificationHelper getInstance() {
        return instance;
    }
    
    public static ClassificationHelper initInstance(final Settings settings) {
        instance = new ClassificationHelper();
        instance.init(settings);
        return instance;
    }
	
	private ThresholdsPerTechnology thresholdsPerTechnology;
	
	private void init(final Settings settings) {
        int[] downloadValues = null;
        int[] uploadValues = null;
        int[] pingValues = null;
        int[] signalMobileValues = null;
        int[] signalRsrpValues = null;
        int[] signalWifiValues = null;
        thresholdsPerTechnology = null;
                
        try { uploadValues = getIntValues(settings.getThresholds().getUpload(), 2); } catch (Exception e) { e.printStackTrace(); }
        try { downloadValues = getIntValues(settings.getThresholds().getDownload(), 2); } catch (Exception e) { e.printStackTrace(); }
        try { pingValues = getIntValues(settings.getThresholds().getPing(), 2); } catch (Exception e) { e.printStackTrace(); }
        try { signalMobileValues = getIntValues(settings.getThresholds().getSignalMobile(), 2); } catch (Exception e) { e.printStackTrace(); }
        try { signalRsrpValues = getIntValues(settings.getThresholds().getSignalRsrp(), 2); } catch (Exception e) { e.printStackTrace(); }
        try { signalWifiValues = getIntValues(settings.getThresholds().getSignalWifi(), 2); } catch (Exception e) { e.printStackTrace(); }
         
        thresholdsPerTechnology = settings.getThresholdsPerTechnology();
        
        if(thresholdsPerTechnology == null) {
        	//TODO: provide def vals
        }
        
        if (uploadValues == null) {
            uploadValues = new int[] { 1000, 500 }; // default
        }
        THRESHOLD_UPLOAD = uploadValues;
//        THRESHOLD_UPLOAD_CAPTIONS = getCaptions(uploadValues);
        
        if (downloadValues == null) {
            downloadValues = new int[] { 2000, 1000 }; // default
        }
        THRESHOLD_DOWNLOAD = downloadValues;
//        THRESHOLD_DOWNLOAD_CAPTIONS = getCaptions(downloadValues);
        
        if (pingValues == null) {
        	pingValues = new int[] { 25, 75 }; // default
        }
        THRESHOLD_PING = pingValues;
//        THRESHOLD_PING_CAPTIONS = getCaptions(pingValues);
        
        if (pingValues.length == 2) {
        	THRESHOLD_PING[0] *= 1000000;
        	THRESHOLD_PING[1] *= 1000000;
        }
        
        if (signalMobileValues == null) {
        	signalMobileValues = new int[] { -85, -101 }; // default
        }
        THRESHOLD_SIGNAL_MOBILE = signalMobileValues;
//        THRESHOLD_SIGNAL_MOBILE_CAPTIONS = getCaptions(signalMobileValues);
        
        if (signalRsrpValues == null) {
        	signalRsrpValues = new int[] { -95, -111 }; // default
        }
        THRESHOLD_SIGNAL_RSRP = signalRsrpValues;
//        THRESHOLD_SIGNAL_RSRP_CAPTIONS = getCaptions(signalRsrpValues);
        
        if (signalWifiValues == null) {
        	signalWifiValues = new int[] { -61, -76 }; // default
        }
        THRESHOLD_SIGNAL_WIFI = signalWifiValues;
//        THRESHOLD_SIGNAL_WIFI_CAPTIONS = getCaptions(signalWifiValues);
    }

//    private static String[] getCaptions(int[] values)
//    {
//        final String[] result = new String[values.length];
//        for (int i = 0; i < values.length; i++)
//            result[i] = String.format(Locale.US, "%.1f", ((double)values[i]) / 1000);
//        return result;
//    }

    private static int[] getIntValues(String value, int expectCount) {
    	if (value == null) {
    		return null;
    	}
    	
    	final String parts[] = value.split(";");
        if (parts.length != expectCount) {
            throw new IllegalArgumentException(String.format(Locale.US, "unexpected number of parameters (expected %d): \"%s\"", expectCount, value));
        }
        
        final int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        
        return result;
    }
    
    private int[] THRESHOLD_UPLOAD = null;
//    private String[] THRESHOLD_UPLOAD_CAPTIONS;
    
    private int[] THRESHOLD_DOWNLOAD = null;
//    private String[] THRESHOLD_DOWNLOAD_CAPTIONS;
    
    private int[] THRESHOLD_PING;
//    private String[] THRESHOLD_PING_CAPTIONS;
    
    // RSSI limits used for 2G,3G (and 4G when RSSI is used)
    // only odd values are reported by 2G/3G 
    private int[] THRESHOLD_SIGNAL_MOBILE; // -85 is still green, -101 is still yellow
//    private String[] THRESHOLD_SIGNAL_MOBILE_CAPTIONS;
    
    // RSRP limit used for 4G
    private int[] THRESHOLD_SIGNAL_RSRP;
//    private String[] THRESHOLD_SIGNAL_RSRP_CAPTIONS;

    // RSSI limits used for Wifi
    private int[] THRESHOLD_SIGNAL_WIFI;
//    private String[] THRESHOLD_SIGNAL_WIFI_CAPTIONS;
    
    private final Set<Integer> NETWORK_IDS_WIFI = new HashSet<>(Arrays.asList(99));
    private final Set<Integer> NETWORK_IDS_RSRP = new HashSet<>(Arrays.asList(13));
    private final Set<Integer> NETWORK_IDS_MOBILE = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 105));
    
    public int classify(final int[] threshold, final long value)
    {
        final boolean inverse = threshold[0] < threshold[1];
        
        if (!inverse)
        {
            if (value >= threshold[0])
                return 3; // GREEN
            else if (value >= threshold[1])
                return 2; // YELLOW
            else
                return 1; // RED
        }
        else if (value <= threshold[0])
            return 3;
        else if (value <= threshold[1])
            return 2;
        else
            return 1;
    }

    public int classify(final ClassificationType type, final long value) {
    	switch (type) {
    	case DOWNLOAD:
    		return classify(THRESHOLD_DOWNLOAD, value);
    	case UPLOAD:
    		return classify(THRESHOLD_UPLOAD, value);
    	case PING:
    		return classify(THRESHOLD_PING, value);
    	case SIGNAL:
    		return classify(THRESHOLD_SIGNAL_MOBILE, value);
    	}
    	
    	return 1;
    }
    
    private String classifyColor(final ColorThresholds colorThresholds, final long value) {
    	final Long ceilingKey = colorThresholds.getColorMap().ceilingKey(value);
    	return ceilingKey == null ? colorThresholds.getDefaultColor() : colorThresholds.getColorMap().get(ceilingKey);
    }
    
//    public ClassificationItem classifyColor(final ClassificationType type, final long value) {
//    	return classifyColor(type, value, null);
//    }
    
    public ClassificationItem classifyColor(final ClassificationType type, final long value, final Integer networkType) {
    	final ClassificationItem ret = new ClassificationItem();
    	switch (type) {
    	case DOWNLOAD:
    		ret.setClassificationNumber(classify(THRESHOLD_DOWNLOAD, value));
    		if(thresholdsPerTechnology != null && networkType != null && thresholdsPerTechnology.getDownload() != null 
    				&& thresholdsPerTechnology.getDownload().containsKey(networkType)) {
    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getDownload().get(networkType), value));
    		}
    		break;
    	case UPLOAD:
    		ret.setClassificationNumber(classify(THRESHOLD_UPLOAD, value));
    		if(thresholdsPerTechnology != null && networkType != null && thresholdsPerTechnology.getUpload() != null
    				&& thresholdsPerTechnology.getUpload().containsKey(networkType)) {
    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getUpload().get(networkType), value));
    		}
    		break;
    	case PING:
    		ret.setClassificationNumber(classify(THRESHOLD_PING, value));
    		if(thresholdsPerTechnology != null && networkType != null && thresholdsPerTechnology.getPing() != null
    				&& thresholdsPerTechnology.getPing().containsKey(networkType)) {
    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getPing().get(networkType), value));
    		}
    		break;
    	case SIGNAL:
    		if(thresholdsPerTechnology != null && networkType != null && thresholdsPerTechnology.getSignal() != null
    			&& thresholdsPerTechnology.getSignal().containsKey(networkType)) {
    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getSignal().get(networkType), value));
    		}
    		//Legacy code below
    		if(NETWORK_IDS_WIFI.contains(networkType)) {
    			ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_WIFI, value));
    		} else if (NETWORK_IDS_RSRP.contains(networkType)) {
    			ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_RSRP, value));
    		} else if (NETWORK_IDS_MOBILE.contains(networkType)) {
    			ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_MOBILE, value));
    		} else {
    			ret.setClassificationNumber(1);
    		}
    		break;
//    	case SIGNAL_MOBILE:
//    		ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_MOBILE, value));
//    		if(thresholdsPerTechnology != null) {
//    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getSignal().get(networkType), value));
//    		}
//    		break;
//    	case SIGNAL_RSRP:
//    		ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_RSRP, value));
//    		if(thresholdsPerTechnology != null) {
//    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getSignal().get(networkType), value));
//    		}
//    		break;
//    	case SIGNAL_WIFI:
//    		ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_WIFI, value));
//    		if(thresholdsPerTechnology != null) {
//    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getSignal().get(networkType), value));
//    		}
//			break;
    	default:
        	//the old default return was 1
        	ret.setClassificationNumber(1);
        	//TODO: default color?
    	}
    	
    	return ret;
    }

    public static enum ClassificationType {
    	UPLOAD,
    	DOWNLOAD,
    	PING,
    	SIGNAL
//    	SIGNAL_MOBILE,
//    	SIGNAL_WIFI,
//    	SIGNAL_RSRP
    }
    
    public static class ClassificationItem {
    	
    	private String classificationColor;
    	
    	private int classificationNumber;

		public String getClassificationColor() {
			return classificationColor;
		}

		public void setClassificationColor(String classificationColor) {
			this.classificationColor = classificationColor;
		}

		public int getClassificationNumber() {
			return classificationNumber;
		}

		public void setClassificationNumber(int classificationNumber) {
			this.classificationNumber = classificationNumber;
		}

		@Override
		public String toString() {
			return "ClassificationItem [classificationColor=" + classificationColor + ", classificationNumber="
					+ classificationNumber + "]";
		}
		
    }
    
    public static Integer getNetworkTypeId(final Measurement measurement) {
    	return measurement.getSignals().isEmpty() ? measurement.getNetworkInfo().getNetworkType() : measurement.getSignals().get(0).getNetworkTypeId();
    }
	
}
