package net.foreworld.yx.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import net.foreworld.util.Client;
import net.foreworld.yx.util.Constants;
import net.foreworld.yx.util.MethodUtil;

/**
 *
 * @author huangxin <3203317@qq.com>
 *
 */
@PropertySource("classpath:zk.properties")
@Component
public class ZkClient extends Client implements Watcher {

	@Value("${zk.host}")
	private String zk_host;

	@Value("${zk.sessionTimeout}")
	private int zk_sessionTimeout;

	@Value("${zk.rootPath}")
	private String zk_rootPath;

	private CountDownLatch countDownLatch;

	private ZooKeeper zk;

	@Value("${server.id}")
	private String server_id;

	private static final Logger logger = LoggerFactory.getLogger(ZkClient.class);

	@Override
	public void shutdown() {
		if (null != zk)
			try {
				zk.close();
			} catch (InterruptedException e) {
				logger.error("", e);
			}
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("{}", event.getState());

		if (event.getState() == KeeperState.SyncConnected) {
			countDownLatch.countDown();
		}
	}

	/**
	 * 向front下注册节点
	 *
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void register() throws KeeperException, InterruptedException {
		registerServer();
		listenerService();
	}

	/**
	 *
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void registerServer() throws KeeperException, InterruptedException {
		zk.create(zk_rootPath + "/front/" + server_id, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}

	private Watcher watcher;

	private Stat stat;

	/**
	 * 监听服务注入
	 *
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void listenerService() throws KeeperException, InterruptedException {
		List<String> list = zk.getChildren(zk_rootPath + "/service", watcher, stat);

		logger.info("service count: {}", stat.getNumChildren());

		for (int i = 0; i < list.size(); i++) {
			String serv_name = list.get(i);

			String[] _keys = serv_name.split(":");

			String _method = null, chan_id = null;

			switch (_keys.length) {
			case 2:
				_method = _keys[0];
				chan_id = Constants.MQ + _keys[1];
				break;
			case 3:
				_method = _keys[1] + ":" + _keys[2];
				chan_id = Constants.MQ + _keys[2];
				break;
			case 4:
				if (!server_id.equals(_keys[2]))
					continue;

				_method = _keys[0];
				chan_id = _keys[3];
				break;
			case 5:
				if (!server_id.equals(_keys[3]))
					continue;

				_method = _keys[1] + ":" + _keys[2];
				chan_id = _keys[4];
				break;
			default:
				continue;
			}

			if (MethodUtil.getDefault().contains(_method, chan_id))
				continue;

			MethodUtil.getDefault().put(_method, chan_id);

			listener(serv_name, _method, chan_id);

			logger.info("method name: {}, value: {}", _method, chan_id);
		}
	}

	/**
	 *
	 * @param serv_name
	 * @param method
	 * @param chan_id
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void listener(final String serv_name, final String method, final String chan_id)
			throws KeeperException, InterruptedException {
		zk.exists(zk_rootPath + "/service/" + serv_name, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				MethodUtil.getDefault().remove(method, chan_id);
			}
		});
	}

	private void init() throws IOException, InterruptedException {
		if (null != zk)
			return;

		if (null == countDownLatch)
			countDownLatch = new CountDownLatch(1);

		zk = new ZooKeeper(zk_host, zk_sessionTimeout, this);
		countDownLatch.await();

		if (null == stat)
			stat = new Stat();

		if (null == watcher)
			watcher = new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					try {
						listenerService();
					} catch (KeeperException | InterruptedException e) {
						logger.error("", e);
					}
				}
			};
	}

	@Override
	public void start() throws IOException, InterruptedException, KeeperException {
		init();
		register();
	}

	public static void main(String[] args) {
		System.err.println("方法:后置机".split(":").length);
		System.err.println(":方法:后置机".split(":").length);
		System.err.println("方法:后置机:68:通道号".split(":").length);
		System.err.println(":方法:后置机:68:通道号".split(":").length);
	}

}
