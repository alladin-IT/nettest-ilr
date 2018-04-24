function(doc) {
  if (doc.doctype == "MeasurementServer" || doc.doctype == "QosMeasurementObjective" || doc.doctype == "Settings") {
    var keys = [];
    if (doc.doctype == "QosMeasurementObjective") {
      keys.push(doc.measurementClass);
    }
    else if (doc.doctype == "MeasurementServer") {
      keys.push(doc.active);
      keys.push(doc.server_type);
    }
	  
	  if (doc.profiles != null) {
	    for (var i = 0; i < doc.profiles.length; i++) {
	      emit([doc.doctype, doc.profiles[i]].concat(keys), null);
	    }
	  }
	  else {
	    emit([doc.doctype, null].concat(keys), null);
	  }
  }
}
