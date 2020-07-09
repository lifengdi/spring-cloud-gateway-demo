package com.lifengdi.gateway.alarm;

import com.lifengdi.gateway.utils.EmailUtils;
import com.lifengdi.gateway.utils.EnvironmentUtils;
import com.lifengdi.gateway.utils.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 邮件报警
 * @author: Li Fengdi
 * @date: 2020/3/13 17:45
 */
@Component
@Slf4j
public class EmailAlarm extends BaseAlarm {

    // 发消息的间隔（秒）
    @Value("${alarm.email.interval.time:300}")
    private Long intervalTime;
    @Value("${alarm.email.receiver:null}")
    private String receiver;

    @Override
    protected void sendAlarm(String receiver, String subject, String content) {
        boolean sendSuccess = false;
        try {
            subject = FormatUtils.wrapStringWithBracket(EnvironmentUtils.getAppEnv() + subject);
            EmailUtils.send(subject, content, receiver);
            sendSuccess = true;
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        } finally {
            log.info("发送报警邮件 {} receiver:{};subject:{}", sendSuccess, receiver, subject);
        }
    }

    @Override
    public void alarm(String receiver, String subject, Throwable e) {
        if (validReceiver(receiver)) {
            String body = ExceptionUtils.getStackTrace(e);
            // 缓存报警信息，等待后台守护线程去执行报警信息的发送程序
            cacheAlarm(receiver, subject, body, e.getClass().getName());
        }
    }

    public void alarm(String subject, Throwable e) {
        alarm(receiver, subject, e);
    }

    public void alarm(String subject, String content) {
        alarm(receiver, subject, content);
    }

    public void alarm(Throwable e) {
        alarm("", e);
    }

    @Override
    protected long getIntervalTime() {
        return (intervalTime == null ? DEFAULT_INTERVAL : intervalTime) * 1000;
    }
}
