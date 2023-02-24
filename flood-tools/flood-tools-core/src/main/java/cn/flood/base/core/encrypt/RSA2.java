/**  
* <p>Title: RSA2.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月26日  
* @version 1.0  
*/  
package cn.flood.base.core.encrypt;

import cn.flood.base.core.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**  
* <p>Title: RSA2</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月26日  
*/
public class RSA2 {
	
	public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String ENCODE_ALGORITHM = "SHA-256";

    /**
     * 使用 字符串 私钥 签名
     * @param privateKey
     * @param plain_text
     * @return
     */
   public static String sign(String privateKey,String plain_text) throws NoSuchAlgorithmException, InvalidKeySpecException {
       PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
       KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
       PrivateKey rsaPrivateKey =  keyf.generatePrivate(pkcs8EncodedKeySpec);
       byte[] signed = sign(rsaPrivateKey,plain_text);
       String res = Base64.getEncoder().encodeToString(signed);//通过Base64转码返回
       return res;//通过Base64转码返回
   }

    /**
     * 签名
     *
     * @param privateKey
     *            私钥
     * @param plain_text
     *            明文
     * @return
     */
    public static byte[] sign(PrivateKey privateKey, String plain_text) {
        MessageDigest messageDigest;
        byte[] signed = null;
        try {
            messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            messageDigest.update(plain_text.getBytes());
            byte[] outputDigest_sign = messageDigest.digest();
            Signature Sign = Signature.getInstance(SIGNATURE_ALGORITHM);
            Sign.initSign(privateKey);
            Sign.update(outputDigest_sign);
            signed = Sign.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
        }
        return signed;
    }

    /**
     *
     * @param publicKey
     * @param plain_text
     * @param signed
     * @return
     */
    public static boolean verifySign(String publicKey, String plain_text, String signed) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        KeyFactory keyf = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey rsaPrivateKey =  keyf.generatePublic(x509EncodedKeySpec);
        return verifySign(rsaPrivateKey,plain_text,Base64.getDecoder().decode(signed));
    }

    /**
     * 验签
     *
     * @param publicKey
     *            公钥
     * @param plain_text
     *            明文
     * @param signed
     *            签名
     */
    public static boolean verifySign(PublicKey publicKey, String plain_text, byte[] signed) {

        MessageDigest messageDigest;
        boolean SignedSuccess=false;
        try {
            messageDigest = MessageDigest.getInstance(ENCODE_ALGORITHM);
            messageDigest.update(plain_text.getBytes());
            byte[] outputDigest_verify = messageDigest.digest();
            Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifySign.initVerify(publicKey);
            verifySign.update(outputDigest_verify);
            SignedSuccess = verifySign.verify(signed);

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return SignedSuccess;
    }

    /**
     * bytes[]换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
	 * 解密数据，接收端接收到数据直接解密
	 * 
	 * @param content
	 * @param privateKey 私钥
	 * @return
	 */
	public static String decrypt(String content, String privateKey) {
		if (null == privateKey || "".equals(privateKey)) {
			return null;
		}
		PrivateKey pk = getPrivateKey(privateKey);
		byte[] data = decryptByPrivateKey(content, pk);
		String res =  new String(data, StandardCharsets.UTF_8);
		return res;
	}

	/**
	 * 对内容进行加密
	 * 
	 * @param content
	 * @param publicKey 公钥
	 * @return
	 */
	public static String encrypt(String content, String publicKey) {
		PublicKey pk = getPublicKey(publicKey);
		byte[] data = encryptByPublicKey(content, pk);
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
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			PrivateKey privatekey = keyFactory.generatePrivate(keySpec);
			return privatekey;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	 
    /**
	* 获取公钥对象
	* @param key 密钥字符串（经过base64编码秘钥字节）
	* @throws Exception
	*/
	public static PublicKey getPublicKey(String publicKey) {
		try {
			byte[] keyBytes;
			keyBytes = Base64.decode(publicKey);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			PublicKey publickey = keyFactory.generatePublic(keySpec);
			return publickey;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * 通过公钥解密
	 * 
	 * @param content待解密数据
	 * @param pk私钥
	 * @return 返回 解密后的数据
	 */
	protected static byte[] decryptByPrivateKey(String content, PrivateKey pk) {
		try {
			Cipher ch = Cipher.getInstance(KEY_ALGORITHM);
			ch.init(Cipher.DECRYPT_MODE, pk);
			InputStream ins = new ByteArrayInputStream(Base64.decode(content));
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			// rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
			byte[] buf = new byte[256];
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
	 * 通过公钥加密
	 * 
	 * @param content
	 * @param pk
	 * @return,加密数据，未进行base64进行加密
	 */
	protected static byte[] encryptByPublicKey(String content, PublicKey pk) {
		try {
			Cipher ch = Cipher.getInstance(KEY_ALGORITHM);
			ch.init(Cipher.ENCRYPT_MODE, pk);
			return ch.doFinal(content.getBytes( StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("通过公钥加密出错");
		}
		return null;
	}
	
	public static void main(String[] args) {
		String str = "18505933993";
		System.out.println("-->" + str);
		String aaa = encrypt(
				str,
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgwreKDq3W5Ru9Uxs+IYcm4NPDpiR7uOwHCcEdqLORJOzpVlg3fgSJK6tjy7M0VWIQ10zzm1mEpT0h8bf09OuJiVQTuFH8nyUkIODrGDiizUQo+J2cO5IoZYCgAFt/7DL2NPozvAJjgC3ZWecqbDlHre14z/jnH0Wjbwyea3+CZYEB4oCRRyTDYdLJNWVCpGYOScFuyEJM4M+OMDn2LMjHdJkLZo8Ouugmh0/3eBhBjC+IuDAIFdiNss0NXNlRvjYTzKD+4OiWsZcddAOfNS9hj+wEgbzV6HFp2baRWQqVZYqFKgZ9Mw34Ts9Fmr7QiRKejWjJstZA1BLHSZbP4GXOwIDAQAB"
				);
		System.out.println("加签-->" + aaa);
		System.out
				.println("验签-->"
						+ decrypt(
								aaa,
								"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCDCt4oOrdblG71TGz4hhybg08OmJHu47AcJwR2os5Ek7OlWWDd+BIkrq2PLszRVYhDXTPObWYSlPSHxt/T064mJVBO4UfyfJSQg4OsYOKLNRCj4nZw7kihlgKAAW3/sMvY0+jO8AmOALdlZ5ypsOUet7XjP+OcfRaNvDJ5rf4JlgQHigJFHJMNh0sk1ZUKkZg5JwW7IQkzgz44wOfYsyMd0mQtmjw666CaHT/d4GEGML4i4MAgV2I2yzQ1c2VG+NhPMoP7g6Jaxlx10A581L2GP7ASBvNXocWnZtpFZCpVlioUqBn0zDfhOz0WavtCJEp6NaMmy1kDUEsdJls/gZc7AgMBAAECggEATCG1PdQXEMqkyDiVmrF3z1WzSXyUVCDOAR0d97rQcYEKPsc1guAL0mn1hZkf/4Jcvx5X6dcfFS6a/oMpKMzzgU/uX1V80kFUorz1c+F53HyA5WW9gBC/+7jGTQ4HlSRm2dsLh6ZnNqvt4KkYmHGgO39p2dQVbaYywzeSb6UJURGUtK0WakABlfif7vgLQ+5XqvH+UhnOf+CWfVix7Zn37o65AMdOEUYOtt7Rw2wDtGBK5qc8hHApQpQaqMEf44FbPl3dfpYgbaxCHZS6hIz13jiXVz1+ay3Uivp0D7SjPjNb75Ouaie0RmF88KT9Thzd9UaXvwwePMYVuAjSUwhE8QKBgQDBZp209+O+Fb/gAdichc1/2cbRltzneqfKLAecFdeLccWufk3jgqtBXiRNlCmhERFlUVXu/mr6jQqCH58TIBPJHpkrWDUcc2rst8dMKvHZZYybsmUN+YsXKIeDMCcNzQMqfXVMChPe1EoIEJYrRHwH56hGzY0V5IL/oBfJAwDRgwKBgQCtdSyV3eyDhMjIthtW7mHrmS49MOKrZWWeyQC0hOaYajOI2T6ZD3gCeDm7AwfLf89A/01NQ+nMV/b5M5G2fzvvsRerbc9QH4xU7E6QAWidCcaZ3wghUuYef4ZObbyESvEgoyz7V6lN6LXiBPblA8gCHo2tNW163AVwq0cgpH/N6QKBgBWPZQFpqR/luNA2JHm+iNw54Oo4kUPU7qOhKPADl1XaRxlexzwXsxPEdPOZEtL73h6XZKcSAccQth0vfUJajxoaqSSl3rXfQApHqpYZK4D6yyQITS+zpAfe4syDL7dgJXw1JEBn1zM+I0qm3rbQGauf7aiG/bV1Fg19QWmNRigVAoGAPiqAkDW5TmmIjGa35bAJ6CY+LDAZDNSKVx4y4n+/JWDql2FEzhZ9LGVqS1wKryRfYywcyFROtq3QFgAleQKguIb/tLDmXBDyen02quXWWHHoe70zbL4JO2T4/aLWrZMuWEQjGvGP/BwrE5Yz5mA0jo3kuO8tDZtNV9QK6egK5gECgYAif7DeTq3s4g9NeAKpXYR4iyhtu0JkLh7n7JRAeQsGBP82hqGHTc3d4dqJZx1Rpd1c1Dy3KYdIgc4LnYx6HzSQds/PIblhMu3dG7b99rvWeEVh+iAL6r0tF+j/nSbVno7XswHwq2xr47OkZINiO0eXH4fratnFPPiON9caa6zVpg=="
								));
	}
   

}
