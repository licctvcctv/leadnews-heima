package com.heima.schedule.service.impl;

import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskServiceimplTest 类是 TaskService 的测试类，主要用于测试 TaskService 中的 addTask 方法。
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
class TaskServiceimplTest {

    // 自动注入 TaskService，用于调用需要测试的方法
    @Autowired
    private TaskService taskService;

    /**
     * 测试 addTask 方法，验证任务是否能正确添加。
     * 创建一个 Task 对象并设置其属性，包括任务类型、优先级、参数和执行时间。
     * 调用 taskService.addTask() 方法，并打印生成的任务 ID。
     */
    @Test
    void addTask() {
        for (int i = 0; i < 30; i++) {
            Task task = new Task();
            task.setTaskType(100); // 设置任务类型
            task.setPriority(50); // 设置任务优先级
            task.setParameters("task test".getBytes()); // 设置任务参数，转换为字节数组
            task.setExecuteTime(new Date().getTime() +500 * i ); // 设置任务执行时间为当前时间加5秒
            // 调用 addTask 方法，将任务添加到任务列表，并返回任务 ID
            long taskId = taskService.addTask(task);

            // 打印生成的任务 ID，以验证任务是否被成功添加
            System.out.println(taskId);

        }
        // 创建任务对象并设置属性

    }

    @Test
    void cancal(){
        boolean b = taskService.cancelTask(1854111477129375745L);
        System.out.println(b);
    }

    @Test
    void poll(){
        Task poll = taskService.poll(100, 50);
        System.out.println(poll);
    }
    @Test
    void refresh(){
        taskService.refresh();
    }
    

}
