function(doc) {
	if (doc.doctype == "Measurement") {
		emit(doc.open_test_uuid, null);
	}
}
