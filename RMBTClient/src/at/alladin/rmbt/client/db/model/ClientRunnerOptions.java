package at.alladin.rmbt.client.db.model;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class ClientRunnerOptions {

	private Boolean useSsl = false;
	
	private Integer threads = 3;
	
	private Integer duration = 5;
	
	private Integer pings = 10;
	
	private Boolean hasQos = true;
	
	private Boolean sslNoVerify = true;
	
	private String clientUuid;

	private Server server;
	
	/**
	 * path to local settings file containing (client uuid, t&c accepted value, etc.) 
	 */
	private String settingsPath;
	
	public Boolean getSslNoVerify() {
		return sslNoVerify;
	}

	public void setSslNoVerify(Boolean sslNoVerify) {
		this.sslNoVerify = sslNoVerify;
	}

	public Boolean getUseSsl() {
		return useSsl;
	}

	public void setUseSsl(Boolean useSsl) {
		this.useSsl = useSsl;
	}

	public Integer getPings() {
		return pings;
	}

	public void setPings(Integer pings) {
		this.pings = pings;
	}

	public Integer getThreads() {
		return threads;
	}

	public void setThreads(Integer threads) {
		this.threads = threads;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Boolean getHasQos() {
		return hasQos;
	}

	public void setHasQos(Boolean hasQos) {
		this.hasQos = hasQos;
	}

	public String getClientUuid() {
		return clientUuid;
	}

	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String getSettingsPath() {
		return settingsPath;
	}

	public void setSettingsPath(String settingsPath) {
		this.settingsPath = settingsPath;
	}

	@Override
	public String toString() {
		return "ClientRunnerOptions [useSsl=" + useSsl + ", threads=" + threads + ", duration=" + duration + ", pings="
				+ pings + ", hasQos=" + hasQos + ", sslNoVerify=" + sslNoVerify + ", clientUuid=" + clientUuid
				+ ", server=" + server + "]";
	}

	/**
	 * 
	 * @author lb@alladin.at
	 *
	 */
	public static class Server {
		private ServerOptions control;
		
		private ServerOptions speedtest;

		public ServerOptions getControl() {
			return control;
		}

		public void setControl(ServerOptions control) {
			this.control = control;
		}

		public ServerOptions getSpeedtest() {
			return speedtest;
		}

		public void setSpeedtest(ServerOptions speedtest) {
			this.speedtest = speedtest;
		}

		@Override
		public String toString() {
			return "Server [control=" + control + ", speedtest=" + speedtest + "]";
		}
	}
	
	
	/**
	 * 
	 * @author lb@alladin.at
	 *
	 */
	public static class ServerOptions {
		
		private String host;
		
		private Integer port;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		@Override
		public String toString() {
			return "ServerOptions [host=" + host + ", port=" + port + "]";
		}
	}
}
