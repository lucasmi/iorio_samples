package br.com.bradesco.cardtech.http.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;

import br.com.bradesco.cardtech.AppParametersBean;

public class CustomSslInitializer {
	public static SSLContext createSslCustomContext(AppParametersBean appParametersBean)
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
			KeyManagementException, UnrecoverableKeyException {

		// Servidor CA keystore
		KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());
		tks.load(new FileInputStream(new File(appParametersBean.getServerStore())),
				appParametersBean.getServerStorePassword().toCharArray());

		// Cliente keystore
		KeyStore cks = KeyStore.getInstance("PKCS12");
		cks.load(new FileInputStream(new File(appParametersBean.getClientStore())),
				appParametersBean.getClientStorePassword().toCharArray());

		// Cria contexto
		SSLContext sslcontext = SSLContexts.custom()
				.loadKeyMaterial(cks, appParametersBean.getClientStorePassword().toCharArray())
				.loadTrustMaterial(tks, new TrustSelfSignedStrategy()).build();
		

		return sslcontext;
	}
}
