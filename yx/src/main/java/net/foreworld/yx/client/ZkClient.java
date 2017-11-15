package net.foreworld.yx.client;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
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
public class ZkClient extends Client {

	@Value("${zk.host}")
	private String zk_host;

	@Value("${zk.sessionTimeout}")
	private int zk_sessionTimeout;

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
	}

	@Override
	public void start() {
		Watcher watcher = new Watcher() {
			public void process(WatchedEvent event) {
				System.out.println("receive event：" + event);
			}
		};

		try {
			final ZooKeeper zookeeper = new ZooKeeper(zk_host, zk_sessionTimeout, watcher);
			final byte[] data = zookeeper.getData("/node_1", watcher, null);
			String value = new String(data);
			System.err.println(value);
			zookeeper.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
