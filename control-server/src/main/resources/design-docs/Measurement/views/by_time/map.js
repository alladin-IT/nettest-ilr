function(doc) {
	if (doc.doctype == "Measurement") {
		emit([doc.timestamp, doc.uuid], doc);
	}
}
