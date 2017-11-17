package net.foreworld.yx;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import net.foreworld.yx.server.WsServer;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@SpringBootApplication
@ComponentScan("net.foreworld")
public class RunWsServer implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(RunWsServer.class);

	@Resource(name = "wsServer")
	private WsServer wsServer;

	public static void main(String[] args) {
		SpringApplication.run(RunWsServer.class, args);
	}

	public void run(String... strings) throws InterruptedException {
		wsServer.start();
		Thread.currentThread().join();
	}

}
