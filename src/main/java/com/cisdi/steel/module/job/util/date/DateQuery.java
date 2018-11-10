package com.cisdi.steel.module.job.util.date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 时间查询条件
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

    /**
     * 记录的时间
     */
    private Date recordDate;
    /**
     * 查询的开始时间
     */
    private Date startTime;
    /**
     * 查询的结束时间
     */
    private Date endTime;
    /**
     * 开始时间key
     */
    private String startTimeKey = "starttime";
    /**
     * 结束时间key
     */
    private String endTimeKey = "endtime";

    public DateQuery(Date recordDate) {
        this.recordDate = recordDate;
    }

    public DateQuery(Date startTime, Date endTime, Date recordDate) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.recordDate = recordDate;
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
     * 构建时间查询参数
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
