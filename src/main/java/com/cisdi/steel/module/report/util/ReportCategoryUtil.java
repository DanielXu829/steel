package com.cisdi.steel.module.report.util;

import com.cisdi.steel.module.report.entity.ReportCategory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * <p>Description: 获得树形结构的工具类实际使用,可将BaseTreeObj直接覆盖为指定类</p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author common
 * @date 2018/3/24
 * @version 1.0
 *
 */
public class ReportCategoryUtil {

    /**
     * 获得指定节点下所有归档
     *
     * @param list     需要归档的数据
     * @param parentId 根节点id
     * @return 结果
     */
    public static List<ReportCategory> list2TreeConverter(List<ReportCategory> list, Long parentId) {
        List<ReportCategory> returnList = new ArrayList<>();

         for (ReportCategory res : list) {
             //判断对象是否为根节点
             if (res.getParentId().equals(parentId)) {
                //该节点为根节点,开始递归
                //通过递归为节点设置childList
                recursionFn(list, res);
                returnList.add(res);
            }
         }

        return returnList;
    }

    /**
     * 获得指定节点下所有归档，没有叶子节点
     *
     * @param list     需要归档的数据
     * @param parentId 根节点id
     * @return 结果
     */
    public static List<ReportCategory> list2TreeConverterNoLeaf(List<ReportCategory> list, Long parentId) {
        List<ReportCategory> returnList = new ArrayList<>();

        for (ReportCategory res : list) {
            //判断对象是否为根节点
            if (res.getParentId().equals(parentId)) {
                //该节点为根节点,开始递归
                //通过递归为节点设置childList
                recursionFnNoLeaf(list, res);
                returnList.add(res);
            }
        }

        return returnList;
    }

    /**
     * 递归列表
     * 通过递归,给指定t节点设置childList
     *
     * @param list 需要归档的数据
     * @param t    对象是否还子节点
     */
    private static void recursionFn(List<ReportCategory> list, ReportCategory t) {
        //只能获取当前t节点的子节点集,并不是所有子节点集
        List<ReportCategory> childsList = getChildList(list, t);
        //设置他的子集对象集
        t.setChildList(childsList);

        //迭代子集对象集
        //遍历完,则退出递归
        for (ReportCategory nextChild : childsList) {

            //判断子集对象是否还有子节点
            if (!CollectionUtils.isEmpty(childsList)) {
                //有下一个子节点,继续递归
                recursionFn(list, nextChild);
            }
        }
    }

    /**
     * 递归列表
     * 通过递归，给指定t节点设置childList，没有叶子节点
     *
     * @param list 需要归档的数据
     * @param t    对象是否还子节点
     */
    private static void recursionFnNoLeaf(List<ReportCategory> list, ReportCategory t) {
        //只能获取当前t节点的子节点集,并不是所有子节点集
        List<ReportCategory> childsList = getChildListNoLeaf(list, t);
        //设置他的子集对象集
        t.setChildList(childsList);

        //迭代子集对象集
        //遍历完,则退出递归
        for (ReportCategory nextChild : childsList) {

            //判断子集对象是否还有子节点
            if (!CollectionUtils.isEmpty(childsList)) {
                //有下一个子节点,继续递归
                recursionFnNoLeaf(list, nextChild);
            }
        }
    }

    /**
     * 获得指定节点下的所有子节点
     *
     * @param list 数据
     * @param t    父节点
     * @return 集合
     */
    public static List<ReportCategory> getChildList(List<ReportCategory> list, ReportCategory t) {
        List<ReportCategory> childList = new ArrayList<>();
        //遍历集合元素,如果元素的ParentId==指定元素的id,则说明是该元素的子节点
        for (ReportCategory t1 : list) {
            if (t1.getParentId().equals(t.getId())) {
                childList.add(t1);
            }
        }

        return childList;
    }

    /**
     * 获得指定节点下的所有子节点，没有叶子节点
     *
     * @param list 数据
     * @param t    父节点
     * @return 集合
     */
    private static List<ReportCategory> getChildListNoLeaf(List<ReportCategory> list, ReportCategory t) {
        List<ReportCategory> childList = new ArrayList<>();
        //遍历集合元素,如果元素的ParentId==指定元素的id,则说明是该元素的子节点
        for (ReportCategory t1 : list) {
            if (t1.getParentId().equals(t.getId()) && !"1".equals(t1.getLeafNode())) {
                childList.add(t1);
            }
        }

        return childList;
    }

    /**
     * 判断是否还有下一个子节点
     *
     * @param list 集合
     * @param t javabean
     */
    public static boolean hasChild(List<ReportCategory> list, ReportCategory t) {
        return getChildList(list, t).size() > 0;
    }


}