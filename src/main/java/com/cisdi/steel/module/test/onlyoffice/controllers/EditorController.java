package com.cisdi.steel.module.test.onlyoffice.controllers;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.cisdi.steel.module.test.onlyoffice.entities.FileModel;
import com.cisdi.steel.module.test.onlyoffice.helpers.ConfigManager;
import com.cisdi.steel.module.test.onlyoffice.helpers.DocumentManager;
import com.cisdi.steel.module.test.onlyoffice.helpers.FileUtility;
import com.cisdi.steel.module.test.onlyoffice.helpers.ServiceConverter;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class EditorController {

    @RequestMapping("/EditorServlet")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
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
                return new ModelAndView(new FastJsonJsonView(), "Error: " + ex.getMessage(), ex);
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
        model.addAttribute("fileType", FileUtility.GetFileExtension(filePath).replace(".", ""));
        //要编辑的文档类型
        model.addAttribute("documentType", FileUtility.GetFileType(filePath).toString().toLowerCase());
        //要编辑的文档访问url
        model.addAttribute("fileUri", DocumentManager.GetFileUri(fileName));
        model.addAttribute("fileKey", ServiceConverter.GenerateRevisionId(DocumentManager.CurUserHostAddress(null) + "/" + fileName));
        model.addAttribute("callbackUrl", DocumentManager.GetCallback(fileName));
        model.addAttribute("serverUrl", DocumentManager.GetServerUrl());
        model.addAttribute("editorMode", DocumentManager.GetEditedExts().contains(FileUtility.GetFileExtension(filePath)) && !"view".equals(request.getAttribute("mode")) ? "edit" : "view");
        model.addAttribute("editorUserId", DocumentManager.CurUserHostAddress(null));

        model.addAttribute("type", desktopMode ? "desktop" : "embedded");


        model.addAttribute("docserviceApiUrl", ConfigManager.GetProperty("files.docservice.url.api"));
        model.addAttribute("docServiceUrlPreloader", ConfigManager.GetProperty("files.docservice.url.preloader"));
        model.addAttribute("currentYear", "2018");
        model.addAttribute("convertExts", String.join(",", DocumentManager.GetConvertExts()));
        model.addAttribute("editedExts", String.join(",", DocumentManager.GetEditedExts()));
        model.addAttribute("documentCreated", new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        model.addAttribute("permissionsEdit", Boolean.toString(DocumentManager.GetEditedExts().contains(FileUtility.GetFileExtension(filePath))).toLowerCase());
        return new ModelAndView("onlyofficeEdit");
    }
}