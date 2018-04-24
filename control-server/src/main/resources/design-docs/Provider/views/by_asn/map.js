function(doc) { 
    if (doc.doctype == 'Provider') {
    	if (doc.asnMappings.length > 0) {
    		for (var i in doc.asnMappings) {
    			var mapping = doc.asnMappings[i];
    			var len = ! mapping.reverseDnsSuffix ? 0 : mapping.reverseDnsSuffix.length;
    			emit([mapping.asn, len], null);
    		}
    	}
    }
}
