/**
 * <p>Title: Base64.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2019年7月19日
 * @version 1.0
 */
package cn.flood.base.core.binary;

import cn.flood.base.core.lang.ArrayUtils;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.util.ObjectUtils;

/**
 * <p>Title: Base64</p>  
 * <p>Description: </p>  
 * @author mmdai
 * @date 2019年7月19日
 */
public class Base64 {

  private Base64() {
    throw new UnsupportedOperationException();
  }

  /**
   * 标准的BASE64编码
   *
   * @param data 待BASE64编码的字节数组
   * @return 待BASE64编码的字符串
   */
  public static String encode(final byte[] data) {
    //return DatatypeConverter.printBase64Binary(data);
    return encode(data, false);
  }

  /**
   * BASE64编码
   *
   * @param data      待BASE64编码的字节数组
   * @param isUrlSafe 为{@code true}则URL安全字符，否则为标准BASE64字符
   * @return 待BASE64编码的字符串
   */
  public static String encode(final byte[] data, final boolean isUrlSafe) {
    if (ArrayUtils.isEmpty(data)) {
      return null;
    }
    return isUrlSafe ? getUrlEncoder().encodeToString(data) : getEncoder().encodeToString(data);
  }

  /**
   * 标准的BASE64编码
   *
   * @param data 待BASE64编码的字符串
   * @return BASE64之后的字符串
   */
  public static String encode(final String data) {
    return encode(data, StandardCharsets.ISO_8859_1);
  }

  /**
   * 标准的BASE64编码
   *
   * @param data      待BASE64编码的字符串
   * @param isUrlSafe 为{@code true}则URL安全字符，否则为标准BASE64字符
   * @return BASE64之后的字符串
   */
  public static String encode(final String data, final boolean isUrlSafe) {
    return encode(data, StandardCharsets.ISO_8859_1, isUrlSafe);
  }

  /**
   * 标准的BASE64编码
   *
   * @param data     待BASE64编码的字符串
   * @param encoding 原始数据的编码,为空时采用UTF-8编码
   * @return BASE64之后的字符串
   */
  public static String encode(final String data, final Charset encoding) {
    if (ObjectUtils.isEmpty(data)) {
      return data;
    }
    if (encoding == null) {
      return null;
    }

    return encode(data, encoding, false);
  }

  /**
   * 标准的BASE64编码
   *
   * @param data      待BASE64编码的字符串
   * @param encoding  原始数据的编码,为空时采用UTF-8编码
   * @param isUrlSafe 为{@code true}则URL安全字符，否则为标准BASE64字符
   * @return BASE64之后的字符串
   */
  public static String encode(final String data, final Charset encoding, final boolean isUrlSafe) {
    if (ObjectUtils.isEmpty(data)) {
      return data;
    }
    if (encoding == null) {
      return null;
    }
    //        Charset charset = ObjectUtils.isEmpty(encoding) ? StandardCharsets.UTF_8 : Charset.forName(encoding);
//	        return DatatypeConverter.printBase64Binary(data.getBytes(charset));
    return encode(data.getBytes(encoding), isUrlSafe);
  }

  /**
   * 标准的BASE64解码
   *
   * @param base64Data 已经编码过的字符串
   * @return 返回原始数据的字节数组形式
   */
  public static byte[] decode(final String base64Data) {
//	        Assert.hasLength(base64Data, "The Base64 data is null.");
//	        return DatatypeConverter.parseBase64Binary(base64Data);
    return decode(base64Data, false);
  }

  /**
   * 标准的BASE64解码
   *
   * @param base64Data 已经编码过的字符串
   * @param isUrlSafe  为{@code true}则URL安全字符，否则为标准BASE64字符
   * @return 返回原始数据的字节数组形式
   */
  public static byte[] decode(final String base64Data, final boolean isUrlSafe) {
    if (ObjectUtils.isEmpty(base64Data)) {
      return null;
    }
    return isUrlSafe ? getUrlDecoder().decode(base64Data) : getDecoder().decode(base64Data);
  }

  /**
   * 标准的BASE64解码
   *
   * @param base64Data 已经编码过的字符串
   * @return 原始数据
   */
  public static String decode2String(final String base64Data) {
    return decode2String(base64Data, false);
  }

  /**
   * 标准的BASE64解码
   *
   * @param base64Data 已经编码过的字符串
   * @param isUrlSafe  原始数据的编码,为空时采用UTF-8编码
   * @return 原始数据
   */
  public static String decode2String(final String base64Data, final boolean isUrlSafe) {
    if (ObjectUtils.isEmpty(base64Data)) {
      return base64Data;
    }
    byte[] decodeData = decode(base64Data, isUrlSafe);
    return new String(decodeData);
  }

  //region 以下代码来源于JDK1.8

  /**
   * Returns a {@link Encoder} that encodes using the
   * <a href="#basic">Basic</a> type base64 encoding scheme.
   *
   * @return A Base64 encoder.
   */
  public static Encoder getEncoder() {
    return Encoder.RFC4648;
  }

  /**
   * Returns a {@link Encoder} that encodes using the
   * <a href="#url">URL and Filename safe</a> type base64
   * encoding scheme.
   *
   * @return A Base64 encoder.
   */
  public static Encoder getUrlEncoder() {
    return Encoder.RFC4648_URLSAFE;
  }

  /**
   * Returns a {@link Encoder} that encodes using the
   * <a href="#mime">MIME</a> type base64 encoding scheme.
   *
   * @return A Base64 encoder.
   */
  public static Encoder getMimeEncoder() {
    return Encoder.RFC2045;
  }

  /**
   * Returns a {@link Encoder} that encodes using the
   * <a href="#mime">MIME</a> type base64 encoding scheme
   * with specified line length and line separators.
   *
   * @param lineLength    the length of each output line (rounded down to nearest multiple
   *                      of 4). If {@code lineLength <= 0} the output will not be separated
   *                      in lines
   * @param lineSeparator the line separator for each output line
   * @return A Base64 encoder.
   * @throws IllegalArgumentException if {@code lineSeparator} includes any
   *                                  character of "The Base64 Alphabet" as specified in Table 1 of
   *                                  RFC 2045.
   */
  public static Encoder getMimeEncoder(int lineLength, byte[] lineSeparator) {
    Objects.requireNonNull(lineSeparator);
    int[] base64 = Decoder.FROM_BASE64;
    for (byte b : lineSeparator) {
      if (base64[b & 0xff] != -1) {
        throw new IllegalArgumentException(
            "Illegal base64 line separator character 0x" + Integer.toString(b, 16));
      }
    }
    if (lineLength <= 0) {
      return Encoder.RFC4648;
    }
    return new Encoder(false, lineSeparator, lineLength >> 2 << 2, true);
  }

  /**
   * Returns a {@link Decoder} that decodes using the
   * <a href="#basic">Basic</a> type base64 encoding scheme.
   *
   * @return A Base64 decoder.
   */
  public static Decoder getDecoder() {
    return Decoder.RFC4648;
  }

  /**
   * Returns a {@link Decoder} that decodes using the
   * <a href="#url">URL and Filename safe</a> type base64
   * encoding scheme.
   *
   * @return A Base64 decoder.
   */
  public static Decoder getUrlDecoder() {
    return Decoder.RFC4648_URLSAFE;
  }

  /**
   * Returns a {@link Decoder} that decodes using the
   * <a href="#mime">MIME</a> type base64 decoding scheme.
   *
   * @return A Base64 decoder.
   */
  public static Decoder getMimeDecoder() {
    return Decoder.RFC2045;
  }

  //endregion
  public static void main(String[] args) {
    String en_value = Base64.encode("fasdfdafsafsadf123sa".getBytes());
    System.out.println(en_value);
    String source = new String(Base64.decode(en_value));
    System.out.println(source);
  }

  /**
   * <p>
   * This class implements an encoder for encoding byte data using
   * the Base64 encoding scheme as specified in RFC 4648 and RFC 2045.
   * </p>
   * <p> Instances of {@link Encoder} class are safe for use by
   * multiple concurrent threads.
   * </p>
   * <p> Unless otherwise noted, passing a {@code null} argument to
   * a method of this class will cause a
   * {@link NullPointerException NullPointerException} to
   * be thrown.
   * </p>
   *
   * @see Decoder
   * @since 1.8
   */
  public static class Encoder {

    static final Encoder RFC4648 = new Encoder(false, null, -1, true);
    static final Encoder RFC4648_URLSAFE = new Encoder(true, null, -1, true);
    /**
     * This array is a lookup table that translates 6-bit positive integer
     * index values into their "Base64 Alphabet" equivalents as specified
     * in "Table 1: The Base64 Alphabet" of RFC 2045 (and RFC 4648).
     */
    private static final char[] TO_BASE64 = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
    /**
     * It's the lookup table for "URL and Filename safe Base64" as specified
     * in Table 2 of the RFC 4648, with the '+' and '/' changed to '-' and
     * '_'. This table is used when BASE64_URL is specified.
     */
    private static final char[] TO_BASE64URL = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
    };
    private static final int MIMELINEMAX = 76;
    private static final byte[] CRLF = new byte[]{'\r', '\n'};
    static final Encoder RFC2045 = new Encoder(false, CRLF, MIMELINEMAX, true);
    private final byte[] newline;
    private final int linemax;
    private final boolean isURL;
    private final boolean doPadding;
    private Encoder(boolean isURL, byte[] newline, int linemax, boolean doPadding) {
      this.isURL = isURL;
      this.newline = newline;
      this.linemax = linemax;
      this.doPadding = doPadding;
    }

    private final int outLength(int srclen) {
      int len = 0;
      if (doPadding) {
        len = 4 * ((srclen + 2) / 3);
      } else {
        int n = srclen % 3;
        len = 4 * (srclen / 3) + (n == 0 ? 0 : n + 1);
      }
      if (linemax > 0) {
        // line separators
        len += (len - 1) / linemax * newline.length;
      }
      return len;
    }

    /**
     * Encodes all bytes from the specified byte array into a newly-allocated
     * byte array using the {@link Base64} encoding scheme. The returned byte
     * array is of the length of the resulting bytes.
     *
     * @param src the byte array to encode
     * @return A newly-allocated byte array containing the resulting
     * encoded bytes.
     */
    public byte[] encode(byte[] src) {
      int len = outLength(src.length);          // dst array size
      byte[] dst = new byte[len];
      int ret = encode0(src, 0, src.length, dst);
      if (ret != dst.length) {
        return Arrays.copyOf(dst, ret);
      }
      return dst;
    }

    /**
     * <p>
     * Encodes all bytes from the specified byte array using the
     * {@link Base64} encoding scheme, writing the resulting bytes to the
     * given output byte array, starting at offset 0.
     * </p>
     * <p> It is the responsibility of the invoker of this method to make
     * sure the output byte array {@code dst} has enough space for encoding
     * all bytes from the input byte array. No bytes will be written to the
     * output byte array if the output byte array is not big enough.
     * </p>
     *
     * @param src the byte array to encode
     * @param dst the output byte array
     * @return The number of bytes written to the output byte array
     * @throws IllegalArgumentException if {@code dst} does not have enough
     *                                  space for encoding all input bytes.
     */
    public int encode(byte[] src, byte[] dst) {
      int len = outLength(src.length);         // dst array size
      if (dst.length < len) {
        throw new IllegalArgumentException(
            "Output byte array is too small for encoding all input bytes");
      }
      return encode0(src, 0, src.length, dst);
    }

    /**
     * <p>
     * Encodes the specified byte array into a String using the {@link Base64}
     * encoding scheme.
     * </p>
     * <p> This method first encodes all input bytes into a base64 encoded
     * byte array and then constructs a new String by using the encoded byte
     * array and the {@link StandardCharsets#ISO_8859_1
     * ISO-8859-1} charset.
     * </p>
     * <p> In other words, an invocation of this method has exactly the same
     * effect as invoking
     * </p>
     * {@code new String(encode(src), StandardCharsets.ISO_8859_1)}.
     *
     * @param src the byte array to encode
     * @return A String containing the resulting Base64 encoded characters
     */
    @SuppressWarnings("deprecation")
    public String encodeToString(byte[] src) {
      byte[] encoded = encode(src);
      return new String(encoded, 0, 0, encoded.length);
    }

    /**
     * <p>
     * Encodes all remaining bytes from the specified byte buffer into
     * a newly-allocated ByteBuffer using the {@link Base64} encoding
     * scheme.
     * </p>
     * <p>
     * Upon return, the source buffer's position will be updated to
     * its limit; its limit will not have been changed. The returned
     * output buffer's position will be zero and its limit will be the
     * number of resulting encoded bytes.
     * </p>
     *
     * @param buffer the source ByteBuffer to encode
     * @return A newly-allocated byte buffer containing the encoded bytes.
     */
    public ByteBuffer encode(ByteBuffer buffer) {
      int len = outLength(buffer.remaining());
      byte[] dst = new byte[len];
      int ret = 0;
      if (buffer.hasArray()) {
        ret = encode0(buffer.array(),
            buffer.arrayOffset() + buffer.position(),
            buffer.arrayOffset() + buffer.limit(),
            dst);
        buffer.position(buffer.limit());
      } else {
        byte[] src = new byte[buffer.remaining()];
        buffer.get(src);
        ret = encode0(src, 0, src.length, dst);
      }
      if (ret != dst.length) {
        dst = Arrays.copyOf(dst, ret);
      }
      return ByteBuffer.wrap(dst);
    }

    /**
     * <p>
     * Wraps an output stream for encoding byte data using the {@link Base64}
     * encoding scheme.
     * </p>
     * <p> It is recommended to promptly close the returned output stream after
     * use, during which it will flush all possible leftover bytes to the underlying
     * output stream. Closing the returned output stream will close the underlying
     * output stream.
     * </p>
     *
     * @param os the output stream.
     * @return the output stream for encoding the byte data into the
     * specified Base64 encoded format
     */
    public OutputStream wrap(OutputStream os) {
      Objects.requireNonNull(os);
      return new EncOutputStream(os, isURL ? TO_BASE64URL : TO_BASE64,
          newline, linemax, doPadding);
    }

    /**
     * <p>
     * Returns an encoder instance that encodes equivalently to this one,
     * but without adding any padding character at the end of the encoded
     * byte data.
     * </p>
     * <p> The encoding scheme of this encoder instance is unaffected by
     * this invocation. The returned encoder instance should be used for
     * non-padding encoding operation.
     * </p>
     *
     * @return an equivalent encoder that encodes without adding any
     * padding character at the end
     */
    public Encoder withoutPadding() {
      if (!doPadding) {
        return this;
      }
      return new Encoder(isURL, newline, linemax, false);
    }

    private int encode0(byte[] src, int off, int end, byte[] dst) {
      char[] base64 = isURL ? TO_BASE64URL : TO_BASE64;
      int sp = off;
      int slen = (end - off) / 3 * 3;
      int sl = off + slen;
      if (linemax > 0 && slen > linemax / 4 * 3) {
        slen = linemax / 4 * 3;
      }
      int dp = 0;
      while (sp < sl) {
        int sl0 = Math.min(sp + slen, sl);
        for (int sp0 = sp, dp0 = dp; sp0 < sl0; ) {
          int bits = (src[sp0++] & 0xff) << 16 |
              (src[sp0++] & 0xff) << 8 |
              (src[sp0++] & 0xff);
          dst[dp0++] = (byte) base64[(bits >>> 18) & 0x3f];
          dst[dp0++] = (byte) base64[(bits >>> 12) & 0x3f];
          dst[dp0++] = (byte) base64[(bits >>> 6) & 0x3f];
          dst[dp0++] = (byte) base64[bits & 0x3f];
        }
        int dlen = (sl0 - sp) / 3 * 4;
        dp += dlen;
        sp = sl0;
        if (dlen == linemax && sp < end) {
          for (byte b : newline) {
            dst[dp++] = b;
          }
        }
      }
      if (sp < end) {               // 1 or 2 leftover bytes
        int b0 = src[sp++] & 0xff;
        dst[dp++] = (byte) base64[b0 >> 2];
        if (sp == end) {
          dst[dp++] = (byte) base64[(b0 << 4) & 0x3f];
          if (doPadding) {
            dst[dp++] = '=';
            dst[dp++] = '=';
          }
        } else {
          int b1 = src[sp++] & 0xff;
          dst[dp++] = (byte) base64[(b0 << 4) & 0x3f | (b1 >> 4)];
          dst[dp++] = (byte) base64[(b1 << 2) & 0x3f];
          if (doPadding) {
            dst[dp++] = '=';
          }
        }
      }
      return dp;
    }
  }

  /**
   * <p>
   * This class implements a decoder for decoding byte data using the
   * Base64 encoding scheme as specified in RFC 4648 and RFC 2045.
   * </p>
   * <p> The Base64 padding character {@code '='} is accepted and
   * interpreted as the end of the encoded byte data, but is not
   * required. So if the final unit of the encoded byte data only has
   * two or three Base64 characters (without the corresponding padding
   * character(s) padded), they are decoded as if followed by padding
   * character(s). If there is a padding character present in the
   * final unit, the correct number of padding character(s) must be
   * present, otherwise {@code IllegalArgumentException} (
   * {@code IOException} when reading from a Base64 stream) is thrown
   * during decoding.
   * </p>
   * <p> Instances of {@link Decoder} class are safe for use by
   * multiple concurrent threads.
   * </p>
   * <p> Unless otherwise noted, passing a {@code null} argument to
   * a method of this class will cause a
   * {@link NullPointerException NullPointerException} to
   * be thrown.
   * </p>
   *
   * @see Encoder
   * @since 1.8
   */
  public static class Decoder {

    static final Decoder RFC4648 = new Decoder(false, false);
    static final Decoder RFC4648_URLSAFE = new Decoder(true, false);
    static final Decoder RFC2045 = new Decoder(false, true);
    /**
     * Lookup table for decoding unicode characters drawn from the
     * "Base64 Alphabet" (as specified in Table 1 of RFC 2045) into
     * their 6-bit positive integer equivalents.  Characters that
     * are not in the Base64 alphabet but fall within the bounds of
     * the array are encoded to -1.
     */
    private static final int[] FROM_BASE64 = new int[256];
    /**
     * Lookup table for decoding "URL and Filename safe Base64 Alphabet"
     * as specified in Table2 of the RFC 4648.
     */
    private static final int[] FROM_BASE64URL = new int[256];

    static {
      Arrays.fill(FROM_BASE64, -1);
      for (int i = 0; i < Encoder.TO_BASE64.length; i++) {
        FROM_BASE64[Encoder.TO_BASE64[i]] = i;
      }
      FROM_BASE64['='] = -2;
    }

    static {
      Arrays.fill(FROM_BASE64URL, -1);
      for (int i = 0; i < Encoder.TO_BASE64URL.length; i++) {
        FROM_BASE64URL[Encoder.TO_BASE64URL[i]] = i;
      }
      FROM_BASE64URL['='] = -2;
    }

    private final boolean isURL;
    private final boolean isMIME;
    private Decoder(boolean isURL, boolean isMIME) {
      this.isURL = isURL;
      this.isMIME = isMIME;
    }

    /**
     * Decodes all bytes from the input byte array using the {@link Base64}
     * encoding scheme, writing the results into a newly-allocated output
     * byte array. The returned byte array is of the length of the resulting
     * bytes.
     *
     * @param src the byte array to decode
     * @return A newly-allocated byte array containing the decoded bytes.
     * @throws IllegalArgumentException if {@code src} is not in valid Base64 scheme
     */
    public byte[] decode(byte[] src) {
      byte[] dst = new byte[outLength(src, 0, src.length)];
      int ret = decode0(src, 0, src.length, dst);
      if (ret != dst.length) {
        dst = Arrays.copyOf(dst, ret);
      }
      return dst;
    }

    /**
     * <p>
     * Decodes a Base64 encoded String into a newly-allocated byte array
     * using the {@link Base64} encoding scheme.
     * </p>
     * <p> An invocation of this method has exactly the same effect as invoking
     * {@code decode(src.getBytes(StandardCharsets.ISO_8859_1))}
     * </p>
     *
     * @param src the string to decode
     * @return A newly-allocated byte array containing the decoded bytes.
     * @throws IllegalArgumentException if {@code src} is not in valid Base64 scheme
     */
    public byte[] decode(String src) {
      return decode(src.getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * <p>
     * Decodes all bytes from the input byte array using the {@link Base64}
     * encoding scheme, writing the results into the given output byte array,
     * starting at offset 0.
     * </p>
     * <p> It is the responsibility of the invoker of this method to make
     * sure the output byte array {@code dst} has enough space for decoding
     * all bytes from the input byte array. No bytes will be be written to
     * the output byte array if the output byte array is not big enough.
     * </p>
     * <p> If the input byte array is not in valid Base64 encoding scheme
     * then some bytes may have been written to the output byte array before
     * IllegalargumentException is thrown.
     * </p>
     *
     * @param src the byte array to decode
     * @param dst the output byte array
     * @return The number of bytes written to the output byte array
     * @throws IllegalArgumentException if {@code src} is not in valid Base64 scheme, or {@code dst}
     *                                  does not have enough space for decoding all input bytes.
     */
    public int decode(byte[] src, byte[] dst) {
      int len = outLength(src, 0, src.length);
      if (dst.length < len) {
        throw new IllegalArgumentException(
            "Output byte array is too small for decoding all input bytes");
      }
      return decode0(src, 0, src.length, dst);
    }

    /**
     * <p>
     * Decodes all bytes from the input byte buffer using the {@link Base64}
     * encoding scheme, writing the results into a newly-allocated ByteBuffer.
     * </p>
     * <p> Upon return, the source buffer's position will be updated to
     * its limit; its limit will not have been changed. The returned
     * output buffer's position will be zero and its limit will be the
     * number of resulting decoded bytes
     * </p>
     * <p> {@code IllegalArgumentException} is thrown if the input buffer
     * is not in valid Base64 encoding scheme. The position of the input
     * buffer will not be advanced in this case.
     * </p>
     *
     * @param buffer the ByteBuffer to decode
     * @return A newly-allocated byte buffer containing the decoded bytes
     * @throws IllegalArgumentException if {@code src} is not in valid Base64 scheme.
     */
    public ByteBuffer decode(ByteBuffer buffer) {
      int pos0 = buffer.position();
      try {
        byte[] src;
        int sp, sl;
        if (buffer.hasArray()) {
          src = buffer.array();
          sp = buffer.arrayOffset() + buffer.position();
          sl = buffer.arrayOffset() + buffer.limit();
          buffer.position(buffer.limit());
        } else {
          src = new byte[buffer.remaining()];
          buffer.get(src);
          sp = 0;
          sl = src.length;
        }
        byte[] dst = new byte[outLength(src, sp, sl)];
        return ByteBuffer.wrap(dst, 0, decode0(src, sp, sl, dst));
      } catch (IllegalArgumentException iae) {
        buffer.position(pos0);
        throw iae;
      }
    }

    /**
     * <p>
     * Returns an input stream for decoding {@link Base64} encoded byte stream.
     * </p>
     * <p> The {@code read}  methods of the returned {@code InputStream} will
     * throw {@code IOException} when reading bytes that cannot be decoded.
     * </p>
     * <p> Closing the returned input stream will close the underlying
     * input stream.
     *
     * @param is the input stream
     * @return the input stream for decoding the specified Base64 encoded
     * byte stream
     */
    public InputStream wrap(InputStream is) {
      Objects.requireNonNull(is);
      return new DecInputStream(is, isURL ? FROM_BASE64URL : FROM_BASE64, isMIME);
    }

    private int outLength(byte[] src, int sp, int sl) {
      int[] base64 = isURL ? FROM_BASE64URL : FROM_BASE64;
      int paddings = 0;
      int len = sl - sp;
      if (len == 0) {
        return 0;
      }
      if (len < 2) {
        if (isMIME && base64[0] == -1) {
          return 0;
        }
        throw new IllegalArgumentException(
            "Input byte[] should at least have 2 bytes for base64 bytes");
      }
      if (isMIME) {
        // scan all bytes to fill out all non-alphabet. a performance
        // trade-off of pre-scan or Arrays.copyOf
        int n = 0;
        while (sp < sl) {
          int b = src[sp++] & 0xff;
          if (b == '=') {
            len -= (sl - sp + 1);
            break;
          }
          if ((b = base64[b]) == -1) {
            n++;
          }
        }
        len -= n;
      } else {
        if (src[sl - 1] == '=') {
          paddings++;
          if (src[sl - 2] == '=') {
            paddings++;
          }
        }
      }
      if (paddings == 0 && (len & 0x3) != 0) {
        paddings = 4 - (len & 0x3);
      }
      return 3 * ((len + 3) / 4) - paddings;
    }

    private int decode0(byte[] src, int sp, int sl, byte[] dst) {
      int[] base64 = isURL ? FROM_BASE64URL : FROM_BASE64;
      int dp = 0;
      int bits = 0;
      int shiftto = 18;       // pos of first byte of 4-byte atom
      while (sp < sl) {
        int b = src[sp++] & 0xff;
        if ((b = base64[b]) < 0) {
          if (b == -2) {         // padding byte '='
            // =     shiftto==18 unnecessary padding
            // x=    shiftto==12 a dangling single x
            // x     to be handled together with non-padding case
            // xx=   shiftto==6&&sp==sl missing last =
            // xx=y  shiftto==6 last is not =
            if (shiftto == 6 && (sp == sl || src[sp++] != '=') ||
                shiftto == 18) {
              throw new IllegalArgumentException(
                  "Input byte array has wrong 4-byte ending unit");
            }
            break;
          }
          if (isMIME) {
            // skip if for rfc2045
            continue;
          } else {
            throw new IllegalArgumentException(
                "Illegal base64 character " +
                    Integer.toString(src[sp - 1], 16));
          }
        }
        bits |= (b << shiftto);
        shiftto -= 6;
        if (shiftto < 0) {
          dst[dp++] = (byte) (bits >> 16);
          dst[dp++] = (byte) (bits >> 8);
          dst[dp++] = (byte) (bits);
          shiftto = 18;
          bits = 0;
        }
      }
      // reached end of byte array or hit padding '=' characters.
      if (shiftto == 6) {
        dst[dp++] = (byte) (bits >> 16);
      } else if (shiftto == 0) {
        dst[dp++] = (byte) (bits >> 16);
        dst[dp++] = (byte) (bits >> 8);
      } else if (shiftto == 12) {
        // dangling single "x", incorrectly encoded.
        throw new IllegalArgumentException(
            "Last unit does not have enough valid bits");
      }
      // anything left is invalid, if is not MIME.
      // if MIME, ignore all non-base64 character
      while (sp < sl) {
        if (isMIME && base64[src[sp++]] < 0) {
          continue;
        }
        throw new IllegalArgumentException(
            "Input byte array has incorrect ending byte at " + sp);
      }
      return dp;
    }
  }

  /*
   * An output stream for encoding bytes into the Base64.
   */
  private static class EncOutputStream extends FilterOutputStream {

    private final char[] base64;    // byte->base64 mapping
    private final byte[] newline;   // line separator, if needed
    private final int linemax;
    private final boolean doPadding;// whether or not to pad
    private int leftover = 0;
    private int b0, b1, b2;
    private boolean closed = false;
    private int linepos = 0;

    EncOutputStream(OutputStream os, char[] base64,
        byte[] newline, int linemax, boolean doPadding) {
      super(os);
      this.base64 = base64;
      this.newline = newline;
      this.linemax = linemax;
      this.doPadding = doPadding;
    }

    @Override
    public void write(int b) throws IOException {
      byte[] buf = new byte[1];
      buf[0] = (byte) (b & 0xff);
      write(buf, 0, 1);
    }

    private void checkNewline() throws IOException {
      if (linepos == linemax) {
        out.write(newline);
        linepos = 0;
      }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      if (closed) {
        throw new IOException("Stream is closed");
      }
      if (off < 0 || len < 0 || off + len > b.length) {
        throw new ArrayIndexOutOfBoundsException();
      }
      if (len == 0) {
        return;
      }
      if (leftover != 0) {
        if (leftover == 1) {
          b1 = b[off++] & 0xff;
          len--;
          if (len == 0) {
            leftover++;
            return;
          }
        }
        b2 = b[off++] & 0xff;
        len--;
        checkNewline();
        out.write(base64[b0 >> 2]);
        out.write(base64[(b0 << 4) & 0x3f | (b1 >> 4)]);
        out.write(base64[(b1 << 2) & 0x3f | (b2 >> 6)]);
        out.write(base64[b2 & 0x3f]);
        linepos += 4;
      }
      int nBits24 = len / 3;
      leftover = len - (nBits24 * 3);
      while (nBits24-- > 0) {
        checkNewline();
        int bits = (b[off++] & 0xff) << 16 |
            (b[off++] & 0xff) << 8 |
            (b[off++] & 0xff);
        out.write(base64[(bits >>> 18) & 0x3f]);
        out.write(base64[(bits >>> 12) & 0x3f]);
        out.write(base64[(bits >>> 6) & 0x3f]);
        out.write(base64[bits & 0x3f]);
        linepos += 4;
      }
      if (leftover == 1) {
        b0 = b[off++] & 0xff;
      } else if (leftover == 2) {
        b0 = b[off++] & 0xff;
        b1 = b[off++] & 0xff;
      }
    }

    @Override
    public void close() throws IOException {
      if (!closed) {
        closed = true;
        if (leftover == 1) {
          checkNewline();
          out.write(base64[b0 >> 2]);
          out.write(base64[(b0 << 4) & 0x3f]);
          if (doPadding) {
            out.write('=');
            out.write('=');
          }
        } else if (leftover == 2) {
          checkNewline();
          out.write(base64[b0 >> 2]);
          out.write(base64[(b0 << 4) & 0x3f | (b1 >> 4)]);
          out.write(base64[(b1 << 2) & 0x3f]);
          if (doPadding) {
            out.write('=');
          }
        }
        leftover = 0;
        out.close();
      }
    }
  }

  /*
   * An input stream for decoding Base64 bytes
   */
  private static class DecInputStream extends InputStream {

    private final InputStream is;
    private final boolean isMIME;
    private final int[] base64;      // base64 -> byte mapping
    private int bits = 0;            // 24-bit buffer for decoding
    private int nextin = 18;         // next available "off" in "bits" for input;
    // -> 18, 12, 6, 0
    private int nextout = -8;        // next available "off" in "bits" for output;
    // -> 8, 0, -8 (no byte for output)
    private boolean eof = false;
    private boolean closed = false;
    private byte[] sbBuf = new byte[1];

    DecInputStream(InputStream is, int[] base64, boolean isMIME) {
      this.is = is;
      this.base64 = base64;
      this.isMIME = isMIME;
    }

    @Override
    public int read() throws IOException {
      return read(sbBuf, 0, 1) == -1 ? -1 : sbBuf[0] & 0xff;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      if (closed) {
        throw new IOException("Stream is closed");
      }

      if (eof && nextout < 0) {
        // eof and no leftover
        return -1;
      }
      if (off < 0 || len < 0 || len > b.length - off) {
        throw new IndexOutOfBoundsException();
      }
      int oldOff = off;
      if (nextout >= 0) {       // leftover output byte(s) in bits buf
        do {
          if (len == 0) {
            return off - oldOff;
          }
          b[off++] = (byte) (bits >> nextout);
          len--;
          nextout -= 8;
        } while (nextout >= 0);
        bits = 0;
      }
      while (len > 0) {
        int v = is.read();
        if (v == -1) {
          eof = true;
          if (nextin != 18) {
            if (nextin == 12) {
              throw new IOException("Base64 stream has one un-decoded dangling byte.");
            }
            // treat ending xx/xxx without padding character legal.
            // same logic as v == '=' below
            b[off++] = (byte) (bits >> (16));
            len--;
            if (nextin == 0) {           // only one padding byte
              if (len == 0) {          // no enough output space
                bits >>= 8;          // shift to lowest byte
                nextout = 0;
              } else {
                b[off++] = (byte) (bits >> 8);
              }
            }
          }
          if (off == oldOff) {
            return -1;
          } else {
            return off - oldOff;
          }
        }
        if (v == '=') {                  // padding byte(s)
          // =     shiftto==18 unnecessary padding
          // x=    shiftto==12 dangling x, invalid unit
          // xx=   shiftto==6 && missing last '='
          // xx=y  or last is not '='
          if (nextin == 18 || nextin == 12 ||
              nextin == 6 && is.read() != '=') {
            throw new IOException("Illegal base64 ending sequence:" + nextin);
          }
          b[off++] = (byte) (bits >> (16));
          len--;
          if (nextin == 0) {           // only one padding byte
            if (len == 0) {          // no enough output space
              bits >>= 8;          // shift to lowest byte
              nextout = 0;
            } else {
              b[off++] = (byte) (bits >> 8);
            }
          }
          eof = true;
          break;
        }
        if ((v = base64[v]) == -1) {
          if (isMIME) {
            // skip if for rfc2045
            continue;
          } else {
            throw new IOException("Illegal base64 character " +
                Integer.toString(v, 16));
          }
        }
        bits |= (v << nextin);
        if (nextin == 0) {
          nextin = 18;    // clear for next
          nextout = 16;
          while (nextout >= 0) {
            b[off++] = (byte) (bits >> nextout);
            len--;
            nextout -= 8;
            if (len == 0 && nextout >= 0) {  // don't clean "bits"
              return off - oldOff;
            }
          }
          bits = 0;
        } else {
          nextin -= 6;
        }
      }
      return off - oldOff;
    }

    @Override
    public int available() throws IOException {
      if (closed) {
        throw new IOException("Stream is closed");
      }
      return is.available();   // TBD:
    }

    @Override
    public void close() throws IOException {
      if (!closed) {
        closed = true;
        is.close();
      }
    }
  }
}
