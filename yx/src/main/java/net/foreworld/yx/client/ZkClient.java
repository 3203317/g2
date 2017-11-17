package net.foreworld.yx.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import net.foreworld.util.Client;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

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

	private CountDownLatch countDownLatch;

	private ZooKeeper zk;

	@Value("${zk.rootPath}")
	private String zk_rootPath;

	@Value("${server.id}")
	private String server_id;

	@Value("${server.host}")
	private String server_host;

	private static final Logger logger = LoggerFactory
			.getLogger(ZkClient.class);

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

	public void register() throws KeeperException, InterruptedException {
		zk.create(zk_rootPath + "/front/" + server_id, server_host.getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	}

	@Override
	public void start() {
		if (null != zk)
			return;

		if (null == countDownLatch)
			countDownLatch = new CountDownLatch(1);

		try {
			zk = new ZooKeeper(zk_host, zk_sessionTimeout, this);
			countDownLatch.await();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
