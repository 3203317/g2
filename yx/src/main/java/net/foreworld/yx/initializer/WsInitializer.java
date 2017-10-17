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
import net.foreworld.yx.handler.HeartbeatV2Handler;
import net.foreworld.yx.handler.LoginV2Handler;
import net.foreworld.yx.handler.ProtocolSafeV2Handler;
import net.foreworld.yx.handler.TimeV2Handler;
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

	@Resource(name = "protocolSafeV2Handler")
	private ProtocolSafeV2Handler protocolSafeV2Handler;

	@Resource(name = "heartbeatV2Handler")
	private HeartbeatV2Handler heartbeatV2Handler;

	@Resource(name = "loginV2Handler")
	private LoginV2Handler loginV2Handler;

	@Resource(name = "timeV2Handler")
	private TimeV2Handler timeV2Handler;

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
		pipe.addLast(protocolSafeV2Handler);
		pipe.addLast(new WebSocketServerProtocolHandler("/", null, false));

		pipe.addLast(new WebSocketServerCompressionHandler());

		pipe.addLast(binaryCodec);

		pipe.addLast(loginV2Handler);
		pipe.addLast(heartbeatV2Handler);

		pipe.addLast(timeV2Handler);
	}

}
