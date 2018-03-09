package com.suixingpay.datas.manager.service;

import com.suixingpay.datas.manager.core.entity.AlarmUser;
import com.suixingpay.datas.manager.web.page.Page;

/**
 * 告警用户关联表 服务接口类
 * 
 * @author: FairyHood
 * @date: 2018-03-07 13:40:30
 * @version: V1.0-auto
 * @review: FairyHood/2018-03-07 13:40:30
 */
public interface AlarmUserService {

    Integer insert(AlarmUser alarmUser);

    Integer update(Long id, AlarmUser alarmUser);

    Integer delete(Long id);

    AlarmUser selectById(Long id);

    Page<AlarmUser> page(Page<AlarmUser> page);
}
