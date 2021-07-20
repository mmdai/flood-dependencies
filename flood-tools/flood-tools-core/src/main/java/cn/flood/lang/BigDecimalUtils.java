/**  
* <p>Title: BigDecimalUtils.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月16日  
* @version 1.0  
*/  
package cn.flood.lang;

import java.math.BigDecimal;

/**  
* <p>Title: BigDecimalUtils</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月16日  
*/
public class BigDecimalUtils {
	
	private BigDecimalUtils() {
        throw new UnsupportedOperationException();
    }
	
	/**
	 * 
	 * <p>Title: add</p>  
	 * <p>Description: +</p>  
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2);
	}
	
	public static BigDecimal add(long v1, long v2) {
		BigDecimal b1 = new BigDecimal(Long.toString(v1));
		BigDecimal b2 = new BigDecimal(Long.toString(v2));
		return b1.add(b2);
	}
	/**
	 * 
	 * <p>Title: sub</p>  
	 * <p>Description: 减</p>  
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2);
	}
	
	public static BigDecimal sub(long v1, long v2) {
		BigDecimal b1 = new BigDecimal(Long.toString(v1));
		BigDecimal b2 = new BigDecimal(Long.toString(v2));
		return b1.subtract(b2);
	}
	/**
	 * 
	 * <p>Title: mul</p>  
	 * <p>Description: x</p>  
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2);
	}
	
	public static BigDecimal mul(long v1, long v2) {
		BigDecimal b1 = new BigDecimal(Long.toString(v1));
		BigDecimal b2 = new BigDecimal(Long.toString(v2));
		return b1.multiply(b2);
	}
	/**
	 * 
	 * <p>Title: div</p>  
	 * <p>Description: ÷</p>  
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal div(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);
	}
	
	public static BigDecimal div(long v1, long v2) {
		BigDecimal b1 = new BigDecimal(Long.toString(v1));
		BigDecimal b2 = new BigDecimal(Long.toString(v2));
		return b1.divide(b2);
	}

}
