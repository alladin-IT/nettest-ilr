package at.alladin.nettest.shared.model;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class MeasurementAnonymization {
	
	private Boolean done = false;

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	@Override
	public String toString() {
		return "Anonymization [done=" + done + "]";
	}
}
