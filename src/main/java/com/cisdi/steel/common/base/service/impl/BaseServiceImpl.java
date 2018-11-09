package com.cisdi.steel.common.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cisdi.steel.common.base.entity.AbstractDataEntity;
import com.cisdi.steel.common.base.service.IBaseService;
import com.cisdi.steel.common.base.vo.BaseId;
import com.cisdi.steel.common.base.vo.DataTableQuery;
import com.cisdi.steel.common.base.vo.PageQuery;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.constant.SearchParam;
import com.cisdi.steel.common.enums.ColumnEnum;
import com.cisdi.steel.common.exception.LeafException;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


/**
 * <p>Description:  基础增删查改 </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * @author yangpeng
 * @version 1.0
 * @date 2018/1/6
 * @since 1.8
 */
@SuppressWarnings("ALL")
@Slf4j
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {


    /**
     * 增加或删除 默认为1条记录 通过id删除
     */
    private static final int SIZE = 1;

    /**
     * 初始化分页参数
     *
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    protected Page<T> initPageParam(PageQuery pageQuery) {
        return new Page<T>(pageQuery.getCurrentPage(), pageQuery.getPageSize());
    }

    @Override
    public ApiResult pageList(PageQuery pageQuery) {
        // 设置分页
        Page<T> page = initPageParam(pageQuery);
        // 查询数据
        this.page(page, null);
        // 返回结果
        return ApiUtil.successPage(page.getTotal(), page.getRecords());
    }

    /**
     * 分页 搜索
     *
     * @param dt
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public ApiResult pageList(DataTableQuery dt) {
        // 设置分页
        Page<T> page = initPageParam(dt);
        // 创建条件
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        // 加载搜索条件
        loadSearchParam(dt.getSearchParams(), wrapper);
        // 加载排序
        loadSort(dt.getSorts(), wrapper);
        log.debug("==>  query:" + dt.toString());
        // 查询数据
        page(page, wrapper);
        // 返回结果
        return ApiUtil.successPage(page.getTotal(), page.getRecords());
    }


    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult saveRecord(T record) {
        log.debug("<== result:" + record.toString());
        if (record instanceof AbstractDataEntity) {
            AbstractDataEntity b = (AbstractDataEntity) record;
            // 当前时间
            LocalDateTime dateTime = LocalDateTime.now();
            // 当前操作人id
//            long currentId = getCurrentId();
            long currentId = 0L;
            // 当前时间
            b.setUpdateTime(dateTime);
            // 当前操作人id
            b.setUpdateId(currentId);
            if (Objects.isNull(b.getId())) {
                // 表明 创建时间
                b.setCreateTime(dateTime);
                // 表明 创建人的id
                b.setCreateId(currentId);
                // 添加
                Integer result = baseMapper.insert(record);
                return getResult(result);
            }
            // 防止修改创建时间和创建人
            b.setCreateId(null);
            b.setCreateTime(null);
            //更新
            Integer result = baseMapper.updateById(record);
            return getResult(result);
        }
        // 由于不知道主键 保存或更新使用mybatis plus的主键更新，如何没有主键 会报错
        boolean b = this.saveOrUpdate(record);
        return getResult(b);
    }

    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult insertRecord(T record) {
        log.debug("<== result:" + record.toString());
        if (record instanceof AbstractDataEntity) {
            AbstractDataEntity b = (AbstractDataEntity) record;
            // 当前时间
            LocalDateTime dateTime = LocalDateTime.now();
            // 当前操作人id
            long currentId = 0L;
            // 当前时间
            b.setUpdateTime(dateTime);
            // 当前操作人id
            b.setUpdateId(currentId);
            // 表明 创建时间
            b.setCreateTime(dateTime);
            // 表明 创建人的id
            b.setCreateId(currentId);
        }
        // 添加
        Integer result = baseMapper.insert(record);
        return getResult(result);
    }

    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult updateRecord(T record) {
        log.debug("<== result:" + record.toString());
        if (record instanceof AbstractDataEntity) {
            AbstractDataEntity b = (AbstractDataEntity) record;
            // 当前时间
            LocalDateTime dateTime = LocalDateTime.now();
            // 当前操作人id
            long currentId = 0L;
            // 当前时间
            b.setUpdateTime(dateTime);
            // 当前操作人id
            b.setUpdateId(currentId);
            // 防止修改创建时间和创建人
            b.setCreateId(null);
            b.setCreateTime(null);
        }
        //更新
        Integer result = baseMapper.updateById(record);
        return getResult(result);
    }

    @Override
    @Transactional(rollbackFor = LeafException.class)
    public ApiResult deleteRecord(BaseId record) {
        // 如果没有记录 ，直接返回删除成功
        if (Objects.isNull(record)) {
            return ApiUtil.success();
        }
        // 判断是否id
        if (Objects.nonNull(record.getId())) {
            // 删除
            log.debug("delete  id" + record.getId());
            Integer result = baseMapper.deleteById(record.getId());
            // 等于1 表示删除了1条记录
            return getResult(result);
        }
        if (Objects.nonNull(record.getIds()) && !record.getIds().isEmpty()) {
            log.debug("delete ids " + record.getIds());
            boolean result = this.removeByIds(record.getIds());
            // 多条记录
            return getResult(result);
        }
        return ApiUtil.success();
    }


    @Override
    public ApiResult getById(Long id) {
        log.debug("==> query:id    ===" + id);
        T t = baseMapper.selectById(id);
        log.debug("<== query:result===" + t);
        return ApiUtil.success(t);
    }

    /**
     * 返回结果
     * 默认处理一条记录 多条记录不适用
     *
     * @param flag true 或false 表明是否成功
     * @return 结果
     */
    protected ApiResult getResult(Integer result) {
        return getResult(result, SIZE);
    }

    /**
     * 根据记录数判断是否成功
     *
     * @param result 需要判断的数据
     * @param size   数量
     * @return 返回结果
     */
    protected ApiResult getResult(Integer result, int size) {
        log.debug("<== result: {} eq {} ", result, size);
        if (size == result.intValue()) {
            return ApiUtil.success();
        }
        return ApiUtil.fail();
    }

    /**
     * 根据记录数判断是否成功
     *
     * @param result 新增或更新 返回的结果
     * @return 返回结果
     */
    protected ApiResult getResult(boolean result) {
        if (result) {
            return ApiUtil.success();
        } else {
            return ApiUtil.fail();
        }
    }

    /**
     * 加载 搜索条件
     *
     * @param searchParams 搜索参数
     * @param wrapper      查询条件
     * @param flag         条件 判断是and 还是 or 连接条件 默认 and连接多个条件
     */
    protected void loadSearchParam(Map<String, Object> searchParams, QueryWrapper<T> wrapper, boolean flag) {
        if (Objects.nonNull(searchParams)) {
            searchParams.forEach((k, v) -> {
                if (idLoadCnd(SearchParam.SEARCH_EQ, k, v)) {
                    // 等于 驼峰转下划线
                    wrapper.eq(StringUtils.toUnderScoreCase(k.split(SearchParam.SEARCH_EQ)[1]), v);
                } else if (idLoadCnd(SearchParam.SEARCH_LLIKE, k, v)) {
                    // 左模糊 不建议
                    wrapper.likeLeft(StringUtils.toUnderScoreCase(k.split(SearchParam.SEARCH_LLIKE)[1]), String.valueOf(v));
                } else if (idLoadCnd(SearchParam.SEARCH_RLIKE, k, v)) {
                    // 右模糊
                    wrapper.likeRight(StringUtils.toUnderScoreCase(k.split(SearchParam.SEARCH_RLIKE)[1]), String.valueOf(v));
                } else if (idLoadCnd(SearchParam.SEARCH_LIKE, k, v)) {
                    // 全模糊 不建议
                    wrapper.like(StringUtils.toUnderScoreCase(k.split(SearchParam.SEARCH_LIKE)[1]), String.valueOf(v));
                }
                if (flag) {
                    wrapper.or();
                }
            });
        }
    }


    /**
     * 加载搜索参数 and连接
     *
     * @param searchParams 搜索参数
     * @param wrapper      查询条件
     */
    protected void loadSearchParam(Map<String, Object> searchParams, QueryWrapper<T> wrapper) {
        loadSearchParam(searchParams, wrapper, false);
    }


    /**
     * 加载 排序条件
     *
     * @param sorts 排序参数
     * @param cnd   条件
     */
    protected void loadSort(Map<String, String> sorts, QueryWrapper<T> wrapper) {
        if (Objects.nonNull(sorts) && sorts.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            Set<String> keys = sorts.keySet();
            Iterator<String> iterator = keys.iterator();
            sorts.forEach((k, v) -> {
                // 驼峰转下划线（数据库一般为下划线）
                wrapper.orderBy(StringUtils.isNotBlank(k), v.toLowerCase().indexOf("asc") > 0, StringUtils.toUnderScoreCase(k));
            });
        }
    }

    /**
     * 是否禁止
     *
     * @param wrapper 条件
     */
    protected void forbid(QueryWrapper<T> wrapper) {
        wrapper.eq(ColumnEnum.FORBID.getColumn(), Constants.NO);
    }

    /**
     * 排序
     *
     * @param wrapper 条件
     */
    protected void sortAsc(QueryWrapper<T> wrapper) {
        wrapper.orderByAsc(ColumnEnum.SORT.getColumn());
    }

    /**
     * 默认 创建时间 倒序
     *
     * @param wrapper 条件
     */
    protected void sortCreateTimeDesc(QueryWrapper<T> wrapper) {
        wrapper.orderByDesc(ColumnEnum.CREATE_TIME.getColumn());
    }

    /**
     * 默认 更新时间 倒序
     *
     * @param wrapper 条件
     */
    protected void sortUpdateTimeDesc(QueryWrapper<T> wrapper) {
        wrapper.orderByDesc(ColumnEnum.CREATE_TIME.getColumn());
    }


    /**
     * 拼接时间范围
     *
     * @param wrapper   拼接条件
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    protected void wrapperBetween(QueryWrapper<T> wrapper, Date startTime, Date endTime) {
        // 默认字段名create_time
        wrapperBetween(wrapper, startTime, endTime, ColumnEnum.CREATE_TIME.getColumn());
    }


    /**
     * 拼接时间范围
     *
     * @param wrapper   拼接条件
     * @param startTime 开始时间 取当前的开始时间
     * @param endTime   结束时间 取当天的最后时间匹配
     * @param column    需要匹配的字段名
     */
    protected void wrapperBetween(QueryWrapper<T> wrapper, Date startTime, Date endTime, String column) {
        if (Objects.isNull(startTime)) {
            return;
        }
        if (Objects.isNull(endTime)) {
            // 默认取今天
            endTime = new Date();
        }
        // 默认取当天的最后时间
        Date dateEndTime = DateUtil.getDateEndTime(endTime);
        // 当前的开始时间
        Date dateBeginTime = DateUtil.getDateBeginTime(startTime);
        // 拼接条件
        wrapper.between(column, dateBeginTime, dateEndTime);
    }

    /**
     * 是否加载 查询条件
     *
     * @param cnd 条件
     * @param k   key
     * @param v   cellValue
     * @return 是否满足格式
     */
    private boolean idLoadCnd(String cnd, String k, Object v) {
        return k.startsWith(cnd) && null != v && v.toString().length() > 0;
    }

}

