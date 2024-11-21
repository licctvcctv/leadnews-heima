package com.heima.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TaskTypeEnum 枚举类，用于定义不同类型的任务及其相关属性。
 * 每个任务类型包含一个任务编号、优先级和描述信息。
 */
@Getter
@AllArgsConstructor
public enum TaskTypeEnum {

    /**
     * NEWS_SCAN_TIME 表示 "文章定时审核" 任务类型
     * taskType: 1001，优先级：1
     * 描述：用于定时审核文章内容
     */
    NEWS_SCAN_TIME(1001, 1,"文章定时审核"),

    /**
     * REMOTEERROR 表示 "第三方接口调用失败，重试" 任务类型
     * taskType: 1002，优先级：2
     * 描述：用于处理第三方接口调用失败后重新尝试的任务
     */
    REMOTEERROR(1002, 2,"第三方接口调用失败，重试");

    private final int taskType; // 任务类型的唯一标识，用于区分不同的业务
    private final int priority; // 任务的优先级，用于控制任务执行的优先顺序
    private final String desc; // 任务类型的描述信息，帮助理解任务的用途
}
