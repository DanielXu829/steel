package com.cisdi.steel.module.onlyoffice.controllers;

import com.alibaba.fastjson.JSONObject;
import com.cisdi.steel.common.constant.Constants;
import com.cisdi.steel.common.enums.EditStatusEnum;
import com.cisdi.steel.common.resp.ApiResult;
import com.cisdi.steel.common.resp.ApiUtil;
import com.cisdi.steel.common.util.JsonUtil;
import com.cisdi.steel.module.quartz.entity.QuartzEntity;
import com.cisdi.steel.module.quartz.service.QuartzService;
import com.cisdi.steel.module.report.entity.ReportCategoryTemplate;
import com.cisdi.steel.module.report.entity.ReportIndex;
import com.cisdi.steel.module.report.service.ReportCategoryTemplateService;
import com.cisdi.steel.module.report.service.ReportIndexService;
import com.cisdi.steel.module.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

/**
 * 用于保存修改后的文件
 */
@RestController
@Slf4j
public class SaveFileController {
    /**
     * 文档编辑服务使用JavaScript API通知callbackUrl，向文档存储服务通知文档编辑的状态。文档编辑服务使用具有正文中的信息的POST请求。
     * https://api.onlyoffice.com/editors/callback
     * 参数示例：
     * {
     * "actions": [{"type": 0, "userid": "78e1e841"}],
     * "changesurl": "https://documentserver/url-to-changes.zip",
     * "history": {
     * "changes": changes,
     * "serverVersion": serverVersion
     * },
     * "key": "Khirz6zTPdfd7",
     * "status": 2,
     * "url": "https://documentserver/url-to-edited-document.docx",
     * "users": ["6d5a81d0"]
     * }
     *
     * @throws Exception
     */
    private final ReportIndexService baseService;

    private final ReportCategoryTemplateService reportCategoryTemplateService;

    private final Scheduler scheduler;

    @Autowired
    private QuartzService quartzService;

    @Autowired
    protected SysConfigService sysConfigService;

    @Autowired
    public SaveFileController(Scheduler scheduler, ReportIndexService baseService, ReportCategoryTemplateService reportCategoryTemplateService) {
        this.baseService = baseService;
        this.scheduler = scheduler;
        this.reportCategoryTemplateService = reportCategoryTemplateService;
    }

    @RequestMapping("/onlyoffice/save")
    public void saveFile(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter writer = null;

         /*
            0 - no document with the key identifier could be found,
            1 - document is being edited,
            2 - document is ready for saving,
            3 - document saving error has occurred,
            4 - document is closed with no changes,
            6 - document is being edited, but the current document state is saved,
            7 - error has occurred while force saving the document.
         * */
        System.out.println("===saveeditedfile------------");

        try {
            writer = response.getWriter();
            Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
            String body = scanner.hasNext() ? scanner.next() : "";
            JSONObject jsonObj = JsonUtil.jsonToObject(body, JSONObject.class);
            System.out.println("===saveeditedfile:" + jsonObj.get("status"));

            Integer status = (Integer) jsonObj.get("status");

            // 获取保存的reportIndex的主键
            String reportId = "";
            if (request.getParameterMap().containsKey("id")) {
                reportId = request.getParameter("id");

                if (reportId != "") {
                    // 当status为2或4，将editStatus改为0
                    if (status == 2 || status == 4) {
                        ReportIndex report = baseService.getById(reportId);

                        if (report != null) {
                            EditStatusEnum editStatus = EditStatusEnum.Release;
                            report.setEditStatus(editStatus.getEditStatus());
                            // 更新reportIndex的editStatus字段
                            ApiResult result = baseService.updateRecord(report);

                            // 获取jobName
                            String categoryCode = report.getReportCategoryCode();
                            QuartzEntity quartzEntity = quartzService.selectQuartzByCode(categoryCode);

                            // 获取language
                            String lang = sysConfigService.selectActionByCode(Constants.LANGUAGE_CODE);

                            // 获取reportTemplate
                            List<ReportCategoryTemplate> templates = reportCategoryTemplateService.selectTemplateInfo(report.getReportCategoryCode(), lang, report.getSequence());

                            // 根据templates中的makeupInterval判断是否触发job
                            boolean isTrigger = this.isTriggerJob(templates);

                            if (isTrigger) {
                                log.info("触发任务");
                                try {
                                    JobKey key = new JobKey(quartzEntity.getJobName(), quartzEntity.getJobGroup());
                                    scheduler.triggerJob(key);
                                } catch (SchedulerException e) {
                                    log.error("触发任务报错", e);
                                }
                            }
                        }
                    }
                }
            }

            String filePath = "";
            if (request.getParameterMap().containsKey("filePath")) {
                filePath = request.getParameter("filePath");
            }

            //强制保存时 回调修改文件
            if (status.intValue() == 6) {
                /*
                 * 当我们关闭编辑窗口后，十秒钟左右onlyoffice会将它存储的我们的编辑后的文件，，此时status = 2，通过request发给我们，我们需要做的就是接收到文件然后回写该文件。
                 * */

                /*
                 * 定义要与文档存储服务保存的编辑文档的链接。当状态值仅等于2或3时，存在链路。
                 * */
                String downloadUri = (String) jsonObj.get("url");
                System.out.println("====文档编辑完成，现在开始保存编辑后的文档，其下载地址为:" + downloadUri);
                //解析得出文件名
                String fileName = downloadUri.substring(downloadUri.lastIndexOf('/') + 1);
                System.out.println("====下载的文件名:" + fileName);

                synchronized (this) {
                    URL url = new URL(downloadUri);
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                    InputStream stream = connection.getInputStream();

                    File savedFile = new File(filePath);
                    try (FileOutputStream out = new FileOutputStream(savedFile)) {
                        int read;
                        final byte[] bytes = new byte[1024];
                        while ((read = stream.read(bytes)) != -1) {
                            out.write(bytes, 0, read);
                        }
                        out.flush();
                    }
                    connection.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
         * status = 1，我们给onlyoffice的服务返回{"error":"0"}的信息，这样onlyoffice会认为回调接口是没问题的，这样就可以在线编辑文档了，否则的话会弹出窗口说明
         * */
        writer.write("{\"error\":0}");
//        writer.print("<script language='javascript'>alert(\"保存成功"  + "\");</script>");
    }

    /**
     * 根据makeupInterval判断是否触发job
     * @param templates
     * @return boolean
     */
    private boolean isTriggerJob(List<ReportCategoryTemplate> templates) {
        long nextTriggerTime = 0L;
        long currentTime = 0L;
        int makeupInterval = 5;
        long timeStamp = 0L;
        Date currentDate = new Date();
        boolean isTrigger = false;

        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();

            if (templates != null && templates.size() > 0) {
                ReportCategoryTemplate reportTemplate = templates.get(0);
                cronTriggerImpl.setCronExpression(reportTemplate.getCron());
                makeupInterval = reportTemplate.getMakeupInterval();
            }

            // 获取makeupInterval对应的时间戳
            timeStamp = makeupInterval * 60 * 1000;

            // 获取此job下次执行时间
            List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 1);
            Date nextDate = new Date();
            if (dates != null && dates.size() > 0) {
                nextDate = dates.get(0);
            }

            // 如果下次定时任务执行时间 - 当前时间 > makeupInterval 对应的时间，则表示需要触发一次job
            nextTriggerTime = nextDate.getTime();
            currentTime = currentDate.getTime();
            isTrigger = nextTriggerTime - currentTime > timeStamp;

        } catch (ParseException e) {
            log.error("根据makeupInterval判断是否触发job执行出现错误", e);
        }

        return isTrigger;
    }

}