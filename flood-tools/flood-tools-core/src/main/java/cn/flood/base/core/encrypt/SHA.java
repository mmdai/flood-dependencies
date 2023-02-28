package cn.flood.base.core.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Title: SHA</p>
 * <p>Description: </p>
 *
 * @author mmdai
 * @date 2018年10月25日
 */
public class SHA {

  /**
   * @param args
   * @throws NoSuchAlgorithmException
   */
  public static void main(String[] args) throws NoSuchAlgorithmException {
    String msg = "爪哇笔记";
    SHA sha = new SHA();
    byte[] resultBytes = sha.eccrypt(msg);
    System.out.println("明文是：" + msg);
    System.out.println("密文是：" + new String(resultBytes));

  }

  public byte[] eccrypt(String info) throws NoSuchAlgorithmException {
    MessageDigest md5 = MessageDigest.getInstance("SHA");
    byte[] srcBytes = info.getBytes();
    //使用srcBytes更新摘要
    md5.update(srcBytes);
    //完成哈希计算，得到result
    byte[] resultBytes = md5.digest();
    return resultBytes;
  }

}
