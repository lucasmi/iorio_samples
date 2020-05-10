package br.com.bradesco.cardtech.jwt;

import java.util.Date;

public class JwtCreationBean {
	String iss;
	String aud;
	String sub;
	String jti;
	String ver;
	Date iat;
	Date exp;

	public Date getIat() {
		return iat;
	}

	public void setIat(Date iat) {
		this.iat = iat;
	}

	public Date getExp() {
		return exp;
	}

	public void setExpt(Date exp) {
		this.exp = exp;
	}

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

}
