package com.lifengdi.gateway.alarm;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 报警内容
 * @author: Li Fengdi
 * @date: 2020/3/13 17:32
 */
@Data
public class AlarmContent {

    /**
     * 报警信息接收人
     */
    private String receiver;
    /**
     * 报警信息主题
     */
    private String subject;
    /**
     * 报警信息内容
     */
    private String content;
    /**
     * 异常出现次数
     */
    private AtomicLong count;

    /**
     * 重置次数
     */
    public void resetCount() {
        this.count.set(0);
    }
}