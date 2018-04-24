function(doc) { 
    if (doc.doctype == 'Provider') {
    	if (doc.mccMncMappings.length > 0) {
    		for (var i in doc.mccMncMappings) {
    			var mapping = doc.mccMncMappings[i];
    			
    			var sim = mapping.mccMncSim;
    			var net = mapping.mccMncNetwork;
    			
    			if (net)
    				emit([sim.mcc, sim.mnc, net.mcc, net.mnc], null);
    			else
    				emit([sim.mcc, sim.mnc], null);
    		}
    	}
    }
}
