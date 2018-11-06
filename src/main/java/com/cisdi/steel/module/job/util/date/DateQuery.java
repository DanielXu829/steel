package com.cisdi.steel.module.job.util.date;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 时间查询
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/5 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class DateQuery {
    private Date startTime;
    private Date endTime;

    private String startTimeKey = "starttime";
    private String endTimeKey = "endtime";

    public DateQuery(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 查询的开始时间
     *
     * @return 结果
     */
    public Long getQueryStartTime() {
        return startTime.getTime();
    }

    /**
     * 查询的结束时间
     *
     * @return 结果
     */
    public Long getQueryEndTime() {
        return endTime.getTime();
    }

    /**
     * 获取查询的参数
     *
     * @return 结果
     */
    public Map<String, String> getQueryParam() {
        Map<String, String> map = new HashMap<>();
        map.put(startTimeKey, Objects.requireNonNull(getQueryStartTime()).toString());
        map.put(endTimeKey, Objects.requireNonNull(getQueryEndTime()).toString());
        return map;
    }
}
