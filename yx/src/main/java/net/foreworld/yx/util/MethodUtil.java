package net.foreworld.yx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
public final class MethodUtil {

	private Random random;

	// 定义一个静态私有变量
	// 不初始化
	// 不使用final关键字
	// 使用volatile保证了多线程访问时instance变量的可见性
	// 避免了instance初始化时其他变量属性还没赋值完时
	// 被另外线程调用
	private static volatile MethodUtil instance;

	private MethodUtil() {
		map = new ConcurrentHashMap<String, List<String>>();
		random = new Random();
	}

	private Map<String, List<String>> map;

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
		List<String> l = map.get(id);

		if (null == l) {
			l = new ArrayList<String>();
			l.add(chan_id);
			map.put(id, l);
			return;
		}

		if (!l.contains(chan_id))
			l.add(chan_id);
	}

	public void remove(String id) {
		map.remove(id);
	}

	public void remove(String id, String chan_id) {
		List<String> l = map.get(id);

		if (null == l)
			return;

		l.remove(chan_id);
	}

	public String get(String id) {
		List<String> l = map.get(id);

		if (null == l)
			return null;

		int _len = l.size();

		if (1 > _len)
			return null;

		if (1 == _len)
			return l.get(0);

		return l.get(random.nextInt(_len));
	}

	/**
	 *
	 * @param id
	 * @param chan_id
	 * @return
	 */
	public boolean contains(String id, String chan_id) {
		List<String> l = map.get(id);

		if (null == l)
			return false;

		return l.contains(chan_id);
	}

	public static void main(String[] args) {
		List<String> sl = new ArrayList<String>();

		sl.add("1");
		sl.add("2");
		sl.add("abc");

		System.err.println(sl.contains("abc"));

		sl.remove("1");

		System.err.println(sl);

		Random random = new Random();
		System.err.println(random.nextInt(2));
	}
}
