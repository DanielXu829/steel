package com.cisdi.steel.dto.response.gl.req;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 10/8/2018.
 */
public class TagQueryParam {
    public List<String> getTagnames() {
        return tagnames;
    }

    public TagQueryParam() {

    }

    public TagQueryParam(Long startTime, Long endTime, List<String> names) {
        this.starttime = startTime;
        this.endtime = endTime;
        this.tagnames = names;
    }

    public void setTagnames(List<String> tagnames) {
        this.tagnames = tagnames;
    }


    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    private long starttime;
    private long endtime;
    private List<String> tagnames = new ArrayList<String>();
}
