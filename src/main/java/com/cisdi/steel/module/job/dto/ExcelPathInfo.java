package com.cisdi.steel.module.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>Description:         </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/11/6 </P>
 *
 * @author leaf
 * @version 1.0
 */
@AllArgsConstructor
@Data
public class ExcelPathInfo {
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件保存路径
     */
    private String saveFilePath;
}
