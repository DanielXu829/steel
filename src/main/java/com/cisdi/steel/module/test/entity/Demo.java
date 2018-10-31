package com.cisdi.steel.module.test.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.cisdi.steel.common.util.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * <p>Description:  文件demo实体类 </p>
 * <P>Date: 2018-10-19 </P>
 *
 * @author leaf
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Demo extends Model<Demo> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件路径
     */
    private String path;

    private Demo() {
    }

    public Demo(String name, String path) {
        this.name = name;
        this.path = path;
    }

    /**
     * 返回pageoffice编辑路径，加上文件路径参数
     *
     * @return
     */
    public String getPath() {
        String p = "javascript:POBrowser.openWindowModeless('', '')";
        try {
            System.out.println(path);
            p = "javascript:POBrowser.openWindowModeless('word?filePath=" + URLEncoder.encode(path.replaceAll("\\\\", "\\\\\\\\"), "UTF-8") + "', 'width=1200px;height=800px;')";
        } catch (Exception e) {
        }
        return p;
    }

    public String getPath2() {
        return "javascript:doAction('" + this.name + "','" + "http://10.0.75.1:8181/report/" + this.name + "')";
    }

    public static void main(String[] args) {
        String path = "e:/root";
        path = path.replaceAll("\\\\", "//");
        System.out.println(path);
    }

}
