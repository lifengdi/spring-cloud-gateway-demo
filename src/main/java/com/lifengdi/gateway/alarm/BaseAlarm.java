package com.lifengdi.gateway.alarm;

import com.google.common.collect.Maps;
import com.lifengdi.gateway.thread.DaemonThreadFactory;
import com.lifengdi.gateway.utils.FormatUtils;
import com.lifengdi.gateway.utils.IpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Li Fengdi
 * @date: 2020/3/13 17:32
 */
public abstract class BaseAlarm implements IAlarm {

    protected static final Long DEFAULT_INTERVAL = 300L; // 发送报警默认间隔时间，300秒
    private static final long CLEAR_INTERVAL = 12 * 60 * 60 * 1000; // 12个小时内没有新增报警，则清除相应的key
    private final ConcurrentMap<String, Long> sendRecord = Maps.newConcurrentMap();
    private final ConcurrentMap<String, AlarmContent> alarmMap = Maps.newConcurrentMap();

    public BaseAlarm() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
        executor.scheduleWithFixedDelay(new AlarmThread(), getIntervalTime(), getIntervalTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void alarm(String receiver, String subject, String content) {
        if (validReceiver(receiver)) {
            cacheAlarm(receiver, subject, content, subject);
        }
    }

    @Override
    public void alarm(String receiver, String subject, Throwable e) {
        if (validReceiver(receiver)) {
            cacheAlarm(receiver, subject, ExceptionUtils.getStackTrace(e), subject + e.getClass().getName());
        }
    }

    /**
     * 校验接收者是否有效
     *
     * @param receiver 接收者
     * @return Boolean
     */
    protected boolean validReceiver(String receiver) {
        return StringUtils.isNotBlank(receiver);
    }

    /**
     * 缓存报警信息，如果是新的异常类型，则直接发送报警信息，否则进入缓存，防止报警太过频繁
     *
     * @param receiver 信息接收者
     * @param subject  信息主题
     * @param content  信息内容
     * @param key      异常类型
     */
    protected final void cacheAlarm(String receiver, String subject, String content, final String key) {
        Long lastTime = sendRecord.get(key);
        long currentTime = System.currentTimeMillis();
        if (lastTime == null || currentTime - lastTime >= getIntervalTime()) {
            sendAlarm(receiver, subject, IpUtils.getIpWithBracketWrap() + content);
            sendRecord.put(key, currentTime);
        } else {
            AlarmContent ac = alarmMap.get(key);
            if (ac == null) {
                ac = new AlarmContent();
                ac.setContent(content);
                ac.setCount(new AtomicLong());
                ac.setReceiver(receiver);
                ac.setSubject(subject);
                alarmMap.put(key, ac);
            }
            ac.getCount().incrementAndGet();
        }
    }

    protected abstract void sendAlarm(String receiver, String subject, String content);

    /**
     * 获取发送报警信息的间隔时间
     *
     * @return 间隔时间
     */
    protected abstract long getIntervalTime();

    class AlarmThread implements Runnable {

        @Override
        public void run() {
            alarmMap.keySet().forEach(key -> {
                AlarmContent ac = alarmMap.get(key);
                if (ac.getCount().get() == 0
                        && System.currentTimeMillis() - sendRecord.get(key) >= CLEAR_INTERVAL) {
                    alarmMap.remove(key);
                    sendRecord.remove(key);
                } else if (ac.getCount().get() > 0) {
                    sendAlarm(ac.getReceiver(),
                            ac.getSubject()
                                    + FormatUtils.wrapStringWithBracket("异常次数：" + ac.getCount().get()),
                            IpUtils.getIpWithBracketWrap() + ac.getContent());
                    ac.resetCount();
                    sendRecord.put(key, System.currentTimeMillis());
                }
            });

        }

    }
}
