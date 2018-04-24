function(doc) {
	if (doc.doctype == "Measurement" && (doc.anonymization == null 
				|| doc.anonymization.done == null
				|| doc.anonymization.done !== true)) {
		emit([doc.timestamp, (doc.anonymization != null 
				&& doc.anonymization.done != null ? doc.anonymization.done : false)], doc);
	}
}
