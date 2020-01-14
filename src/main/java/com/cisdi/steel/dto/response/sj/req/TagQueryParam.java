package com.cisdi.steel.dto.response.sj.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 10/8/2018.
 */
@ApiModel(value = "Tag查询参数对象", description = "Tag查询参数对象")
public class TagQueryParam {
    @ApiModelProperty(value = "Tag名称列表", name = "tagNames", required = true)
    private List<String> tagNames = new ArrayList<>();
    @ApiModelProperty(value = "数据的存储粒度(day/shift/hour 默认查原始数据)", name = "type")
    private String type = "tag_value";
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间", name = "start")
    private Date start;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间", name = "end")
    private Date end;
    @ApiModelProperty(value = "计算方法（SUM,AVG,MAX,MIN,STDDEV方差），/tagValueAction一次一个方法，/tagValueActions可多个方法以,间隔", name = "method")
    private String method;
    @ApiModelProperty(value = "数据排序方式ASC顺序，DESC倒叙，默认ASC", name = "orderBy")
    private String orderBy = "ASC";

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
