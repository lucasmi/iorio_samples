package br.com.bradesco.cardtech.http.utils;

import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import br.com.bradesco.cardtech.AppParametersBean;

public class HttpManager {
	private static CloseableHttpClient closeableHttpClient = null;

	private static CloseableHttpClient createConnection(AppParametersBean appParametersBean) throws Exception {
		// Codigo temporario pelo fato do HOST estar errado no certificado
		// Cria configuracao SSL
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(
				CustomSslInitializer.createSslCustomContext(appParametersBean),
				new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		// SSLConnectionSocketFactory.getDefaultHostnameVerifier()

		// Registra a comunicao no protocolo https
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create().register("https", csf)
				.build();

		// Cria o connection manager
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg);
		cm.setMaxTotal(5);
		cm.setDefaultMaxPerRoute(4);
		HttpHost host = new HttpHost(appParametersBean.getHost(), Integer.valueOf(appParametersBean.getPort()));
		cm.setMaxPerRoute(new HttpRoute(host), 5);

		closeableHttpClient = HttpClients.custom().setConnectionManager(cm).build();

		return closeableHttpClient;
	}

	/*
	 * Metodo criado para gerar somnete uma conexao e usar em todas as chamadas
	 * Avaliando se será necessário
	 */
	public static CloseableHttpClient getConnection(AppParametersBean appParametersBean) throws Exception {
		if (closeableHttpClient == null) {
			closeableHttpClient = createConnection(appParametersBean);
		}

		return closeableHttpClient;
	}

	public static void closeConnection() throws Exception {
		if (closeableHttpClient != null) {
			closeableHttpClient.close();
			closeableHttpClient = null;
		}
	}

}
