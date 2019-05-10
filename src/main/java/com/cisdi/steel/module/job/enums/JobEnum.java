package com.cisdi.steel.module.job.enums;

import com.cisdi.steel.module.job.util.date.DateQuery;

/**
 * <p>Description:  所有工作编码      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 * gl:代表高炉
 * jh:代表焦化
 * sj:代表烧结
 * ygl:代表原供料
 * nj:代表能介
 * hb:代表环保
 *
 * @author leaf
 * @version 1.0
 */
public enum JobEnum {
    // 高炉
    gl_chutiezonglan("gl_chutiezonglan", "出铁总览"),
    gl_gaolubuliao("gl_gaolubuliao", "高炉布料"),
    gl_gaolubuliao6("gl_gaolubuliao6", "高炉布料"),
    gl_gaolubuliao7("gl_gaolubuliao7", "高炉布料"),
    gl_zhongdianbuweicanshu("gl_zhongdianbuweicanshu", "重点部位参数监控报表"),
    gl_zhongdianbuweicanshutubiao("gl_zhongdianbuweicanshutubiao", "重点部位参数监控报表-图表"),
    gl_xiaohao_day("gl_xiaohao_day", "高炉消耗月报表"),
    gl_lugangwendu_day("gl_lugangwendu_day", "炉缸温度日报表"),
    gl_lugangwendu_month("gl_lugangwendu_month", "炉缸温度月报表"),
    gl_chutiezuoye_day("gl_chutiezuoye_day", "出铁作业日报表"),
    gl_chutiezuoye_month("gl_chutiezuoye_month", " 出铁作业月报表"),
    gl_bentilushenjingya_day("gl_bentilushenjingya_day", " 高炉本体炉身静压 日报"),
    gl_bentilushenjingya_month("gl_bentilushenjingya_month", " 高炉本体炉身静压 月报"),
    gl_bentiwendu_day("gl_bentiwendu_day", "高炉本体温度日报表"),
    gl_bentiwendu_month("gl_bentiwendu_month", " 高炉本体温度 月报"),
    gl_lengquebiwendu_day("gl_lengquebiwendu_day", "高炉冷却壁温度日报表"),
    gl_lengquebiwendu_month("gl_lengquebiwendu_month", "高炉冷却壁温度 月报"),
    gl_ludingbuliao_day("gl_ludingbuliao_day", "炉顶装料作业日报表"),
    gl_ludingzhuangliaozuoye_day1("gl_ludingzhuangliaozuoye_day1", "炉顶装料作业日报表"),
    gl_jswgaolu_day("gl_jswgaolu_day", "6高炉 日报"),
    gl_taisu1_month("gl_taisu1_month", "6高炉 月报"),
    gl_refenglu_month("gl_refenglu_month", "热风炉 月报"),
    gl_refenglu_day("gl_refenglu_day", "热风炉 日报"),
    gl_peiliaodan("gl_peiliaodan", "配料单"),
    gl_peiliaodan6("gl_peiliaodan6", "配料单"),
    gl_peiliaodan7("gl_peiliaodan7", "配料单"),
    gl_gaolupenmei("gl_gaolupenmei", "高炉喷煤运行报表"),
    gl_gaolupenmei6("gl_gaolupenmei6", "6高炉喷煤运行报表"),
    gl_gaolupenmei7("gl_gaolupenmei7", "7高炉喷煤运行报表"),
    gl_jingya_day("gl_jingya_day", "高炉静压日报"),
    gl_jingya_month("gl_jingya_month", "高炉静压月报"),
    gl_guankongzhibiao("gl_guankongzhibiao", "高炉重点管控指标"),
    gl_bf6gongyicanshu("gl_bf6gongyicanshu", "6高炉工艺参数跟踪"),
    gl_caoyehui_day("gl_caoyehui_day", "高炉操业会议"),
    gl_tuosifuyang("gl_tuosifuyang", "6高炉脱湿富氧操作报表"),
    gl_zhuangliaochuchen("gl_zhuangliaochuchen", "装料除尘操作报表"),
    gl_refenglujiankong("gl_refenglujiankong", "6高炉热风炉设备监控日报"),
    gl_zhouliuyasuoji("gl_zhouliuyasuoji", "轴流压缩机操作报表AV50"),
    gl_zhouliuyasuoji_two("gl_zhouliuyasuoji_two", "轴流压缩机操作报表AV63"),
    gl_qimixiang_day("gl_qimixiang_day", "上料日报"),
    gl_6gyicanshu_export("gl_6gyicanshu_export", "6高炉工艺参数导出"),
    gl_7gyicanshu_export("gl_7gyicanshu_export", "7高炉工艺参数导出"),
    gl_8gyicanshu_export("gl_8gyicanshu_export", "8高炉工艺参数导出"),
    gl_lglqbjcs_day("gl_lglqbjcs_day", "高炉炉缸冷却壁进出水日报"),
    gl_lglqbjcs_month("gl_lglqbjcs_month", "高炉炉缸冷却壁进出水月报"),
    gl_lqbjcs_month("gl_lqbjcs_month", "高炉冷却壁进出水量月报"),

    // 焦化
    jh_zidongpeimei("jh_zidongpeimei", "配煤-自动配煤报表（班）"),
    jh_fensuixidu("jh_fensuixidu", "配煤-粉碎细度报表（月）"),
    jh_cdqcaozuoa("jh_cdqcaozuoa", "干熄焦-CDQ操作运行报表A（日）"),
    jh_cdqcaozuob("jh_cdqcaozuob", "干熄焦-CDQ操作运行报表B（日）"),
    jh_chujiaochuchen("jh_chujiaochuchen", "出焦除尘报表"),
    jh_zhuangmeichuchen("jh_zhuangmeichuchen", "装煤除尘报表"),
    jh_cdqchuchen("jh_cdqchuchen", "CDQ除尘报表"),
    jh_shaijiaochuchen("jh_shaijiaochuchen", "干熄焦-筛焦除尘报表（日）"),
    jh_gufenglengning1("jh_gufenglengning1", "化产-鼓风冷凝报表（一）（日）"),
    jh_gufenglengning2("jh_gufenglengning2", "化产-鼓风冷凝报表（二）（日）"),
    jh_zhilengxunhuanshui("jh_zhilengxunhuanshui", "化产-制冷循环水报表（日）"),
    jh_zhengan("jh_zhengan", "化产-蒸氨报表（日）"),
    jh_liuan("jh_liuan", "化产-硫铵报表（日）"),
    jh_chubenzhengliu("jh_chubenzhengliu", "化产-粗苯蒸馏报表（日）"),
    jh_zhonglengxiben("jh_zhonglengxiben", "化产-终冷洗苯报表（日）"),
    jh_tuoliujiexi("jh_tuoliujiexi", "化产-脱硫解吸（日）报表设计"),
    jh_zhisuancaozuo("jh_zhisuancaozuo", "化产-制酸操作报表（日）"),
    jh_lianjiaoyuebao("jh_lianjiaoyuebao", "炼焦-月报表报表（日&月）"),
    jh_jiaolujiare6("jh_jiaolujiare6", "炼焦-6#焦炉加热制度报表（日）"),
    jh_jiaolujiare7("jh_jiaolujiare7", "炼焦-7#焦炉加热制度报表（日）"),
    jh_luwenjilu6("jh_luwenjilu6", "炼焦-6#炉温记录报表（日）"),
    jh_luwenjilu7("jh_luwenjilu7", "炼焦-7#炉温记录报表（日）"),
    jh_jlguanjianzhibiao("jh_jlguanjianzhibiao", "炼焦-6#-7#焦炉关键指标统计"),
    jh_peimeiliang("jh_peimeiliang", "配煤-配煤量月报表"),
    jh_zhibiaoguankong("jh_zhibiaoguankong", "炼焦-6#-7#焦炉关键指标管控"),
    jh_zhuyaogycs("jh_zhuyaogycs", "炼焦-主要工艺参数"),
    jh_luwenguankong("jh_luwenguankong", "炼焦-炉温管控"),
    jh_chanhaozonghe("jh_chanhaozonghe", "化产-产耗综合报表"),

    // 烧结
    sj_tuoliu("sj_tuoliu", "脱硫系统运行日报"),
    sj_gengzongbiao("sj_gengzongbiao", "五烧六烧主抽电耗跟踪表"),
    sj_tuoliutuoxiaogongyicaiji("sj_tuoliutuoxiaogongyicaiji", "脱硫脱硝工艺参数采集"),
    sj_tuoxiaoyunxingjilu("sj_tuoxiaoyunxingjilu", "脱硝运行记录表"),
    sj_shaojieji_day("sj_shaojieji_day", "烧结机生产日报"),
    sj_shaojieji_month("sj_shaojieji_month", "烧结机生产月报"),
    sj_liushaogycanshu("sj_liushaogycanshu", "4小时发布-主要工艺参数及实物质量情况日报"),
    sj_caoyehui_day("sj_caoyehui_day", "烧结每日操业会"),
    sj_rongji("sj_rongji", "熔剂燃料质量管控"),
    sj_tuoliutuoxiao_year("sj_tuoliutuoxiao_year", "脱硫脱硝生产运行年报表"),
    sj_gycanshutotal("sj_gycanshutotal", "烧结分厂主要工艺参数及实物质量情况"),
    sj_huanbaojiankong_day("sj_huanbaojiankong_day", "烧结公辅环保设施运行情况及在线监测数据发布"),
    sj_gongzuoliushuizhang("sj_gongzuoliushuizhang", "工作流水账"),
    sj_yujizuoyequ("sj_yujizuoyequ", "烧结生产作业区雨季生产记录表"),
    sj_gongzuoliushuizhang6("sj_gongzuoliushuizhang6", "工作流水账"),
    sj_yujizuoyequ6("sj_yujizuoyequ6", "烧结生产作业区雨季生产记录表"),
    sj_jingyiguankong5("sj_jingyiguankong5", "5烧结精益生产管控系统"),
    sj_jingyiguankong6("sj_jingyiguankong6", "6烧结精益生产管控系统"),
    sj_hunhejiashuizhengqi5_month("sj_hunhejiashuizhengqi5_month", "烧结混合机加水蒸汽预热温度统计表"),
    sj_hunhejiashuizhengqi6_month("sj_hunhejiashuizhengqi6_month", "烧结混合机加水蒸汽预热温度统计表"),
    sj_huanliaoqingkuang5_month("sj_huanliaoqingkuang5_month", "缓料情况记录表"),
    sj_huanliaoqingkuang6_month("sj_huanliaoqingkuang6_month", "缓料情况记录表"),
    sj_gyicanshu5_export("sj_gyicanshu5_export", "5烧结工艺参数导出"),
    sj_gyicanshu6_export("sj_gyicanshu6_export", "6烧结工艺参数导出"),
    sj_wuzhituoliu_month("sj_wuzhituoliu_month", "56脱硫运行记录"),
    sj_gyijiancha_month("sj_gyijiancha_month", "工艺检查项目"),

    // 原供料
    gl_chejianwuliaowaipai("ygl_chejianwuliaowaipai", "供料车间物料外排统计表"),
    gl_chejianjikongzhongxinjioajieban("ygl_chejianjikongzhongxinjioajieban", "供料车间集控中心交接班记录"),
    ygl_shaixiafentongji_day("ygl_shaixiafentongji_day", "筛下粉统计"),
    ygl_zhongjiaowaipai_month("ygl_zhongjiaowaipai_month", "中焦外排记录"),
    ygl_meitouwaipai_month("ygl_meitouwaipai_month", "煤头外排记录"),
    ygl_gongliaochejian_month("ygl_gongliaochejian_month", "供料车间运输车辆统计_录入"),
    ygl_Liaojiaomei_day("ygl_Liaojiaomei_day", "炼焦煤每日库存动态表"),
    ygl_chengpincang("ygl_chengpincang", "成品仓出入记录"),
    ygl_yichanggenzong("ygl_yichanggenzong", "供料异常跟踪表"),
    ygl_shengchanxiechedegji("ygl_shengchanxiechedegji", "生产卸车登记表"),
    ygl_jinchangwuzi("ygl_jinchangwuzi", "进厂物资（精煤）化验记录表"),
    yl_chejianshengchanyunxing("ygl_chejianshengchanyunxing", "原料车间生产运行记录表"),
    yl_chejianshengchanjiaoban("ygl_chejianshengchanjiaoban", "原料车间生产交班表"),

    // 能介
    nj_qiguidianjian("nj_qiguidianjian", "气柜点检表"),
    nj_diaojianoneKong_day("nj_diaojianoneKong_day", "能源环保部一空压站设备日点检表"),
    nj_diaojiantwoKong_day("nj_diaojiantwoKong_day", "能源环保部二空压站设备日点检表"),
    nj_diaojianthreeKong_day("nj_diaojianthreeKong_day", "能源环保部三空压站设备日点检表"),
    nj_diaojianfourKong_day("nj_diaojianfourKong_day", "能源环保部四空压站设备日点检表"),
    nj_qiguidianjianruihua_month("nj_qiguidianjianruihua_month", "气柜区润滑台帐表格"),
    nj_kongqiya_month("nj_kongqiya_month", "空压站设备给油脂标准及加油记录"),
    nj_twokong("nj_twokong", "二空压站运行记录表"),
    nj_threekong("nj_threekong", "三空压站运行记录表"),
    nj_fourkong("nj_fourkong", "四空压站运行记录表"),
    nj_xinyikong("nj_xinyikong", "新一空压站运行记录表"),
    nj_sansigui_day("nj_sansigui_day", "三四柜区运行记录表"),

    nj_onekongcount("nj_onekongcount", "一空压站启停次数表"),
    nj_twokongcount("nj_twokongcount", "二空压站启停次数表"),
    nj_threekongcount("nj_threekongcount", "三空压站启停次数表"),
    nj_fourkongcount("nj_fourkongcount", "四空压站启停次数表"),
    nj_yasuokongqi("nj_yasuokongqi", "压缩空气生产情况汇总表"),
    nj_meiqihunhemei("nj_meiqihunhemei", "煤气柜作业区混合煤气情况表"),
    nj_guifengjimeiyaji("nj_guifengjimeiyaji", "柜区风机煤压机时间统计表"),
    nj_dongli_month("nj_dongli_month", "动力分厂主要设备开停机信息表"),
    nj_meiqihunhemeisd_month("nj_meiqihunhemeisd_month", "煤气柜作业区混合煤气情况表-人工录入"),
    nj_jiepailing_day("nj_jiepailing_day", "界牌岭运行日志"),
    nj_gongluyinsutongji_month("nj_gongluyinsutongji_month", "功率因素统计表"),
    nj_jidu_year("nj_jidu_year", "能介年度报表"),

    // 环保
    hb_6bftrt("hb_6bftrt", "6BF-TRT日报表"),
    hb_7bftrt("hb_7bftrt", "7BF-TRT日报表"),
    hb_meiqichuchen6bf("hb_meiqichuchen6bf", "6BF-煤气布袋除尘报表"),
    hb_meiqichuchen7bf("hb_meiqichuchen7bf", "7BF-煤气布袋除尘报表"),
    hb_meiqichuchen8bf("hb_meiqichuchen8bf", "8BF-煤气布袋除尘报表"),
    hb_8bftrt("hb_8bftrt", "8BF-TRT日报表");


    private String code;
    private String name;

    private DateQuery dateQuery;

    JobEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

}
