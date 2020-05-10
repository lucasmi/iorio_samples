package br.com.bradesco.cardtech.jwt;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.RSADecrypter;

import br.com.bradesco.cardtech.rsa.RSA;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTUtils {

	public static String generate(String privateKeyFileName, JwtCreationBean jwtBean) throws Exception {
		PrivateKey privateKey = RSA.getPrivateKey(privateKeyFileName);

		return Jwts.builder().setIssuer(jwtBean.getIss()).setAudience(jwtBean.getAud()).setSubject(jwtBean.getSub())
				.setIssuedAt(jwtBean.getIat()).setExpiration(jwtBean.getExp()).setId(jwtBean.getJti())
				.claim("ver", jwtBean.getVer()).signWith(SignatureAlgorithm.RS512, privateKey).compact();
	}

	public static String decrypt(String privateKeyFileName, String publicKey, String jwtString) throws Exception {
		PrivateKey privateKey = RSA.getPrivateKey(privateKeyFileName);
		RSADecrypter rsaDecrypter = new RSADecrypter((RSAPrivateKey) privateKey);
		JWEObject jweObject = JWEObject.parse(jwtString);
		jweObject.decrypt(rsaDecrypter);
		return Jwts.parser().setSigningKey(RSA.getKeyFromBase64(publicKey))
				.parseClaimsJws(jweObject.getPayload().toString()).getBody().toString();
	}

}
