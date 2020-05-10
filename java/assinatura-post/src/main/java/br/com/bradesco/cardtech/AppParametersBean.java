package br.com.bradesco.cardtech;

public class AppParametersBean {
	private String caminhoChavePrivada;
	private String host;
	private String port;
	private String clientStore;
	private String clientStorePassword;
	private String serverStore;
	private String serverStorePassword;
	private String nomeCadastrado;
	private String secretKeyCadastrada;

	public String getNomeCadastrado() {
		return nomeCadastrado;
	}

	public void setNomeCadastrado(String nomeCadastrado) {
		this.nomeCadastrado = nomeCadastrado;
	}

	public String getSecretKeyCadastrada() {
		return secretKeyCadastrada;
	}

	public void setSecretKeyCadastrada(String secretKeyCadastrada) {
		this.secretKeyCadastrada = secretKeyCadastrada;
	}

	public String getClientStore() {
		return clientStore;
	}

	public void setClientStore(String clientStore) {
		this.clientStore = clientStore;
	}

	public String getClientStorePassword() {
		return clientStorePassword;
	}

	public void setClientStorePassword(String clientStorePassword) {
		this.clientStorePassword = clientStorePassword;
	}

	public String getServerStore() {
		return serverStore;
	}

	public void setServerStore(String serverStore) {
		this.serverStore = serverStore;
	}

	public String getServerStorePassword() {
		return serverStorePassword;
	}

	public void setServerStorePassword(String serverStorePassword) {
		this.serverStorePassword = serverStorePassword;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getCaminhoChavePrivada() {
		return caminhoChavePrivada;
	}

	public void setCaminhoChavePrivada(String caminhoChavePrivada) {
		this.caminhoChavePrivada = caminhoChavePrivada;
	}
}
