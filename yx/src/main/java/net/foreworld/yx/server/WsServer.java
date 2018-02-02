package net.foreworld.yx.server;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.foreworld.util.Server;
import net.foreworld.yx.client.ZkClient;
import net.foreworld.yx.initializer.WsInitializer;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
public class WsServer extends Server {

	@Value("${server.port:1234}")
	private int port;

	@Value("${server.bossThread:2}")
	private int bossThread;

	@Value("${server.workerThread:8}")
	private int workerThread;

	@Value("${server.so.backlog:1024}")
	private int so_backlog;

	@Value("${server.id}")
	private String server_id;

	@Resource(name = "wsInitializer")
	private WsInitializer wsInitializer;

	@Resource(name = "zkClient")
	private ZkClient zkClient;

	private static final Logger logger = LoggerFactory.getLogger(WsServer.class);

	private ChannelFuture f;
	private EventLoopGroup bossGroup, workerGroup;

	@Override
	public void start() {
		bossGroup = new NioEventLoopGroup(bossThread);
		workerGroup = new NioEventLoopGroup(workerThread);

		ServerBootstrap b = new ServerBootstrap();

		b.localAddress(port);
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);

		b.option(ChannelOption.SO_BACKLOG, so_backlog);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.option(ChannelOption.TCP_NODELAY, true);

		// b.option(ChannelOption.SO_SNDBUF, 1024 * 32);
		// b.option(ChannelOption.SO_RCVBUF, 1024 * 32);

		b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

		// b.handler(new LoggingHandler(LogLevel.INFO));

		b.childHandler(wsInitializer);

		try {
			f = b.bind().sync();
			if (f.isSuccess()) {
				logger.info("ws start: {}", port);
				afterStart();
			}
		} catch (Exception e) {
			logger.error("", e);
			System.exit(1);
		} finally {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					beforeShut();
					shutdown();
				}
			});
		}
	}

	@Override
	public void shutdown() {
		if (null != f) {
			f.channel().close().syncUninterruptibly();
		}
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
		}
		if (null != workerGroup) {
			workerGroup.shutdownGracefully();
		}
	}

	private void afterStart() throws IOException, InterruptedException, KeeperException {
		zkClient.start();
	}

	private void beforeShut() {
		if (null != zkClient)
			zkClient.shutdown();
	}

}
