package cn.flood.base.core.encrypt;

import cn.flood.base.core.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * 
* <p>Title: AES</p>  
* <p>Description: AES对称加密</p>  
* @author mmdai  
* @date 2018年10月25日
 */
public class AES {
	
	private final static String ENCODING = StandardCharsets.UTF_8.name();
	
	/**
	 * 
	 * <p>Title: encryptAES</p>  
	 * <p>Description: AES加密</p>  
	 * @param content
	 * @param password
	 * @return
	 */
	public static String encryptAES(String content, String password) {
		byte[] encryptResult = encrypt(content, password);
		String encryptResultStr = parseByte2HexStr(encryptResult);
		// BASE64位加密
		encryptResultStr = ebotongEncrypto(encryptResultStr);
		return encryptResultStr;
	}
	/**
	 * 
	 * <p>Title: decrypt</p>  
	 * <p>Description: AES解密</p>  
	 * @param encryptResultStr
	 * @param password
	 * @return
	 */
	public static String decrypt(String encryptResultStr, String password) {
		// BASE64位解密
		String decrpt = ebotongDecrypto(encryptResultStr);
		byte[] decryptFrom = parseHexStr2Byte(decrpt);
		byte[] decryptResult = decrypt(decryptFrom, password);
		return new String(decryptResult);
	}
	/**
	 * 
	 * <p>Title: ebotongEncrypto</p>  
	 * <p>Description: 加密字符串</p>  
	 * @param str
	 * @return
	 */
	public static String ebotongEncrypto(String str) {
		if (str != null && str.length() > 0) {
			String result = str;
			try {
				byte[] encodeByte = str.getBytes(ENCODING);
				//阿里巴巴
				//result = Base64.byteArrayToBase64(encodeByte);
				result  = Base64.encode(encodeByte);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//base64加密超过一定长度会自动换行 需要去除换行符
			return result.replace("\r\n", "").replace("\r", "").replace("\n", "");
		}
		return null;
	}
	/**
	 * 
	 * <p>Title: ebotongDecrypto</p>  
	 * <p>Description: 解密字符串</p>  
	 * @param str
	 * @return
	 */
	public static String ebotongDecrypto(String str) {
		try {
			//byte[] encodeByte  = Base64.base64ToByteArray(str);//阿里巴巴
			byte[] encodeByte  = Base64.decode(str);
			
			return new String(encodeByte);
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}
	/**
	 * 
	 * <p>Title: encrypt</p>  
	 * <p>Description: 加密</p>  
	 * @param content
	 * @param password
	 * @return
	 */
	private static byte[] encrypt(String content, String password) {   
		try {              
			KeyGenerator kgen = KeyGenerator.getInstance("AES"); 
			//防止linux下 随机生成key
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );   
			secureRandom.setSeed(password.getBytes());   
			kgen.init(256, secureRandom);
			SecretKey secretKey = kgen.generateKey();   
			byte[] enCodeFormat = secretKey.getEncoded();   
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			// 创建密码器
			Cipher cipher = Cipher.getInstance("AES");
			byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
			// 初始化
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(byteContent);
			// 加密
			return result;
		} catch (NoSuchAlgorithmException e) {   
			e.printStackTrace();   
		} catch (NoSuchPaddingException e) {   
			e.printStackTrace();   
		} catch (InvalidKeyException e) {   
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {   
			e.printStackTrace();   
		} catch (BadPaddingException e) {   
			e.printStackTrace();   
		}   
		return new byte[0];
	}  
	/**
	 * 
	 * <p>Title: decrypt</p>  
	 * <p>Description: 解密</p>  
	 * @param content
	 * @return
	 */
	private static byte[] decrypt(byte[] content, String password) {   
		try {   
			KeyGenerator kgen = KeyGenerator.getInstance("AES"); 
			//防止linux下 随机生成key
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );   
			secureRandom.setSeed(password.getBytes());   
			kgen.init(256, secureRandom);
			SecretKey secretKey = kgen.generateKey();   
			byte[] enCodeFormat = secretKey.getEncoded();   
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			// 创建密码器
			Cipher cipher = Cipher.getInstance("AES");
			// 初始化
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] result = cipher.doFinal(content);
			// 加密
			return result;
		} catch (NoSuchAlgorithmException e) {   
			e.printStackTrace();   
		} catch (NoSuchPaddingException e) {   
			e.printStackTrace();   
		} catch (InvalidKeyException e) {   
			e.printStackTrace();   
		} catch (IllegalBlockSizeException e) {   
			e.printStackTrace();   
		} catch (BadPaddingException e) {   
			e.printStackTrace();   
		}   
		return new byte[0];
	}  
	/**
	 * 
	 * <p>Title: parseByte2HexStr</p>  
	 * <p>Description: 将二进制转换成16进制  </p>  
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {   
		StringBuffer sb = new StringBuffer();   
		for (int i = 0; i < buf.length; i++) {   
			String hex = Integer.toHexString(buf[i] & 0xFF);   
			if (hex.length() == 1) {   
				hex = '0' + hex;   
			}   
			sb.append(hex.toUpperCase());   
		}   
		return sb.toString();   
	}  
	/**
	 * 
	 * <p>Title: parseHexStr2Byte</p>  
	 * <p>Description:将16进制转换为二进制   </p>  
	 * @param hexStr
	 * @return
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {   
		if (hexStr.length() < 1)   {
			return null;
		}
		byte[] result = new byte[hexStr.length()/2];   
		for (int i = 0;i< hexStr.length()/2; i++) {   
			int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);   
			int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);   
			result[i] = (byte) (high * 16 + low);   
		}   
		return result;   
	}
	
	public static void main(String[] args) {
		String str = encryptAES("580", "qwedcxza");
		System.out.println(str);
		str = decrypt(str, "qwedcxza");
		System.out.println(str);
	}
}
