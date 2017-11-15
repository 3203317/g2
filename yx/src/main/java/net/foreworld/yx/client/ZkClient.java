package net.foreworld.yx.client;

import java.io.IOException;

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

import net.foreworld.util.Client;

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

	@Value("${server.id}")
	private String server_id;

	@Value("${server.host}")
	private String server_host;

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

	@Override
	public void start() {

		if (null != zk)
			return;

		try {
			zk = new ZooKeeper(zk_host, zk_sessionTimeout, this);

			zk.create("/front/" + server_id, server_host.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("", event.getState());

		if (event.getState() == KeeperState.SyncConnected) {
		}
	}

}
