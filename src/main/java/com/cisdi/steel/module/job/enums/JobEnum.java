package com.cisdi.steel.module.job.enums;

/**
 * <p>Description:  所有工作编码      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum JobEnum {
    // 高炉
    chutiezuoye_day("chutiezuoye_day", " 出铁作业日报表"),
    chutiezuoye_month("chutiezuoye_month", " 出铁作业月报表"),
    gaolubentilushenjingya_day("gaolubentilushenjingya_day", " 高炉本体炉身静压 日报"),
    gaolubentilushenjingya_month("gaolubentilushenjingya_month", " 高炉本体炉身静压 月报"),
    gaolubentiwendu_day("gaolubentiwendu_day", " 高炉本体温度 日报"),
    gaolubentiwendu_month("gaolubentiwendu_month", " 高炉本体温度 月报"),
    gaolulengquebiwendu_day("gaolulengquebiwendu_day", "高炉冷却壁温度 日报"),
    gaolulengquebiwendu_month("gaolulengquebiwendu_month", "高炉冷却壁温度 月报"),
    gaoluludingzhuangliaozuoye_day1("gaoluludingzhuangliaozuoye_day1", "高炉本体温度 日报1"),
    gaoluludingzhuangliaozuoye_day2("gaoluludingzhuangliaozuoye_day2", "高炉本体温度 日报2"),
    refenglu_day("refenglu_day", "热风炉 日报"),
    refenglu_month("refenglu_month", "热风炉 月报"),
    jswgaolu_day("jswgaolu_day", "JSW高炉 日报"),
    taisu1_month("taisu1_month", "台塑1高炉 月报"),
    jswzhinengpinghengjisuan("jswzhinengpinghengjisuan", "JSW质能平衡计算报表"),


    // 烧结
    jh_peimeizuoyequ("jh_peimeizuoyequ", "配煤作业区报表设计"),
    jh_huachan("jh_huachan", "化产报表设计"),
    jh_ganxijiao("jh_ganxijiao","干熄焦报表设计"),
    jh_shaojiao("jh_shaojiao","炼焦报表设计"),

    // 烧结
    sj_tuoliu("sj_tuoliu","脱硫报表"),
    sj_gengzongbiao("sj_gengzongbiao","五烧降低主抽电耗跟踪表"),
    sj_rongjiranliao("sj_tuoxiaoyunxingjilu","熔剂燃料质量管控"),
    sj_tuoliutuoxiaogongyicaiji("sj_tuoliutuoxiaogongyicaiji","脱硫脱硝工艺参数采集"),
    sj_tuoxiaoyunxingjilu("sj_tuoxiaoyunxingjilu","脱硝运行记录表"),

    // 原供料
    gl_chejianwuliaowaipai("gl_chejianwuliaowaipai","供料车间物料外排统计表"),
    gl_chejianjikongzhongxinjioajieban("gl_chejianjikongzhongxinjioajieban","供料车间物料外排统计表"),
    gl_yichanggenzong("gl_yichanggenzong","供料异常跟踪表"),

    yl_duihunyunkuangfenpeibi("yl_duihunyunkuangfenpeibi","堆混匀矿粉配比通知单"),
    yl_jinchangwuzijingmeihuayan("yl_jinchangwuzijingmeihuayan","原料进厂物资精煤化验记录表"),
    yl_chejianzhongkongshiyuanshijilu("yl_chejianzhongkongshiyuanshijilu","原料车间中控室原始记录表"),
    yl_chejianshengchanyunxing("yl_chejianshengchanyunxing","原料车间生产运行记录表"),
    yl_chejianshengchanjiaoban("yl_chejianshengchanjiaoban","原料车间生产交班表"),
    yl_shujujilu("yl_shujujilu","原料数据记录表"),
    yl_hunyunkuangfenA4ganzhuanshi("yl_hunyunkuangfenA4ganzhuanshi","原料混匀矿粉A4干转湿配比换算计算表")

    ;

    private String code;
    private String name;

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
