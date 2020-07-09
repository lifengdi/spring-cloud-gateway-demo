package com.lifengdi.gateway.alarm;

/**
 * 报警接口
 * @author: Li Fengdi
 * @date: 2020/3/13 15:55
 */
public interface IAlarm {
    /**
     * 发送报警信息
     *
     * @param receiver 报警接受者
     * @param subject  报警主题
     * @param content  报警内容
     */
    void alarm(String receiver, String subject, String content);

    /**
     * 发送报警信息
     *
     * @param receiver 报警接受者
     * @param subject  报警主题
     * @param e        异常信息
     */
    void alarm(String receiver, String subject, Throwable e);
}
