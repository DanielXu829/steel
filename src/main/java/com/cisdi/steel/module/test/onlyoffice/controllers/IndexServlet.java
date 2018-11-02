///*
// *
// * (c) Copyright Ascensio System Limited 2010-2018
// *
// * The MIT License (MIT)
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// *
// */
//
//
//package com.cisdi.steel.module.test.onlyoffice.controllers;
//
//
//import com.alibaba.fastjson.JSONObject;
//import com.cisdi.steel.common.util.JsonUtil;
//import com.cisdi.steel.module.test.onlyoffice.entities.FileType;
//import com.cisdi.steel.module.test.onlyoffice.helpers.ConfigManager;
//import com.cisdi.steel.module.test.onlyoffice.helpers.DocumentManager;
//import com.cisdi.steel.module.test.onlyoffice.helpers.FileUtility;
//import com.cisdi.steel.module.test.onlyoffice.helpers.ServiceConverter;
//import com.cisdi.steel.module.test.entity.Demo;
//import org.primeframework.jwt.domain.JWT;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.Part;
//import java.io.*;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Scanner;
//
//@RestController
//public class IndexServlet {
//
//    @RequestMapping("/index1")
//    public ModelAndView index(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
//
//        List<Demo> list = new ArrayList<>();
//        list.add(new Demo("demo.xlsx", "/file/demo.xlsx"));
//        map.put("list", list);
//        return new ModelAndView("onlyoffice");
//
//    }
//
//    @RequestMapping("/IndexServlet")
//    public ModelAndView processRequest(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String action = request.getParameter("type");
//        ModelAndView onlyoffice = new ModelAndView("onlyoffice");
//        map.put("cnvertExts", String.join(",", DocumentManager.GetConvertExts()));
//        map.put("editedExts", String.join(",", DocumentManager.GetEditedExts()));
//        if (action == null) {
//            String property = ConfigManager.GetProperty("files.docservice.url.preloader");
//            map.put("preloader", property);
//            return onlyoffice;
//        }
//
//        DocumentManager.Init(request, response);
//        PrintWriter writer = response.getWriter();
//
//        switch (action.toLowerCase()) {
//            case "upload":
//                Upload(request, response, writer);
//                break;
//            case "convert":
//                Convert(request, response, writer);
//                break;
//            case "track":
//                Track(request, response, writer);
//                break;
//        }
//        System.out.println("--------------------------IndexServlet:action===="+action+"-------------------------------------");
//        String property = ConfigManager.GetProperty("files.docservice.url.preloader");
//        map.put("preloader", property);
//        return onlyoffice;
//    }
//
//
//    private static void Upload(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
//        response.setContentType("text/plain");
//
//        try {
//            Part httpPostedFile = request.getPart("file");
//
//            String fileName = "";
//            for (String content : httpPostedFile.getHeader("content-disposition").split(";")) {
//                if (content.trim().startsWith("filename")) {
//                    fileName = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
//                }
//            }
//
//            long curSize = httpPostedFile.getSize();
//            if (DocumentManager.GetMaxFileSize() < curSize || curSize <= 0) {
//                writer.write("{ \"error\": \"File size is incorrect\"}");
//                return;
//            }
//
//            String curExt = FileUtility.GetFileExtension(fileName);
//            if (!DocumentManager.GetFileExts().contains(curExt)) {
//                writer.write("{ \"error\": \"File type is not supported\"}");
//                return;
//            }
//
//            InputStream fileStream = httpPostedFile.getInputStream();
//
//            fileName = DocumentManager.GetCorrectName(fileName);
//            String fileStoragePath = DocumentManager.StoragePath(fileName, null);
//
//            File file = new File(fileStoragePath);
//
//            try (FileOutputStream out = new FileOutputStream(file)) {
//                int read;
//                final byte[] bytes = new byte[1024];
//                while ((read = fileStream.read(bytes)) != -1) {
//                    out.write(bytes, 0, read);
//                }
//
//                out.flush();
//            }
//
//            writer.write("{ \"filename\": \"" + fileName + "\"}");
//
//        } catch (IOException | ServletException e) {
//            writer.write("{ \"error\": \"" + e.getMessage() + "\"}");
//        }
//    }
//
//    private static void Convert(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
//        response.setContentType("text/plain");
//
//        try {
//            String fileName = request.getParameter("filename");
//            String fileUri = DocumentManager.GetFileUri(fileName);
//            String fileExt = FileUtility.GetFileExtension(fileName);
//            FileType fileType = FileUtility.GetFileType(fileName);
//            String internalFileExt = DocumentManager.GetInternalExtension(fileType);
//
//            if (DocumentManager.GetConvertExts().contains(fileExt)) {
//                String key = ServiceConverter.GenerateRevisionId(fileUri);
//
//                String newFileUri = ServiceConverter.GetConvertedUri(fileUri, fileExt, internalFileExt, key, true);
//
//                if (newFileUri.isEmpty()) {
//                    writer.write("{ \"step\" : \"0\", \"filename\" : \"" + fileName + "\"}");
//                    return;
//                }
//
//                String correctName = DocumentManager.GetCorrectName(FileUtility.GetFileNameWithoutExtension(fileName) + internalFileExt);
//
//                URL url = new URL(newFileUri);
//                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
//                InputStream stream = connection.getInputStream();
//
//                if (stream == null) {
//                    throw new Exception("Stream is null");
//                }
//
//                File convertedFile = new File(DocumentManager.StoragePath(correctName, null));
//                try (FileOutputStream out = new FileOutputStream(convertedFile)) {
//                    int read;
//                    final byte[] bytes = new byte[1024];
//                    while ((read = stream.read(bytes)) != -1) {
//                        out.write(bytes, 0, read);
//                    }
//
//                    out.flush();
//                }
//
//                connection.disconnect();
//
//                //remove source file ?
//                //File sourceFile = new File(DocumentManager.StoragePath(fileName, null));
//                //sourceFile.delete();
//
//                fileName = correctName;
//            }
//
//            writer.write("{ \"filename\" : \"" + fileName + "\"}");
//
//        } catch (Exception ex) {
//            writer.write("{ \"error\": \"" + ex.getMessage() + "\"}");
//        }
//    }
//
//    private static void Track(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
//        String userAddress = request.getParameter("userAddress");
//        String fileName = request.getParameter("fileName");
//
//        String storagePath = DocumentManager.StoragePath(fileName, userAddress);
//        String body = "";
//
//        try {
//            Scanner scanner = new Scanner(request.getInputStream());
//            scanner.useDelimiter("\\A");
//            body = scanner.hasNext() ? scanner.next() : "";
//            scanner.close();
//        } catch (Exception ex) {
//            writer.write("get request.getInputStream error:" + ex.getMessage());
//            return;
//        }
//
//        if (body.isEmpty()) {
//            writer.write("empty request.getInputStream");
//            return;
//        }
//
//        JSONObject jsonObj;
//
//        try {
//            jsonObj = JsonUtil.jsonToObject(body, JSONObject.class);
//        } catch (Exception ex) {
//            writer.write("JSONParser.parse error:" + ex.getMessage());
//            return;
//        }
//
//        int status;
//        String downloadUri;
//
//        if (DocumentManager.TokenEnabled()) {
//            String token = (String) jsonObj.get("token");
//
//            JWT jwt = DocumentManager.ReadToken(token);
//            if (jwt == null) {
//                writer.write("JWT.parse error");
//                return;
//            }
//
//            status = jwt.getInteger("status");
//            downloadUri = jwt.getString("url");
//        } else {
//            status = (int) jsonObj.get("status");
//            downloadUri = (String) jsonObj.get("url");
//        }
//
//        int saved = 0;
//        if (status == 2 || status == 3)//MustSave, Corrupted
//        {
//            try {
//                URL url = new URL(downloadUri);
//                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
//                InputStream stream = connection.getInputStream();
//
//                if (stream == null) {
//                    throw new Exception("Stream is null");
//                }
//
//                File savedFile = new File(storagePath);
//                try (FileOutputStream out = new FileOutputStream(savedFile)) {
//                    int read;
//                    final byte[] bytes = new byte[1024];
//                    while ((read = stream.read(bytes)) != -1) {
//                        out.write(bytes, 0, read);
//                    }
//
//                    out.flush();
//                }
//
//                connection.disconnect();
//
//            } catch (Exception ex) {
//                saved = 1;
//            }
//        }
//
//        writer.write("{\"error\":" + saved + "}");
//    }
//}
