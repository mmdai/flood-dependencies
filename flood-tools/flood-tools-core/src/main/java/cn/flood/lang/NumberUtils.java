/**
 * Copyright (c) 2018-2028,
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.flood.lang;


import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * 数字类型工具类
 *
 * @author mmdai
 */
public class NumberUtils extends org.springframework.util.NumberUtils {


	private static final int DEFAULT_SCALE = 2;

	private NumberUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 将科学计数法转为字符串
	 *
	 * @param val 以科学计数法形式的数字
	 * @return 字符串
	 */
	public static String avoidScientificNotation(double val) {
		return avoidScientificNotation(String.valueOf(val));
	}


	/**
	 * 将科学计数法转为字符串
	 *
	 * @param val 以科学计数法形式的数字
	 * @return 字符串
	 */
	public static String avoidScientificNotation(String val) {
		return isEmpty(val) ? StringUtils.EMPTY_STRING : (val.matches("^\\d(.\\d+)?[eE](\\d+)$") ? new BigDecimal(val).toPlainString() : val);
	}


	/**
	 * 判断字符串是否为纯数字组成
	 *
	 * @param str 数值字符串
	 * @return {@link Boolean}
	 * @see #isNumeric(String) 带有小数点的判断请调用该方法
	 */
	public static boolean isDigital(String str) {
		if (isEmpty(str)) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否是数值,如果是则返回true,否则返回false
	 * <p>
	 * 支持类似'1','-1','0.01','.01'等格式的数值字符串,但是不支持进制数字字符串
	 * </p>
	 *
	 * @param str 数值字符串
	 * @return {@linkplain Boolean}
	 */
	public static boolean isNumeric(String str) {
		if (StringUtils.hasText(str)) {
			String regex = "^[-+]?(\\d+)?(\\.\\d+)?$";
			Pattern pattern = Pattern.compile(regex);
			return pattern.matcher(str).find();
		}
		return false;
	}

	/**
	 * 人民币金额转为分
	 *
	 * @param yuan 金额,单位为元
	 * @return 金额分
	 */
	public static long yuan2Fen(long yuan) {
		return yuan * 100;
	}

	/**
	 * 人民币金额转为分,默认保留2位小数,超过则四舍五入.
	 *
	 * @param yuan 金额,单位为元
	 * @return 金额分
	 */
	public static long yuan2Fen(double yuan) {
		return yuan2Fen(Double.toString(yuan));
	}

	/**
	 * 人民币金额转为分,默认保留2位小数,超过则四舍五入.
	 *
	 * @param yuan 金额,单位为元
	 * @return 金额分
	 */
	public static long yuan2Fen(String yuan) {
		return yuan2Fen(yuan, true);
	}

	/**
	 * 人民币金额转为分,保留小数位位数
	 *
	 * @param yuan  金额,单位为元
	 * @param round 如果值{@code true}则超出部分四舍五入，否则直接忽略
	 * @return 金额分
	 */
	public static long yuan2Fen(double yuan, boolean round) {
		return yuan2Fen(Double.toString(yuan), round);
	}

	/**
	 * 人民币金额转为分,保留小数位位数.
	 *
	 * @param yuan  金额,单位为元
	 * @param round 如果值{@code true}则超出部分四舍五入，否则直接忽略
	 * @return 金额分
	 */
	public static long yuan2Fen(String yuan, boolean round) {
		BigDecimal multiplier = new BigDecimal(String.valueOf(yuan)), multiplicand = new BigDecimal(100);
		if (round) {
			multiplier = multiplier.setScale(DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP);
		} else {
			multiplier = multiplier.setScale(DEFAULT_SCALE, BigDecimal.ROUND_DOWN);
		}
		BigDecimal result = multiplier.multiply(multiplicand);
		return result.longValue();
	}

	/**
	 * 将人民币金额(字符串)转成分(结果为仍为字符串)
	 *
	 * @param yuan 金额，单位为元
	 * @return 金额，单位为分
	 */
	public static String yuan2FenString(String yuan) {
		return String.valueOf(yuan2Fen(yuan, true));
	}

	/**
	 * 人民币金额分转元，保留2位小数
	 *
	 * @param fen 金额，单位分
	 * @return 金额元
	 */
	public static double fen2Yuan(long fen) {
		return fen2Yuan(Long.toString(fen));
	}

	/**
	 * 人民币金额分转元，保留2位小数
	 *
	 * @param fen 金额，单位分
	 * @return 金额元
	 */
	public static double fen2Yuan(String fen) {
		BigDecimal dividend = new BigDecimal(fen), divisor = new BigDecimal(100);
		BigDecimal result = dividend.divide(divisor, 2, BigDecimal.ROUND_HALF_UP);
		return result.doubleValue();
	}

	//-----------------------------------------------------------------------

	/**
	 * <p>Convert a <code>String</code> to an <code>int</code>, returning
	 * <code>zero</code> if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
	 *
	 * <pre>
	 *   NumberUtils.toInt(null) = 0
	 *   NumberUtils.toInt("")   = 0
	 *   NumberUtils.toInt("1")  = 1
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the int represented by the string, or <code>zero</code> if
	 * conversion fails
	 */
	public static int toInt(final String str) {
		return toInt(str, -1);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>int</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   NumberUtils.toInt(null, 1) = 1
	 *   NumberUtils.toInt("", 1)   = 1
	 *   NumberUtils.toInt("1", 0)  = 1
	 * </pre>
	 *
	 * @param str          the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static int toInt(@Nullable final String str, final int defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return Integer.valueOf(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>long</code>, returning
	 * <code>zero</code> if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
	 *
	 * <pre>
	 *   NumberUtils.toLong(null) = 0L
	 *   NumberUtils.toLong("")   = 0L
	 *   NumberUtils.toLong("1")  = 1L
	 * </pre>
	 *
	 * @param str the string to convert, may be null
	 * @return the long represented by the string, or <code>0</code> if
	 * conversion fails
	 */
	public static long toLong(final String str) {
		return toLong(str, 0L);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>long</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   NumberUtils.toLong(null, 1L) = 1L
	 *   NumberUtils.toLong("", 1L)   = 1L
	 *   NumberUtils.toLong("1", 0L)  = 1L
	 * </pre>
	 *
	 * @param str          the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the long represented by the string, or the default if conversion fails
	 */
	public static long toLong(@Nullable final String str, final long defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return Long.valueOf(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>Double</code>
	 *
	 * @param value value
	 * @return double value
	 */
	public static Double toDouble(String value) {
		return toDouble(value, null);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>Double</code>
	 *
	 * @param value value
	 * @param defaultValue 默认值
	 * @return double value
	 */
	public static Double toDouble(@Nullable String value, Double defaultValue) {
		if (value != null) {
			return Double.valueOf(value.trim());
		}
		return defaultValue;
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>Double</code>
	 *
	 * @param value value
	 * @return double value
	 */
	public static Float toFloat(String value) {
		return toFloat(value, null);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>Double</code>
	 *
	 * @param value value
	 * @param defaultValue 默认值
	 * @return double value
	 */
	public static Float toFloat(@Nullable String value, Float defaultValue) {
		if (value != null) {
			return Float.valueOf(value.trim());
		}
		return defaultValue;
	}

	/**
	 * All possible chars for representing a number as a String
	 */
	private final static char[] DIGITS = {
		'0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'a', 'b',
		'c', 'd', 'e', 'f', 'g', 'h',
		'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't',
		'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F',
		'G', 'H', 'I', 'J', 'K', 'L',
		'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z'
	};

	/**
	 * 将 long 转短字符串 为 62 进制
	 *
	 * @param i 数字
	 * @return 短字符串
	 */
	public static String to62String(long i) {
		int radix = DIGITS.length;
		char[] buf = new char[65];
		int charPos = 64;
		i = -i;
		while (i <= -radix) {
			buf[charPos--] = DIGITS[(int) (-(i % radix))];
			i = i / radix;
		}
		buf[charPos] = DIGITS[(int) (-i)];

		return new String(buf, charPos, (65 - charPos));
	}

}
