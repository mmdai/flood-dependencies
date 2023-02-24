package cn.flood.base.core.encrypt;

import cn.flood.base.core.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA公钥加密
 */
public class RSA {
	
	
	public static String ALGORITHM = "RSA";

	public static String SIGN_ALGORITHMS = "SHA1WithRSA";// 摘要加密算饭

	/**
	 * 数据签名
	 * 
	 * @param content
	 *            签名内容
	 * @param privateKey
	 *            私钥
	 * @return 返回签名数据
	 */
	public static String sign(String content, String privateKey) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(StandardCharsets.UTF_8));

			byte[] signed = signature.sign();

			return new String(Base64.encode(signed));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 签名验证
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @return
	 */
	public static boolean verify(String content, String sign,
			String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(publicKey);
			PublicKey pubKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(StandardCharsets.UTF_8));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 通过公钥解密
	 * 
	 * @param content 待解密数据
	 * @param pk 公钥
	 * @return 返回 解密后的数据
	 */
	protected static byte[] decryptByPublicKey(String content, PublicKey pk) {

		try {
			Cipher ch = Cipher.getInstance(ALGORITHM);
			ch.init(Cipher.DECRYPT_MODE, pk);
			InputStream ins = new ByteArrayInputStream(Base64.decode(content));
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			// rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
			byte[] buf = new byte[128];
			int bufl;
			while ((bufl = ins.read(buf)) != -1) {
				byte[] block = null;

				if (buf.length == bufl) {
					block = buf;
				} else {
					block = new byte[bufl];
					for (int i = 0; i < bufl; i++) {
						block[i] = buf[i];
					}
				}

				writer.write(ch.doFinal(block));
			}

			return writer.toByteArray();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 通过私钥加密
	 * 
	 * @param content
	 * @param pk
	 * @return,加密数据，未进行base64进行加密
	 */
	protected static byte[] encryptByPrivateKey(String content, PrivateKey pk) {

		try {
			Cipher ch = Cipher.getInstance(ALGORITHM);
			ch.init(Cipher.ENCRYPT_MODE, pk);
			return ch.doFinal(content.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("通过私钥加密出错");
		}
		return null;

	}

	/**
	 * 解密数据，接收端接收到数据直接解密
	 * 
	 * @param content
	 * @param key
	 * @return
	 */
	public static String decrypt(String content, String key) {
		if (null == key || "".equals(key)) {
			return null;
		}
		PublicKey pk = getPublicKey(key);
		byte[] data = decryptByPublicKey(content, pk);
		String res = new String(data, StandardCharsets.UTF_8);
		return res;
	}

	/**
	 * 对内容进行加密
	 * 
	 * @param content
	 * @param key 私钥
	 * @return
	 */
	public static String encrypt(String content, String key) {
		PrivateKey pk = getPrivateKey(key);
		byte[] data = encryptByPrivateKey(content, pk);
		String res = null;
		try {
			res = new String(Base64.encode(data));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;

	}
	
	/* 
	 * 得到私钥对象
	* @param key 密钥字符串（经过base64编码的秘钥字节）
	* @throws Exception
	*/
	public static PrivateKey getPrivateKey(String privateKey)  {
		try {
		byte[] keyBytes;
		
		keyBytes = Base64.decode(privateKey);
		
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		PrivateKey privatekey = keyFactory.generatePrivate(keySpec);
		
		return privatekey;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	 
    /**
	* 获取公钥对象
	* @param publicKey 密钥字符串（经过base64编码秘钥字节）
	* @throws Exception
	*/
	public static PublicKey getPublicKey(String publicKey) {

		try {
			
			byte[] keyBytes;
			
			keyBytes = Base64.decode(publicKey);
			
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			PublicKey publickey = keyFactory.generatePublic(keySpec);
			
			return publickey;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
		
	}

	public static void main(String[] args) {
		String str = new String(Base64.encode("18505933993".getBytes()));
		System.out.println("Base64加密-->" + str);
		String aaa = sign(
				str,
				"MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKWmOMF5zQBqc8xA41zq9BKHI8GUdZdJR8qP57mv8y2N7T/TD1YeNEx3UH7cSWpsZpdzV5BWKAhH7sTOWfhIU+EhrXrWbQWAndWcT2+ZDYlI0ctJ5Bo1gM0MEU01+g3mhOf70I9DJrYGvoVYV815m+F46bjq8qVEL06zZZLEKCTPAgMBAAECgYEAjZl3rrvFp/NXpWRadtVJaoUm5ZVYp8g2nEtDVJG5mFlYU1TCKWWMY0kjAC6ie1zKnfA1C+b6NYn36zhR5FE/kTSwUYT1P6INT4rD7JUEiwE8hi4MTvWIDCyqeUmb2H+abHpBo9VZymmh5wmwRTi1PgPQGTDq5uP519lgD00DzEECQQDZzHPSvgy0GztmD6Uip1mHDI9j7syIEVCEPtuIsNzH63GQdR5jLH7hZn0WRXEgrZa+f3gKZnFIew+lj6Ip1lPRAkEAwrQrwe6dcioJHdc0PsW/In5TOBTY+ppVKhrkJ6x9UOzZT/BYuXUFVJL8kiKGIK0wihOzMhHK57HjoN5fT5w2nwJBAJ5D4npmXgbWrxAYGFCZOQZYyy28DmZl5pNitdabZqPj5A8r/Bvm7oBOIGF5rp4nZh4htJIiJPmdax5MxHMQarECQH8yMPvqpJT2fSovcwQnL2ybVkZm6DEfLc/p7W81slBxyq38eBoAJtFPjQzy3Ojv+6vYntJw6Ttf7TMk0uMxTEUCQDw1ZXU5jiKoktSYjHjFL2EfqtfT9F8xTHVyaruKL+R67Z0OvxMNlM0j77ADUS/BAFlwF2bCsSkMT3PLvcNtpPk=");
		System.out.println("加签-->" + aaa);
		System.out
				.println("验签-->"
						+ verify(
								str,
								aaa,
								"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClpjjBec0AanPMQONc6vQShyPBlHWXSUfKj+e5r/Mtje0/0w9WHjRMd1B+3ElqbGaXc1eQVigIR+7Ezln4SFPhIa161m0FgJ3VnE9vmQ2JSNHLSeQaNYDNDBFNNfoN5oTn+9CPQya2Br6FWFfNeZvheOm46vKlRC9Os2WSxCgkzwIDAQAB"));
		System.out.println("原文-->" + new String(Base64.decode(str)));
	}

}
