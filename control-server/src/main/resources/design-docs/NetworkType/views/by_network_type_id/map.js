function(doc) { 
    if (doc.doctype == 'NetworkType') {
    	emit([doc.network_type_id, doc.technology_order], null);
    }
}
