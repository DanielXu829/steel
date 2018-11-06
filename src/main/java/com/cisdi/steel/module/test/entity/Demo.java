package com.cisdi.steel.module.test.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.cisdi.steel.common.util.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

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
            p = "javascript:POBrowser.openWindowModeless('/pageoffice/edit?filePath=" + path.replaceAll("\\\\", "\\\\\\\\") + "', 'width=1200px;height=800px;')";
        } catch (Exception e) {
        }
        return p;
    }


}
