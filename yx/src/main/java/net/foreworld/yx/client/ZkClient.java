package net.foreworld.yx.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.codec.CharEncoding;
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
	 * @throws InterruptedException
	 * @throws KeeperException
	 * @throws UnsupportedEncodingException
	 */
	private void register() throws KeeperException, InterruptedException, UnsupportedEncodingException {
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
	 * @throws UnsupportedEncodingException
	 */
	private void listenerService() throws KeeperException, InterruptedException, UnsupportedEncodingException {
		List<String> list = zk.getChildren(zk_rootPath + "/service", watcher, stat);

		logger.info("service count: {}", stat.getNumChildren());

		for (int i = 0; i < list.size(); i++) {
			String serv_name = list.get(i);
			String serv_val = new String(zk.getData(zk_rootPath + "/service/" + serv_name, false, null),
					CharEncoding.UTF_8);
			logger.info("service name: {}, value: {}", serv_name, serv_val);

			String[] _keys = serv_name.split(":");

			switch (_keys.length) {
			case 2:
				MethodUtil.getDefault().put(_keys[0], Constants.MQ);
				break;
			case 3:
				MethodUtil.getDefault().put(_keys[1] + ":" + _keys[2], Constants.MQ);
				break;
			case 4:
				if (server_id.equals(_keys[3]))
					MethodUtil.getDefault().put(_keys[1], serv_val);
				break;
			case 5:
				if (server_id.equals(_keys[4]))
					MethodUtil.getDefault().put(_keys[2] + ":" + _keys[3], serv_val);
				break;
			}
		}
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
					} catch (KeeperException | InterruptedException | UnsupportedEncodingException e) {
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
		System.err.println(":方法:后置机:前置机".split(":").length);
		System.err.println("::方法:后置机:前置机".split(":").length);
	}

}
