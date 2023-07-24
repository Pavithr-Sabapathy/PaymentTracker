package com.mashreq.paymentTracker.utility;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import com.mashreq.paymentTracker.exception.CryptographyException;
import com.mashreq.paymentTracker.type.EncryptionAlgorithm;

public class AesUtil {

	private static final String ENCRYPTION_DECRYPTION_KEY = "execueDatasourceConnection";


	public static String encryptDecrypt(String password, String flag) {

		String stringToBeEncryptDecrypt = password;
		String flagToSpecifyEncryptDecrypt = flag;

		String encryptDecryptedString = null;
		try {
			if (flagToSpecifyEncryptDecrypt.equalsIgnoreCase("e")) {
				encryptDecryptedString = encryptBase64(stringToBeEncryptDecrypt, ENCRYPTION_DECRYPTION_KEY,
						EncryptionAlgorithm.TRIPLE_DES);
			} else if (flagToSpecifyEncryptDecrypt.equalsIgnoreCase("d")) {
				encryptDecryptedString = decryptBase64(stringToBeEncryptDecrypt, ENCRYPTION_DECRYPTION_KEY,
						EncryptionAlgorithm.TRIPLE_DES);
			}
		} catch (CryptographyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return encryptDecryptedString;
	}

	public static String decryptBase64(String encryptedString, String encryptionKey,
			EncryptionAlgorithm encryptionAlgorithm) throws CryptographyException {
		try {
			if (StringUtils.isEmpty(encryptedString)) {
				return encryptedString;
			}
			Cipher decryptCipher = Cipher.getInstance(encryptionAlgorithm.getValue());

			decryptCipher.init(Cipher.DECRYPT_MODE, TripleDESKeyGenerationService.generateSecretKey(encryptionKey));
			// Encode bytes to base64 to get a string
			byte[] decodedBytes = Base64.decodeBase64(encryptedString.getBytes());
			// Decrypt
			byte[] unencryptedByteArray = decryptCipher.doFinal(decodedBytes);
			// Decode using utf-8
			return new String(unencryptedByteArray, "UTF8");
		} catch (NoSuchAlgorithmException e) {
			throw new CryptographyException(10903, e);
		} catch (NoSuchPaddingException e) {
			throw new CryptographyException(10903, e);
		} catch (InvalidKeyException e) {
			throw new CryptographyException(10903, e);
		} catch (UnsupportedEncodingException e) {
			throw new CryptographyException(10903, e);
		} catch (IllegalBlockSizeException e) {
			throw new CryptographyException(10903, e);
		} catch (BadPaddingException e) {
			throw new CryptographyException(10903, e);
		}
	}

	public static String encryptBase64(String unencryptedString, String decryptionKey,
			EncryptionAlgorithm encryptionAlgorithm) throws CryptographyException {
		try {
			if (StringUtils.isEmpty(unencryptedString)) {
				return unencryptedString;
			}
		//	Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			Cipher encryptCipher = Cipher.getInstance(encryptionAlgorithm.getValue());
			encryptCipher.init(Cipher.ENCRYPT_MODE, TripleDESKeyGenerationService.generateSecretKey(decryptionKey));
			// Encode the string into bytes using utf-8
			byte[] unencryptedByteArray = unencryptedString.getBytes("UTF8");
			// Encrypt
			byte[] encryptedBytes = encryptCipher.doFinal(unencryptedByteArray);
			// Encode bytes to base64 to get a string
			return new String(Base64.encodeBase64(encryptedBytes));
		} catch (NoSuchAlgorithmException e) {
			throw new CryptographyException(10902, e);
		} catch (NoSuchPaddingException e) {
			throw new CryptographyException(10902, e);
		} catch (InvalidKeyException e) {
			throw new CryptographyException(10902, e);
		} catch (UnsupportedEncodingException e) {
			throw new CryptographyException(10902, e);
		} catch (IllegalBlockSizeException e) {
			throw new CryptographyException(10902, e);
		} catch (BadPaddingException e) {
			throw new CryptographyException(10902, e);
		}
	}

}
