package com.cisdi.steel.module.quartz.mapper;

import com.cisdi.steel.module.quartz.entity.QuartzEntity;
import com.cisdi.steel.module.quartz.query.QuartzEntityQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * <p>Description:  quartzMapper </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author leaf
 * @version 1.0
 * @since 2018/7/9
 */
@Mapper
public interface QuartzMapper {

    /**
     * 查询列表
     *
     * @param query 分页参数
     * @return 集合
     */
    List<QuartzEntity> selectQuartzList(QuartzEntityQuery query);

    /**
     * 通过任务编码查询
     * @param code
     * @return
     */
    QuartzEntity selectQuartzByCode(@Param("jobName") String code);

    /**
     * 查询 数量
     *
     * @param query 查询条件
     * @return 结果
     */
    long selectQuartzCount(QuartzEntityQuery query);


    /**
     * 查询所有分组名称
     *
     * @return 分组
     */
    Set<String> selectAllGroupName();


    /**
     * 查询执行错误的 触发器
     *
     * @return 结果
     */
    List<QuartzEntity> selectErrorRecord();
}
