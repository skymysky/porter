/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月14日 18:15
 * @Copyright ©2017 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.suixingpay.datas.common.cluster.zookeeper;

import com.suixingpay.datas.common.client.impl.ZookeeperClient;
import com.suixingpay.datas.common.cluster.*;
import com.suixingpay.datas.common.cluster.command.ClusterCommand;
import com.suixingpay.datas.common.config.Config;
import com.suixingpay.datas.common.config.ConfigType;
import com.suixingpay.datas.common.config.source.ZookeeperConfig;
import com.suixingpay.datas.common.task.TaskEventListener;
import com.suixingpay.datas.common.task.TaskEventProvider;

import java.io.IOException;

/**
 * zookeeper集群提供者
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月14日 18:15
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2017年12月14日 18:15
 */
public class ZookeeperClusterProvider extends ClusterProvider{
    private ZookeeperClient client;
    private ZookeeperClusterMonitor zkMonitor;


    @Override
    protected void addListener(ClusterListener listener) {
        listener.setClient(client);
        zkMonitor.addListener(listener);
    }

    @Override
    public void addTaskEventListener(TaskEventListener listener) {
        for (ClusterListener clusterListener : zkMonitor.getListener().values()) {
            if (clusterListener instanceof TaskEventProvider) {
                TaskEventProvider teProvider = (TaskEventProvider) clusterListener;
                teProvider.addTaskEventListener(listener);
            }
        }
    }

    @Override
    public void removeTaskEventListener(TaskEventListener listener) {
        for (ClusterListener clusterListener : zkMonitor.getListener().values()) {
            if (clusterListener instanceof TaskEventProvider) {
                TaskEventProvider teProvider = (TaskEventProvider) clusterListener;
                teProvider.removeTaskEventListener(listener);
            }
        }
    }

    @Override
    public void doInitialize(Config configIn) {
        ZookeeperConfig config = (ZookeeperConfig) configIn;
        client = new ZookeeperClient(config);
        zkMonitor = new ZookeeperClusterMonitor();
        zkMonitor.setClient(client);
    }

    @Override
    protected boolean matches(ConfigType type) {
        return ConfigType.ZOOKEEPER == type;
    }

    @Override
    public void start() throws IOException {
        if( null != client) {
            client.start();
        }
        zkMonitor.start();
    }

    @Override
    public void stop() {
        try {
            zkMonitor.stop();
        } finally {
            if( null != client) try {
                client.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void distributeCommand(ClusterCommand command) throws Exception {
        for (ClusterListener listener : zkMonitor.getListener().values()) {
            listener.hobby(command);
        }
    }
}