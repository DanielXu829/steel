package com.cisdi.steel.common.util.generate;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author yangpeng
 * @version 1.0
 * Description 生成21位的订单号（fork）
 * @date 2017/11/13
 */
@SuppressWarnings("ALL")
public class IdGenerator {


    /**
     * 用ip地址最后几个字节标示
     */
    private long workerId;

    /**
     * 可配置在properties中,启动时加载,此处默认先写成0
     */
    private long dataCenterId = 0L;

    /**
     * 序号
     */
    private long sequence = 0L;

    /**
     * 节点ID长度
     */
    private long workerIdBits = 8L;


    /**
     * 序列号12位
     */
    private long sequenceBits = 12L;

    /**
     * 机器节点左移12位
     */
    private long workerIdShift = sequenceBits;

    /**
     * 数据中心节点左移14位
     */
    private long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 4095
     */
    private long sequenceMask = ~(1L << sequenceBits);

    /**
     * 记录最后一次的时间轴
     */
    private long lastTimestamp = -1L;

    /**
     * 私有化构造器
     */
    private IdGenerator(){
        workerId = 0x000000FF & getLastIP();
    }

    /**
     * 静态内部类
     */
    private static final class IdGeneratorHolder{
        private static final IdGenerator INSTANCE = new IdGenerator();
    }

    /**
     *
     * @return 返回该对象的实例
     */
    public static  IdGenerator getInstance(){
        return IdGeneratorHolder.INSTANCE;
    }

    /**
     * 生成下一个订单号
     * @return 生成的订单号
     */
    public synchronized String nextId() {
        // 获取当前毫秒数
        long timestamp = timeGen();
        // 如果服务器时间有问题(时钟后退) 报错。
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果上次生成时间和当前时间相同,在同一毫秒内
        if (lastTimestamp == timestamp) {
            //sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
            sequence = (sequence + 1) & sequenceMask;
            //判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
            if (sequence == 0) {
                //自旋等待到下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
            sequence = 0L;
        }
        lastTimestamp = timestamp;


        long suffix = (dataCenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;

        String datePrefix = DateFormatUtils.format(timestamp,"yyyyMMddHHMMssSSS");

        return datePrefix + suffix;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private byte getLastIP(){
        byte lastIp = 0;
        try{
            InetAddress ip = InetAddress.getLocalHost();
            byte[] ipByte = ip.getAddress();
            lastIp = ipByte[ipByte.length - 1];
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return lastIp;
    }
}