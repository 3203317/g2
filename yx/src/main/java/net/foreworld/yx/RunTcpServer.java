package net.foreworld.yx;
//package net.foreworld.gws;
//
//import javax.annotation.Resource;
//
//import net.foreworld.gws.server.TcpServer;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.ComponentScan;
//
///**
// *
// * @author huangxin <3203317@qq.com>
// *
// */
//@SpringBootApplication
//@ComponentScan("net.foreworld.gws")
//public class RunTcpServer implements CommandLineRunner {
//
//	private static final Logger logger = LoggerFactory
//			.getLogger(RunTcpServer.class);
//
//	@Resource(name = "tcpServer")
//	private TcpServer tcpServer;
//
//	public static void main(String[] args) {
//		SpringApplication.run(RunTcpServer.class, args);
//	}
//
//	public void run(String... strings) throws Exception {
//		try {
//			tcpServer.start();
//			Thread.currentThread().join();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//	}
//
// }
