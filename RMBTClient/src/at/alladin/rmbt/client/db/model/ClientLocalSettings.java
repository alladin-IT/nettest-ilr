package at.alladin.rmbt.client.db.model;

public class ClientLocalSettings {

	private String clientUuid;
	
	private boolean isTermsAndConditionsAccepted = false;
	
	private int termsAndConditionsAcceptedVersion = 1;

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

	public boolean isTermsAndConditionsAccepted() {
		return isTermsAndConditionsAccepted;
	}

	public void setTermsAndConditionsAccepted(boolean isTermsAndConditionsAccepted) {
		this.isTermsAndConditionsAccepted = isTermsAndConditionsAccepted;
	}

	public int getTermsAndConditionsAcceptedVersion() {
		return termsAndConditionsAcceptedVersion;
	}

	public void setTermsAndConditionsAcceptedVersion(int termsAndConditionsAcceptedVersion) {
		this.termsAndConditionsAcceptedVersion = termsAndConditionsAcceptedVersion;
	}

	@Override
	public String toString() {
		return "ClientLocalSettings [clientUuid=" + clientUuid + ", isTermsAndConditionsAccepted="
				+ isTermsAndConditionsAccepted + ", termsAndConditionsAcceptedVersion="
				+ termsAndConditionsAcceptedVersion + "]";
	}
}
