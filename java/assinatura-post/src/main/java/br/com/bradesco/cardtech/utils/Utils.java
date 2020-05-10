package br.com.bradesco.cardtech.utils;

import java.security.SecureRandom;

public class Utils {

	public static String nonceGenerator() {
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < 15; i++) {
			stringBuilder.append(secureRandom.nextInt(10));
		}
		String randomNumber = stringBuilder.toString();
		return randomNumber;
	}

}
