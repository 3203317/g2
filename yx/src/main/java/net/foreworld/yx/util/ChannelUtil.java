package net.foreworld.yx.util;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.foreworld.yx.model.ChannelInfo;

/**
 *
 * @author huangxin
 *
 */
public final class ChannelUtil {

	// 定义一个静态私有变量
	// 不初始化
	// 不使用final关键字
	// 使用volatile保证了多线程访问时instance变量的可见性
	// 避免了instance初始化时其他变量属性还没赋值完时
	// 被另外线程调用
	private static volatile ChannelUtil instance;

	private ChannelUtil() {
		map = new ConcurrentHashMap<String, ChannelInfo>();
		all = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}

	private ConcurrentMap<String, ChannelInfo> map;

	private ChannelGroup all;

	/**
	 * 定义一个共有的静态方法
	 *
	 * @return 该类型实例
	 */
	public static ChannelUtil getDefault() {
		// 对象实例化时与否判断
		// 不使用同步代码块
		// instance不等于null时
		// 直接返回对象
		// 提高运行效率
		if (null == instance) {
			// 同步代码块
			// 对象未初始化时
			// 使用同步代码块
			// 保证多线程访问时对象在第一次创建后
			// 不再重复被创建
			synchronized (ChannelUtil.class) {
				// 未初始化
				// 则初始instance变量
				if (null == instance) {
					instance = new ChannelUtil();
				}
			}
		}
		return instance;
	}

	/**
	 *
	 * @param id
	 * @param channelInfo
	 */
	public void putChannel(String id, ChannelInfo channelInfo) {
		all.add(channelInfo.getChannel());
		map.put(id, channelInfo);
	}

	public void removeChannel(String id) {
		ChannelInfo ci = this.getChannel(id);

		if (null != ci) {
			all.remove(ci.getChannel());
			map.remove(id);
		}
	}

	public void removeChannel(Channel chan, String chan_id) {
		all.remove(chan);
		map.remove(chan_id);
	}

	public ChannelInfo getChannel(String id) {
		return map.get(id);
	}

	public ConcurrentMap<String, ChannelInfo> getChannels() {
		return map;
	}

	public ChannelGroupFuture broadcast(Object message) {
		return all.writeAndFlush(message);
	}

	public ChannelGroupFuture broadcast(Object message, ChannelMatcher matcher) {
		return all.writeAndFlush(message, matcher);
	}

	public ChannelGroup flush() {
		return all.flush();
	}

	public ChannelGroupFuture close() {
		return all.close();
	}

	public int size() {
		return all.size();
	}
}
