package com.mashreq.paymentTracker.utility;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.mashreq.paymentTracker.exception.CryptographyException;
import com.mashreq.paymentTracker.type.EncryptionAlgorithm;

public class TripleDESKeyGenerationService {

	public static SecretKey generateSecretKey(String key) throws CryptographyException {
		try {
			if (key.getBytes().length < 24) {
				throw new CryptographyException(10910, "Inappropriate key length");
			}
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(EncryptionAlgorithm.TRIPLE_DES.getValue());
			DESedeKeySpec keySpec = new DESedeKeySpec(key.getBytes());
			return keyFactory.generateSecret(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptographyException(10904, e);
		} catch (InvalidKeyException e) {
			throw new CryptographyException(10904, e);
		} catch (InvalidKeySpecException e) {
			throw new CryptographyException(10904, e);
		}

	}

}
