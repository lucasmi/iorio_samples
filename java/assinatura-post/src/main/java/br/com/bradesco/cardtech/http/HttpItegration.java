package br.com.bradesco.cardtech.http;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import br.com.bradesco.cardtech.AppParametersBean;
import br.com.bradesco.cardtech.http.bean.EncriptyDataBean;
import br.com.bradesco.cardtech.http.bean.OAuthTokenRequestBean;
import br.com.bradesco.cardtech.http.bean.OAuthTokenResponseBean;
import br.com.bradesco.cardtech.http.utils.HttpManager;
import br.com.bradesco.cardtech.jwt.JWTUtils;
import br.com.bradesco.cardtech.jwt.JwtCreationBean;
import br.com.bradesco.cardtech.rsa.RSA;
import br.com.bradesco.cardtech.utils.Utils;

public class HttpItegration {
	AppParametersBean appParametersBean;

	public HttpItegration(AppParametersBean appParametersBean) {
		this.appParametersBean = appParametersBean;
	}

	public AppParametersBean getAppParametersBean() {
		return appParametersBean;
	}

	public String createJWT(JwtCreationBean jwtCreationBean) throws Exception {
		return JWTUtils.generate(getAppParametersBean().getCaminhoChavePrivada(), jwtCreationBean);
	}

	public OAuthTokenResponseBean doLogin(OAuthTokenRequestBean bean) throws Exception {
		OAuthTokenResponseBean oAuthTokenResponseBean = null;

		// Cria o cliente para usar a configuacao SSL
		CloseableHttpClient httpClient = HttpManager.getConnection(getAppParametersBean());

		HttpPost request = new HttpPost("https://" + getAppParametersBean().getHost() + ":"
				+ getAppParametersBean().getPort() + "/auth/server/v1/token");

		// add request parameter, form parameters
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
		params.add(new BasicNameValuePair("assertion", bean.getJwt()));
		request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		// add request headers
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

		try (CloseableHttpResponse response = httpClient.execute(request)) {

			// Get HttpResponse Status
			System.out.println(response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				if (entity != null) {
					String result = EntityUtils.toString(entity);
					Gson gson = new Gson();
					oAuthTokenResponseBean = gson.fromJson(result, OAuthTokenResponseBean.class);
				}
			} else {
				System.out.println(EntityUtils.toString(entity));
			}
		}

		HttpManager.closeConnection();

		return oAuthTokenResponseBean;
	}

	public String doSinedPost(String endPoint, String jsonString, String access_token) throws Exception {
		String retorno = "";

		Gson gson = new Gson();

		// Cria o cliente para usar a configuacao SSL
		CloseableHttpClient httpClient = HttpManager.getConnection(getAppParametersBean());

		HttpPost request = new HttpPost(
				"https://" + getAppParametersBean().getHost() + ":" + getAppParametersBean().getPort() + endPoint);

		// Assina conteudo
		String date = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss").format(new Date());

		// Nonce
		String nonce = Utils.nonceGenerator();

		// Conteudo assinado
		String sinedContent = getSinedContent(endPoint, jsonString, access_token, date, nonce);
		// add request headers
		request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + access_token);
		request.addHeader("X-Brad-Nonce", nonce);
		request.addHeader("X-Brad-Signature", sinedContent);
		request.addHeader("X-Brad-Timestamp", date);
		request.addHeader("X-Brad-Algorithm", "SHA256");

		// monta o body JSON
		StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
		request.setEntity(requestEntity);

		try (CloseableHttpResponse response = httpClient.execute(request)) {

			// Get HttpResponse Status
			System.out.println(response.getStatusLine().toString());

			Header[] contentcert = response.getHeaders("x-signer-cert");
			String signerCert = null;
			for (Header header : contentcert) {
				if ("x-signer-cert".equals(header.getName())) {
					signerCert = header.getValue();
				}
			}

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String result = EntityUtils.toString(entity);
				if (response.getStatusLine().getStatusCode() == 200) {
					EncriptyDataBean encriptyDataBean = gson.fromJson(result, EncriptyDataBean.class);
					String decriptedJwt = JWTUtils.decrypt(getAppParametersBean().getCaminhoChavePrivada(), signerCert,
							encriptyDataBean.getEncryptData());
					retorno = decriptedJwt;
				} else {
					retorno = result;
				}
			}
		}

		HttpManager.closeConnection();

		return retorno;
	}

	/*
	 * Gera conteudo no formato solicitado para assinatura
	 */
	private String getSinedContent(String endPoint, String jsonString, String access_token, String requestDate,
			String nonce) throws Exception {

		String terminator = "\n";
		String requesMethod = "POST";
		String requestURL = endPoint;
		String requestParam = "";
		String requestBody = jsonString;
		String chaveAcesso = access_token;
		String requestNonce = nonce;
		String requestTime = requestDate;
		String requestAlg = "SHA256";

		String requestString = requesMethod.concat(terminator);
		requestString = requestString.concat(requestURL).concat(terminator);
		requestString = requestString.concat(requestParam).concat(terminator);
		requestString = requestString.concat(requestBody).concat(terminator);
		requestString = requestString.concat(chaveAcesso).concat(terminator);
		requestString = requestString.concat(requestNonce).concat(terminator);
		requestString = requestString.concat(requestTime).concat(terminator);
		requestString = requestString.concat(requestAlg);

		PrivateKey privateKey = RSA.getPrivateKey(getAppParametersBean().getCaminhoChavePrivada());
		return RSA.sign(privateKey, requestString);
	}

}
