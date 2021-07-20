package cn.flood.job.admin;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author xuxueli 2018-10-28 00:38:13
 */
@SpringBootApplication
public class XxlJobAdminApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder(XxlJobAdminApplication.class).banner(new XxlJobBanner()).run(args);

	}

}