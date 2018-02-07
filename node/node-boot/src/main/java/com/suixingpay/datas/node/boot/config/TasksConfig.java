/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月19日 10:14
 * @Copyright ©2017 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.suixingpay.datas.node.boot.config;

import com.suixingpay.datas.common.config.TaskConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月19日 10:14
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2017年12月19日 10:14
 */
@ConfigurationProperties(prefix = "node")
@Component
public class TasksConfig {
    private List<TaskConfig> task;
    public TasksConfig() {
        task = new ArrayList<>();
    }

    public List<TaskConfig> getTask() {
        return task;
    }

    public void setTask(List<TaskConfig> task) {
        this.task = task;
    }
}