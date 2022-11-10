package cn.flood.base.core.encrypt;

import cn.flood.base.core.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;


/**
 * 对称加密
 * 这里面的API里面有很多是调用getInstance方法，这个方法的参数有algorithm或者transformation
 * 一：algorithm：算法
 * 
 * 二：transformation：有两种格式
 * 1：算法/模式/填充方式。如：DES/CBC/PKCS5Padding
 * 2：算法。                              如：DES
 * 
 * 其中，algorithm、transformation的值，不区分大小写
 * 
 * Java加密解密官方参考文档：
 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/index.html
 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html
 */
public class DES {
	
	private byte[] desKey;

    public DES(String desKey) {
        this.desKey = desKey.getBytes();
    }

    public byte[] desEncrypt(byte[] plainText) throws Exception {
        SecureRandom sr = new SecureRandom();
        byte rawKeyData[] = desKey;
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, sr);
        byte data[] = plainText;
        byte encryptedData[] = cipher.doFinal(data);
        return encryptedData;
    }

    public byte[] desDecrypt(byte[] encryptText) throws Exception {
        SecureRandom sr = new SecureRandom();
        byte rawKeyData[] = desKey;
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, sr);
        byte encryptedData[] = encryptText;
        byte decryptedData[] = cipher.doFinal(encryptedData);
        return decryptedData;
    }

    public String encrypt(String input) throws Exception {
    	System.out.println(input);
        return new String(Base64.encode(desEncrypt(input.getBytes("UTF-8"))));
    }

    public String decrypt(String input) throws Exception {
        byte[] result = Base64.decode(input);
        return new String(desDecrypt(result),"UTF-8");
    }

    public String encrypt2(String input) throws Exception {
    	return new String(desEncrypt(input.getBytes("UTF-8")));
    }
    
    public String decrypt2(String input) throws Exception {
    	return new String(desDecrypt(input.getBytes()),"UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String key = "tg3hijl9ytre2sts0y";
        String input = "OG673L44Qayn7TdOSFthDAjxjqcsWkeSGcqU+yndQeci2O/o2TV6HW5wWw48oXjG4udZLmzitjEbeYgyXnKjTO/M/F3oqQkUjZReIMz3Ql+Vr3brokkDN6rJc1hUtTJxOoQYDk7wB7eIhjggkG3UxW5ytMf4HSuA";
        DES crypt = new DES(key);
//        String param = URLDecoder.decode(input,"UTF-8");//转码
        System.out.println("Encode:" + crypt.encrypt(input));
        System.out.println("Decode:" + crypt.decrypt(crypt.encrypt(input)));
        System.out.println("Decode:" + crypt.decrypt(input));
        
        System.out.println(new String(Base64.decode("MTExMTExMTExMTE")));
    }
}
