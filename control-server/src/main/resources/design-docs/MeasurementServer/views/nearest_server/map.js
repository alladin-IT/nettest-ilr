function(doc) {
	if (doc.active && doc.doctype == "MeasurementServer") {
        emit([doc.server_type, doc._id], doc);
	}
}
