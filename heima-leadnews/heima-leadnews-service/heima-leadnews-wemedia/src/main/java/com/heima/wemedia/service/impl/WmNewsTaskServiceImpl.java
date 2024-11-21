package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.schedule.IScheduleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.TaskTypeEnum;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.ProtostuffUtil;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * WmNewsTaskServiceImpl 类实现 WmNewsTaskService 接口
 * 负责将文章审核任务添加到延迟任务队列中。
 */
@Service
@Slf4j
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Autowired
    private IScheduleClient scheduleClient; // 调用调度服务客户端接口，用于添加任务到延迟队列

    /**
     * 将文章审核任务添加到延迟队列中，以便在指定时间执行审核操作。
     *
     * @param id          文章的 ID
     * @param publishTime 文章的发布时间，作为任务的执行时间
     */
    @Override
    @Async
    public void addNewsToTask(Integer id, Date publishTime) {

        log.info("添加任务到延迟服务中----begin");

        // 创建 Task 对象，封装任务的相关信息
        Task task = new Task();
        task.setExecuteTime(publishTime.getTime()); // 设置任务的执行时间
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType()); // 设置任务类型为文章定时审核
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority()); // 设置任务优先级

        // 创建 WmNews 对象，设置文章 ID
        WmNews wmNews = new WmNews();
        wmNews.setId(id);

        // 序列化 WmNews 对象并设置为任务参数
        task.setParameters(ProtostuffUtil.serialize(wmNews));

        // 调用调度服务客户端，添加任务到延迟队列
        scheduleClient.addTask(task);

        log.info("添加任务到延迟服务中----end");
    }

    // 注入WmNewsAutoScanService服务
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    // 定时任务方法，每隔一秒执行一次
    @Scheduled(fixedRate = 1000)
    @Override
    public void scanNewByTask() {
        log.info("自动审核消费延迟服务中----end");
        // 从调度客户端拉取一个新闻扫描任务
        ResponseResult poll = scheduleClient.poll(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());

        // 判断任务是否成功拉取且数据不为空
        if (poll.getCode().equals(200) && poll.getData() != null){
            // 将任务数据转换为Task对象
            Task task = JSON.parseObject(JSON.toJSONString(poll.getData()), Task.class);

            // 反序列化任务参数，将其转换为WmNews对象
            WmNews deserialize = ProtostuffUtil.deserialize(task.getParameters(), WmNews.class);

            // 调用自媒体文章审核服务进行文章审核
            wmNewsAutoScanService.autoScanWmNews(deserialize.getId());
        }
    }

}
