/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月28日 13:38
 * @Copyright ©2017 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */

package com.suixingpay.datas.node.task.transform.transformer;

import com.alibaba.fastjson.JSON;
import com.suixingpay.datas.common.db.meta.TableColumn;
import com.suixingpay.datas.common.db.meta.TableSchema;
import com.suixingpay.datas.common.statistics.TaskLog;
import com.suixingpay.datas.node.core.event.etl.ETLBucket;
import com.suixingpay.datas.node.core.event.etl.ETLColumn;
import com.suixingpay.datas.node.core.event.etl.ETLRow;
import com.suixingpay.datas.node.core.event.s.EventType;
import com.suixingpay.datas.node.core.loader.DataLoader;
import com.suixingpay.datas.node.core.task.TableMapper;
import com.suixingpay.datas.node.task.worker.TaskWork;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月28日 13:38
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2017年12月28日 13:38
 */
public class ETLRowTransformer implements Transformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ETLRowTransformer.class);

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void transform(ETLBucket bucket, TaskWork work) {
        LOGGER.debug("start tranforming bucket:{},size:{}", bucket.getSequence(), bucket.getRows().size());
        for (ETLRow row : bucket.getRows()) {
            LOGGER.debug("try tranform row:{},{}", row.getIndex(), JSON.toJSONString(row));
            TableMapper tableMapper = work.getTableMapper(row.getSchema(), row.getTable());
            mappingRowData(tableMapper, row);
            TableSchema table = findTable(work.getDataLoader(), row.getFinalSchema(), row.getFinalTable(), false, work);
            if (null != table) remedyColumns(table, row);

            //DataLoader自定义处理
            work.getDataLoader().mouldRow(row);

            LOGGER.debug("after tranform row:{},{}", row.getIndex(), JSON.toJSONString(row));
        }
    }

    private void mappingRowData(TableMapper mapper, ETLRow row) {
        if (null != mapper) {
            //替换schema和表名
            if (null != mapper.getSchema() && mapper.getSchema().length == 2) row.setFinalSchema(mapper.getSchema()[1]);
            if (null != mapper.getTable() && mapper.getTable().length == 2) row.setFinalTable(mapper.getTable()[1]);
            if (null != row.getColumns() && null != mapper.getColumn()) {
                for (ETLColumn c : row.getColumns()) {
                    //替换字段名
                    c.setFinalName(mapper.getColumn().getOrDefault(c.getName(), c.getName()));
                }
            }
        }
    }

    private void remedyColumns(TableSchema table, ETLRow row) {
        List<ETLColumn> removeables = new ArrayList<>();
        //正向查找
        for (ETLColumn c : row.getColumns()) {
            TableColumn column = table.findColumn(c.getFinalName());
            if (null != column) {
                c.setFinalType(column.getTypeCode());
                c.setKey(column.isPrimaryKey());
                c.setRequired(column.isRequired());
                //如果是更新并且原主键不存在
                if (row.getOpType() == EventType.UPDATE && column.isRequired() && StringUtils.isBlank(c.getOldValue())) {
                    c.setOldValue(c.getNewValue());
                }
            } else {
                removeables.add(c);
            }
        }

        //反向查找
        List<String> inColumnNames = row.getColumns().stream().map(p  -> p.getFinalName()).collect(Collectors.toList());
        //如果目标库表中有新增必填的字段需要补充上去
        table.getColumns().stream().filter(p -> ! inColumnNames.contains(p.getName()) && p.isRequired() && null == p.getDefaultValue())
                .forEach(p -> {
                    ETLColumn column = new ETLColumn(p.getName(), p.getDefaultValue(), p.getDefaultValue(), p.getDefaultValue(), p.isPrimaryKey(), p.isRequired(), p.getTypeCode());
                    if (row.getOpType() == EventType.INSERT) {
                        row.getColumns().add(column);
                    }
                    //更新失败需要插入时才需要
                    if (row.getOpType() == EventType.UPDATE) {
                        row.getAppendsWhenUInsert().add(column);
                    }
                });

        row.getColumns().removeAll(removeables);
    }


    private TableSchema findTable(DataLoader loader, String finalSchema, String finalTable, boolean cache, TaskWork work) {
        TableSchema table = null;
        try {
            table = loader.findTable(finalSchema, finalTable, cache);
        } catch (Exception e) {
            TaskLog.upload(work.getTaskId(), "查询数据库表结构出错" , e.getMessage(), work.getDataConsumer().getSwimlaneId());
            e.printStackTrace();
            LOGGER.error("查询目标仓库表结构{}.{}出错!", finalSchema, finalTable, e);
        }
        return table;
    }
}