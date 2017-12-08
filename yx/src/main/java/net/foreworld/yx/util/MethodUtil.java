package net.foreworld.yx.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
public final class MethodUtil {

	// 定义一个静态私有变量
	// 不初始化
	// 不使用final关键字
	// 使用volatile保证了多线程访问时instance变量的可见性
	// 避免了instance初始化时其他变量属性还没赋值完时
	// 被另外线程调用
	private static volatile MethodUtil instance;

	private MethodUtil() {
		map = new ConcurrentHashMap<String, String>();
	}

	private Map<String, String> map;

	/**
	 * 定义一个共有的静态方法
	 *
	 * @return 该类型实例
	 */
	public static MethodUtil getDefault() {
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
			synchronized (MethodUtil.class) {
				// 未初始化
				// 则初始instance变量
				if (null == instance) {
					instance = new MethodUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 
	 * @param id
	 * @param chan_id
	 */
	public void put(String id, String chan_id) {
		map.put(id, chan_id);
	}

	public void remove(String id) {
		map.remove(id);
	}

	public String get(String id) {
		return map.get(id);
	}

	public static void main(String[] args) {
		MethodUtil.getDefault().put("b", "c");
		MethodUtil.getDefault().put("a", "");

		System.err.println(MethodUtil.getDefault().get("b"));
		System.err.println(MethodUtil.getDefault().get("a"));
		System.err.println(null == MethodUtil.getDefault().get("bb"));

		MethodUtil.getDefault().put("a", "d");
		System.err.println(MethodUtil.getDefault().get("a"));
	}
}
