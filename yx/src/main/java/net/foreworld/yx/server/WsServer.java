package net.foreworld.yx.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.foreworld.util.Server;
import net.foreworld.yx.initializer.WsInitializer;
import net.foreworld.yx.util.ChannelUtil;
import net.foreworld.yx.util.Constants;
import net.foreworld.yx.util.RedisUtil;
import redis.clients.jedis.Jedis;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@PropertySource("classpath:activemq.properties")
@PropertySource("classpath:redis.properties")
@Component
public class WsServer extends Server {

	@Value("${sha.server.open}")
	private String sha_server_open;

	@Value("${sha.server.close}")
	private String sha_server_close;

	@Value("${db.redis.database:1}")
	private String db_redis_database;

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

	@Value("${server.host}")
	private String server_host;

	@Value("${queue.front.start}")
	private String queue_front_start;

	@Value("${queue.front.stop}")
	private String queue_front_stop;

	@Resource(name = "wsInitializer")
	private WsInitializer wsInitializer;

	@Resource(name = "jmsMessagingTemplate")
	private JmsMessagingTemplate jmsMessagingTemplate;

	private static final Logger logger = LoggerFactory.getLogger(WsServer.class);

	private ChannelFuture f;
	private EventLoopGroup bossGroup, workerGroup;

	@Override
	public void start() {

		if (!beforeStart())
			return;

		bossGroup = new NioEventLoopGroup(bossThread);
		workerGroup = new NioEventLoopGroup(workerThread);

		ServerBootstrap b = new ServerBootstrap();

		b.localAddress(port);
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);

		b.option(ChannelOption.SO_BACKLOG, so_backlog);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.option(ChannelOption.TCP_NODELAY, true);

		// b.handler(new LoggingHandler(LogLevel.INFO));

		b.childHandler(wsInitializer);

		try {
			f = b.bind().sync();
			if (f.isSuccess()) {
				logger.info("ws start {}", port);
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
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

	private void beforeShut() {
		ChannelUtil.getDefault().close();

		List<String> s = new ArrayList<String>();
		s.add(db_redis_database);
		s.add(server_id);

		List<String> b = new ArrayList<String>();

		Jedis j = RedisUtil.getDefault().getJedis();

		if (null == j)
			return;

		j.evalsha(sha_server_close, s, b);
		j.close();
	}

	private boolean beforeStart() {
		List<String> s = new ArrayList<String>();
		s.add(db_redis_database);
		s.add(server_id);

		List<String> b = new ArrayList<String>();
		b.add(String.valueOf(System.currentTimeMillis()));
		b.add(server_host);

		Jedis j = RedisUtil.getDefault().getJedis();

		if (null == j)
			return false;

		Object o = j.evalsha(sha_server_open, s, b);
		j.close();

		return Constants.OK.equals(o.toString());
	}

}
