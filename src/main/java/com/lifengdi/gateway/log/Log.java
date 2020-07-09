package com.lifengdi.gateway.log;

import com.lifengdi.gateway.utils.EnvironmentUtils;
import com.lifengdi.gateway.utils.IpUtils;
import lombok.Data;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * 日志实体类，方便后续接入ELK
 */
@Data
public class Log {
    private Log.TYPE logType;
    private Log.LEVEL level;
    private String appEnv;
    private String applicationName;
    private String hostName;
    private String ip;
    private Long handleTime;
    private String timeStamp;
    private String requestUrl;
    private String userName;
    private String account;
    private String requestBody;
    private String responseBody;
    private String requestId;
    private String requestMethod;
    private Integer status;
    private String serverIp;
    private String sessionId;
    private String _class;

    public Log() {
        this(Log.TYPE.REQUEST);
    }

    public Log(Log.TYPE logType) {
        this.logType = logType;
        this.applicationName = EnvironmentUtils.getAppEnv();
        this.hostName = IpUtils.getHostName();
        this.timeStamp = ZonedDateTime.now(ZoneOffset.of("+08:00")).toString();
        this.serverIp = IpUtils.getLocalIp();
    }

    /**
     * 日志级别枚举类
     */
    public static enum LEVEL {
        OFF,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE,
        ALL;

        private LEVEL() {
        }
    }

    /**
     * 日志类型枚举
     */
    public static enum TYPE {
        REQUEST,
        RESPONSE,
        OUT;

        private TYPE() {
        }
    }
}
