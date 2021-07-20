/**  
* <p>Title: IPUtils.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月5日  
* @version 1.0  
*/  
package cn.flood.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**  
* <p>Title: IPUtils</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月5日  
*/
public class CustomSystemUtils {
	
	private static Logger log =  LoggerFactory.getLogger(CustomSystemUtils.class);
	/**
	 * 内网ip
	 */
	public static String INTRANET_IP = getIntranetIp();
	/**
	 * 外网ip
	 */
	public static String INTERNET_IP = getInternetIp();
	
	/**
	 * 
	 * <p>Title: getIntranetIp</p>  
	 * <p>Description: 内网ip</p>  
	 * @return
	 */
	private static String getIntranetIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 
	 * <p>Title: getInternetIp</p>  
	 * <p>Description: 外网IP</p>  
	 * @return
	 */
	private static String getInternetIp() {
		try {
			Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
			InetAddress ip;
			Enumeration<InetAddress> addrs;
			while (networks.hasMoreElements()) {
				addrs = networks.nextElement().getInetAddresses();
				while (addrs.hasMoreElements()) {
					ip = addrs.nextElement();
					if (ip != null
							&& ip instanceof Inet4Address
							&& ip.isSiteLocalAddress()
							&& !ip.getHostAddress().equals(INTRANET_IP)) {
						return ip.getHostAddress();
					}
				}
			}
			// 如果没有外网IP，就返回内网IP
			return INTRANET_IP;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
