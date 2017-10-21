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
import net.foreworld.yx.codec.BinaryCodec;
import net.foreworld.yx.handler.BlacklistHandler;
import net.foreworld.yx.handler.ExceptionHandler;
import net.foreworld.yx.handler.HeartbeatHandler;
import net.foreworld.yx.handler.LoginHandler;
import net.foreworld.yx.handler.ProtocolSafeHandler;
import net.foreworld.yx.handler.TimeHandler;
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

	@Resource(name = "binaryCodec")
	private BinaryCodec binaryCodec;

	@Resource(name = "timeoutHandler")
	private TimeoutHandler timeoutHandler;

	@Resource(name = "blacklistHandler")
	private BlacklistHandler blacklistHandler;

	@Resource(name = "exceptionHandler")
	private ExceptionHandler exceptionHandler;

	@Resource(name = "protocolSafeHandler")
	private ProtocolSafeHandler protocolSafeHandler;

	@Resource(name = "heartbeatHandler")
	private HeartbeatHandler heartbeatHandler;

	@Resource(name = "loginHandler")
	private LoginHandler loginHandler;

	@Resource(name = "timeHandler")
	private TimeHandler timeHandler;

	@Override
	protected void initChannel(NioSocketChannel ch) throws Exception {
		ChannelPipeline pipe = ch.pipeline();

		// pipe.addLast(new LoggingHandler(LogLevel.INFO));

		pipe.addLast(exceptionHandler);
		pipe.addLast(blacklistHandler);

		pipe.addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime, TimeUnit.SECONDS));
		pipe.addLast(timeoutHandler);

		pipe.addLast(new HttpServerCodec());
		pipe.addLast(new HttpObjectAggregator(1024 * 64));
		pipe.addLast(new ChunkedWriteHandler());
		pipe.addLast(new HttpContentCompressor());
		pipe.addLast(protocolSafeHandler);
		pipe.addLast(new WebSocketServerProtocolHandler("/", null, false));

		pipe.addLast(new WebSocketServerCompressionHandler());

		pipe.addLast(binaryCodec);

		pipe.addLast(loginHandler);
		pipe.addLast(heartbeatHandler);

		pipe.addLast(timeHandler);
	}

}
