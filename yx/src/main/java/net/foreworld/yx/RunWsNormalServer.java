package net.foreworld.yx;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import net.foreworld.yx.server.WsNormalServer;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@SpringBootApplication
@ComponentScan("net.foreworld")
public class RunWsNormalServer implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(RunWsNormalServer.class);

	@Resource(name = "wsNormalServer")
	private WsNormalServer wsNormalServer;

	public static void main(String[] args) {
		SpringApplication.run(RunWsNormalServer.class, args);
	}

	public void run(String... strings) throws Exception {
		try {
			wsNormalServer.start();
			Thread.currentThread().join();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
