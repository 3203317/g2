package net.foreworld.yx.initializer;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import net.foreworld.yx.codec.LoginCodec;
import net.foreworld.yx.codec.OutEncoder;
import net.foreworld.yx.handler.BlacklistHandler;
import net.foreworld.yx.handler.HttpSafeHandler;
import net.foreworld.yx.handler.LoginHandler;
import net.foreworld.yx.handler.LoginTimeoutHandler;
import net.foreworld.yx.handler.TimeoutHandler;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
public class WsInitializer extends ChannelInitializer<NioSocketChannel> {

	@Value("${server.idle.readerIdleTime:3}")
	private int readerIdleTime;

	@Value("${server.idle.writerIdleTime:7}")
	private int writerIdleTime;

	@Value("${server.idle.allIdleTime:10}")
	private int allIdleTime;

	@Resource(name = "loginCodec")
	private LoginCodec loginCodec;

	@Resource(name = "timeoutHandler")
	private TimeoutHandler timeoutHandler;

	@Resource(name = "blacklistHandler")
	private BlacklistHandler blacklistHandler;

	@Resource(name = "loginHandler")
	private LoginHandler loginHandler;

	@Resource(name = "httpSafeHandler")
	private HttpSafeHandler httpSafeHandler;

	@Resource(name = "loginTimeoutHandler")
	private LoginTimeoutHandler loginTimeoutHandler;

	@Resource(name = "outEncoder")
	private OutEncoder outEncoder;

	@Override
	protected void initChannel(NioSocketChannel ch) throws Exception {
		ChannelPipeline pipe = ch.pipeline();

		// pipe.addLast(new LoggingHandler(LogLevel.INFO));

		pipe.addLast(blacklistHandler);

		pipe.addLast("loginTimeout", loginTimeoutHandler);

		pipe.addLast("defaIdleState", new IdleStateHandler(3, writerIdleTime, allIdleTime, TimeUnit.SECONDS));
		pipe.addLast(timeoutHandler);

		pipe.addLast(new HttpServerCodec());
		pipe.addLast(new HttpObjectAggregator(1024 * 64));
		pipe.addLast(new ChunkedWriteHandler());
		pipe.addLast(new HttpContentCompressor());
		pipe.addLast("httpSafe", httpSafeHandler);
		// pipe.addLast(protocolSafeHandler);
		pipe.addLast(new WebSocketServerProtocolHandler("/", null, false));

		pipe.addLast(new WebSocketServerCompressionHandler());

		pipe.addLast(outEncoder);
		pipe.addLast("loginCodec", loginCodec);

		pipe.addLast(loginHandler);

	}

}
