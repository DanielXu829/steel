package com.cisdi.steel.module.test.controller;

import com.cisdi.steel.common.util.FileUtil;
import com.cisdi.steel.common.util.StringUtils;
import com.cisdi.steel.module.test.service.DemoService;
import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.boot.web.servlet.ServletRegistrationBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author Administrator
 */
@RestController
public class DemoController {

    @Value("${posyspath}")
    private String poSysPath;

    @Value("${popassword}")
    private String poPassWord;

    @Autowired
    private DemoService demoService;

    @RequestMapping("/demo")
    public ModelAndView demo(Map<String, Object> map, HttpServletRequest request) throws Exception {
        request.setCharacterEncoding("utf-8");

        String filePath = request.getParameter("filePath");
        if (StringUtils.isBlank(filePath)) {
            filePath = "/xroot/office";
        }
        map.put("list", demoService.fileListDirectory(filePath));
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/word", method = RequestMethod.GET)
    public ModelAndView showWord(HttpServletRequest request, Map<String, Object> map) throws Exception {
        request.setCharacterEncoding("utf-8");

        String filePath = request.getParameter("filePath");

        if (StringUtils.isBlank(filePath)) {
            filePath = "/xroot/office";
        }
        PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
        poCtrl.setServerPage("/poserver.zz");//设置服务页面
        poCtrl.addCustomToolButton("保存", "Save", 1);//添加自定义保存按钮
        //poCtrl.addCustomToolButton("盖章","AddSeal",2);//添加自定义盖章按钮
        poCtrl.setSaveFilePage("/save?filePath=" + filePath);//设置处理文件保存的请求方法
        //打开指定目录下的office
        File file = new File(filePath);
//        File file = new File("e://test.xlsx");
        String absolutePath = request.getServletContext().getRealPath("/");
        File temp = new File(absolutePath + file.getName());
        try {
            FileUtil.emptyDirectory(absolutePath);
            FileUtil.CopyFile(file, temp);
        } catch (Exception e) {
        }
        String newPath = "/" + file.getName();
        //获取文件类型
        String fileType = filePath.substring(filePath.lastIndexOf("."));
        if (fileType.trim().equals(".doc") || fileType.trim().equals(".docx")) {
            poCtrl.webOpen(newPath, OpenModeType.docNormalEdit, "word");//可编辑模式打开word文档
        } else if (fileType.trim().equals(".xls") || fileType.trim().equals(".xlsx")) {
            poCtrl.webOpen(newPath, OpenModeType.xlsNormalEdit, "excel");//可编辑模式打开excel文档
        } else if (fileType.trim().equals(".ppt") || fileType.trim().equals(".pptx")) {
            poCtrl.webOpen(newPath, OpenModeType.pptNormalEdit, "ppt");//可编辑模式打开ppt文档
        }
        map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        return new ModelAndView("Word");
    }

    @RequestMapping("/save")
    public void saveFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("utf-8");
        String filePath = request.getParameter("filePath");
        FileSaver fs = new FileSaver(request, response);
        fs.saveToFile(filePath);
        fs.close();
    }


    /**
     * 添加PageOffice的服务器端授权程序Servlet（必须）
     *
     * @return
     */
//    @Bean
//    public ServletRegistrationBean servletRegistrationBean() {
//        com.zhuozhengsoft.pageoffice.poserver.Server poserver = new com.zhuozhengsoft.pageoffice.poserver.Server();
//        poserver.setSysPath(poSysPath);//设置PageOffice注册成功后,license.lic文件存放的目录
//        ServletRegistrationBean srb = new ServletRegistrationBean(poserver);
//        srb.addUrlMappings("/poserver.zz");
//        srb.addUrlMappings("/posetup.exe");
//        srb.addUrlMappings("/pageoffice.js");
//        srb.addUrlMappings("/sealsetup.exe");
//        return srb;
//    }
}
