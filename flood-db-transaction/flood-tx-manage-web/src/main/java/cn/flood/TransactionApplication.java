package cn.flood; /**
* <p>Title: TransactionApplication.java</p>
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年3月21日  
* @version 1.0  
*/

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;


/**  
* <p>Title: TransactionApplication</p>
* <p>Description: 分布式事务管理启动项</p>  
* @author mmdai  
* @date 2019年3月21日  
*/
@EnableTransactionManagerServer
@SpringBootApplication
public class TransactionApplication {
	
	public static void main(String[] args) {
    	new SpringApplicationBuilder(TransactionApplication.class).banner(new TransactionBanner()).run(args);
    }

}
