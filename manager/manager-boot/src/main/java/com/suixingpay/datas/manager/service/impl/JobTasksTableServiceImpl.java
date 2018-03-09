/**  
 * All rights Reserved, Designed By Suixingpay.
 * @author: FairyHood 
 * @date: 2018-03-07 13:40:30  
 * @Copyright ©2017 Suixingpay. All rights reserved. 
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.suixingpay.datas.manager.service.impl;

import com.suixingpay.datas.manager.core.entity.JobTasksTable;
import com.suixingpay.datas.manager.web.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suixingpay.datas.manager.core.mapper.JobTasksTableMapper;
import com.suixingpay.datas.manager.service.JobTasksTableService;

/**
 * 任务数据表对照关系表 服务实现类
 * 
 * @author: FairyHood
 * @date: 2018-03-07 13:40:30
 * @version: V1.0-auto
 * @review: FairyHood/2018-03-07 13:40:30
 */
@Service
public class JobTasksTableServiceImpl implements JobTasksTableService {

    @Autowired
    private JobTasksTableMapper jobTasksTableMapper;

    @Override
    public Integer insert(JobTasksTable jobTasksTable) {
        return jobTasksTableMapper.insert(jobTasksTable);
    }

    @Override
    public Integer update(Long id, JobTasksTable jobTasksTable) {
        return jobTasksTableMapper.update(id, jobTasksTable);
    }

    @Override
    public Integer delete(Long id) {
        return jobTasksTableMapper.delete(id);
    }

    @Override
    public JobTasksTable selectById(Long id) {
        return jobTasksTableMapper.selectById(id);
    }

    @Override
    public Page<JobTasksTable> page(Page<JobTasksTable> page) {
        Integer total = jobTasksTableMapper.pageAll(1);
        if(total>0) {
            page.setTotalItems(total);
            page.setResult(jobTasksTableMapper.page(page, 1));
        }
        return page;
    }
}
