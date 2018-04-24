function(doc) { 
    if (doc.doctype == 'QosMeasurementObjective') {
    	emit(doc.qos_test_uid, null);
    }
}
