package net.foreworld.yx.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.foreworld.util.Client;

/**
 * 
 * @author huangxin <3203317@qq.com>
 *
 */
@Component
public class ZkClient extends Client implements Watcher {

	private CountDownLatch countDownLatch;

	private ZooKeeper zk;

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

	public void connect(String host, int sessionTimeout) throws IOException, InterruptedException {
		if (null == zk) {
			zk = new ZooKeeper(host, sessionTimeout, this);
			countDownLatch.await();
		}
	}

	/**
	 * 
	 * @param path
	 * @param data
	 * @param acl
	 * @param createMode
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void create(String path, byte data[], List<ACL> acl, CreateMode createMode)
			throws KeeperException, InterruptedException {
		zk.create(path, data, acl, createMode);
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("", event.getState());

		if (event.getState() == KeeperState.SyncConnected) {
			countDownLatch.countDown();
		}
	}

	@Override
	public void start() {
		if (null == countDownLatch)
			countDownLatch = new CountDownLatch(1);
	}

}
