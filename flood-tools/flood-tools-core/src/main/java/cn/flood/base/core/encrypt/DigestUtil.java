/**
 * Copyright (c) 2018-2028,
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0; you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package cn.flood.base.core.encrypt;

import cn.flood.base.core.lang.StringPool;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.lang.Nullable;

/**
 * 加密相关工具类直接使用Spring util封装，减少jar依赖
 *
 * @author mmdai
 */
public class DigestUtil extends org.springframework.util.DigestUtils {

  private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

  /**
   * Calculates the MD5 digest and returns the value as a 32 character hex string.
   *
   * @param data Data to digest
   * @return MD5 digest as a hex string
   */
  public static String md5Hex(final String data) {
    return md5DigestAsHex(data.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Return a hexadecimal string representation of the MD5 digest of the given bytes.
   *
   * @param bytes the bytes to calculate the digest over
   * @return a hexadecimal digest string
   */
  public static String md5Hex(final byte[] bytes) {
    return md5DigestAsHex(bytes);
  }

  public static String sha1(String srcStr) {
    return hash("SHA-1", srcStr);
  }

  public static String sha256(String srcStr) {
    return hash("SHA-256", srcStr);
  }

  public static String sha384(String srcStr) {
    return hash("SHA-384", srcStr);
  }

  public static String sha512(String srcStr) {
    return hash("SHA-512", srcStr);
  }

  public static String hash(String algorithm, String srcStr) {
    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      byte[] bytes = md.digest(srcStr.getBytes(StandardCharsets.UTF_8));
      return toHex(bytes);
    } catch (NoSuchAlgorithmException e) {
      return StringPool.EMPTY;
    }
  }

  public static String toHex(byte[] bytes) {
    StringBuilder ret = new StringBuilder(bytes.length * 2);
    for (int i = 0; i < bytes.length; i++) {
      ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
      ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
    }
    return ret.toString();
  }

  public static boolean slowEquals(@Nullable String a, @Nullable String b) {
    if (a == null || b == null) {
      return false;
    }
    return slowEquals(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
  }

  public static boolean slowEquals(@Nullable byte[] a, @Nullable byte[] b) {
    if (a == null || b == null) {
      return false;
    }
    if (a.length != b.length) {
      return false;
    }
    int diff = a.length ^ b.length;
    for (int i = 0; i < a.length && i < b.length; i++) {
      diff |= a[i] ^ b[i];
    }
    return diff == 0;
  }

  /**
   * 自定义加密 先MD5再SHA1
   *
   * @param data 数据
   * @return String
   */
  public static String encrypt(String data) {
    return sha1(md5Hex(data));
  }

}
