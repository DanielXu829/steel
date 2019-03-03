package com.cisdi.steel.module.test.onlyoffice.controllers;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.cisdi.steel.common.util.DateUtil;
import com.cisdi.steel.common.util.encodes.MD5Util;
import com.cisdi.steel.module.test.onlyoffice.entities.FileModel;
import com.cisdi.steel.module.test.onlyoffice.helpers.ConfigManager;
import com.cisdi.steel.module.test.onlyoffice.helpers.DocumentManager;
import com.cisdi.steel.module.test.onlyoffice.helpers.FileUtility;
import com.cisdi.steel.module.test.onlyoffice.helpers.ServiceConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.Random;

@RestController
@Slf4j
public class EditorController {

    @RequestMapping("/onlyoffice/edit")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response, Model model) {
        String filePath = "";
        String fileName = "";
        if (request.getParameterMap().containsKey("filePath")) {
            filePath = request.getParameter("filePath");
        }

        if (filePath != null) {
            try {
                DocumentManager.Init(request, response);
                fileName = DocumentManager.createTempServerFile(filePath);
            } catch (Exception ex) {
                log.error("文件不存在：" + ex.getMessage());
                return new ModelAndView(new FastJsonJsonView(), "此文件不存在", null);
            }
        }

        String mode = "";
        if (request.getParameterMap().containsKey("mode")) {
            mode = request.getParameter("mode");
        }
        Boolean desktopMode = !"embedded".equals(mode);

        FileModel file = new FileModel();
        file.setTypeDesktop(desktopMode);
        file.setFileName(fileName);

        System.out.println("==========EditorController==========");
        DocumentManager.Init(request, response);
        //要编辑的文件名
        model.addAttribute("fileName", fileName);
        //要编辑的文件类型
        String fileType = FileUtility.GetFileExtension(filePath).replace(".", "");
        model.addAttribute("fileType", fileType);
        //要编辑的文档类型
        model.addAttribute("documentType", FileUtility.GetFileType(filePath).toString().toLowerCase());
        //要编辑的文档访问url
        model.addAttribute("fileUri", DocumentManager.GetFileUri(fileName) + "." + fileType);

        File file1 = new File(filePath);
        String md5 = MD5Util.MD5(file1);

       // model.addAttribute("fileKey", md5);
        model.addAttribute("fileKey", ServiceConverter.GenerateRevisionId(DocumentManager.CurUserHostAddress(null) + "/" + filePath + new Random().nextInt(100)));
        model.addAttribute("callbackUrl", DocumentManager.GetCallback(filePath));
        model.addAttribute("serverUrl", DocumentManager.GetServerUrl());
        model.addAttribute("editorMode", DocumentManager.GetEditedExts().contains(FileUtility.GetFileExtension(filePath)) && !"view".equals(request.getAttribute("mode")) ? "edit" : "view");
        model.addAttribute("editorUserId", DocumentManager.CurUserHostAddress(null));
        model.addAttribute("type", desktopMode ? "desktop" : "embedded");
        Date date = new Date();
        model.addAttribute("docserviceApiUrl", ConfigManager.GetProperty("files.docservice.url.api"));
        model.addAttribute("docServiceUrlPreloader", ConfigManager.GetProperty("files.docservice.url.preloader"));
        model.addAttribute("currentYear", DateUtil.getFormatDateTime(date, DateUtil.yyyyFormat));
        model.addAttribute("convertExts", String.join(",", DocumentManager.GetConvertExts()));
        model.addAttribute("editedExts", String.join(",", DocumentManager.GetEditedExts()));
        model.addAttribute("documentCreated", DateUtil.getFormatDateTime(date, "MM/dd/yyyy"));
        model.addAttribute("permissionsEdit", Boolean.toString(DocumentManager.GetEditedExts().contains(FileUtility.GetFileExtension(filePath))).toLowerCase());
        return new ModelAndView("onlyofficeEdit");
    }
}