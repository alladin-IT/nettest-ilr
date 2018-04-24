function(doc) { 
    if (doc.doctype == 'QosMeasurementObjective') {
    	emit(doc.measurementClass, null);
    }
}
