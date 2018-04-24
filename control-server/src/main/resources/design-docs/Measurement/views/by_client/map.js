function(doc) {
	if (doc.client_info.uuid != null && 
		doc.client_info.uuid !== "" &&
		doc.doctype == 'Measurement' && 
		doc.speedtest.status == 'FINISHED' && 
		doc.deleted != true) {
		
		emit([doc.client_info.uuid, doc.speedtest.time], {
			_id: doc._id,
			"uuid": doc.uuid,
			
			"qos": (doc.qos != null && doc.qos.status != null && doc.qos.status == "FINISHED" ? {"status": doc.qos.status} : null),
			
			"client_info": {
				"time": doc.client_info.time,				// time
				"timezone": doc.client_info.timezone,		// timezone
				"type": doc.client_info.type,				// client type
			},
			
			"speedtest": {
				"ping_median": doc.speedtest.ping_median,
				"ping_shortest": doc.speedtest.ping_shortest,
				"speed_download": doc.speedtest.speed_download,
				"speed_upload": doc.speedtest.speed_upload	
			},
			
			"network_info": {
				"network_group_name": doc.network_info.network_group_name,
				"network_type": ((doc.signals != null && doc.signals.length >= 1) ? doc.signals[0].network_type_id : doc.network_info.network_type)
			},
			
			"device_info": {
				"model": doc.device_info.model
			}
		})
	}
}
