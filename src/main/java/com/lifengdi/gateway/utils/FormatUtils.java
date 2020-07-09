package com.lifengdi.gateway.utils;

/**
 * 格式化工具类
 * @author: Li Fengdi
 * @date: 2020/3/13 17:42
 */
public interface FormatUtils {

    /**
     * 将字符串用中括号括起来
     * @param s 字符串
     * @return [s]
     */
    static String wrapStringWithBracket(String s) {
        return "[" + s + "] ";
    }

}
