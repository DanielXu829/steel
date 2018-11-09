package com.cisdi.steel.module.job.enums;

/**
 * <p>Description:  所有工作编码      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/24 </P>
 * gl:代表高炉
 * jh:代表焦化
 * sj:代表烧结
 * gl:代表供料
 * yl:代表原料
 * ygl:代表原供料
 *
 * @author leaf
 * @version 1.0
 */
public enum JobEnum {
    // 高炉
    gl_chutiezuoye_day("gl_chutiezuoye_day", "出铁作业日报表"),
    gl_chutiezuoye_month("gl_chutiezuoye_month", " 出铁作业月报表"),
    gl_bentilushenjingya_day("gl_bentilushenjingya_day", " 高炉本体炉身静压 日报"),
    gl_bentilushenjingya_month("gl_bentilushenjingya_month", " 高炉本体炉身静压 月报"),
    gl_bentiwendu_day("gl_bentiwendu_day", "高炉本体温度日报表"),
    gl_bentiwendu_month("gl_bentiwendu_month", " 高炉本体温度 月报"),
    gl_lengquebiwendu_day("gl_lengquebiwendu_day", "高炉冷却壁温度日报表"),
    gl_lengquebiwendu_month("gl_lengquebiwendu_month", "高炉冷却壁温度 月报"),
    gl_ludingzhuangliaozuoye_day1("gl_ludingzhuangliaozuoye_day1", "炉顶装料作业日报表"),
    gl_ludingzhuangliaozuoye_day2("gl_ludingzhuangliaozuoye_day2", "高炉本体温度 日报2"),
    gl_refenglu_day("gl_refenglu_day", "热风炉 日报"),
    gl_refenglu_month("gl_refenglu_month", "热风炉 月报"),
    gl_jswgaolu_day("gl_jswgaolu_day", "JSW高炉 日报"),
    gl_taisu1_month("gl_taisu1_month", "台塑1高炉 月报"),
    gl_jswzhinengpinghengjisuan("gl_jswzhinengpinghengjisuan", "JSW质能平衡计算报表"),


    // 烧结
    jh_peimeizuoyequ("jh_peimeizuoyequ", "配煤作业区报表设计"),
    jh_huachan("jh_huachan", "化产报表设计"),
    jh_ganxijiao("jh_ganxijiao", "干熄焦报表设计"),
    jh_shaojiao("jh_shaojiao", "炼焦报表设计"),

    // 烧结
    sj_tuoliu("sj_tuoliu", "脱硫报表"),
    sj_gengzongbiao("sj_gengzongbiao", "五烧降低主抽电耗跟踪表"),
    sj_rongjiranliao("sj_tuoxiaoyunxingjilu", "熔剂燃料质量管控"),
    sj_tuoliutuoxiaogongyicaiji("sj_tuoliutuoxiaogongyicaiji", "脱硫脱硝工艺参数采集"),
    sj_tuoxiaoyunxingjilu("sj_tuoxiaoyunxingjilu", "脱硝运行记录表"),

    // 原供料
    gl_chejianwuliaowaipai("gl_chejianwuliaowaipai", "供料车间物料外排统计表"),
    gl_chejianjikongzhongxinjioajieban("gl_chejianjikongzhongxinjioajieban", "供料车间物料外排统计表"),
    gl_yichanggenzong("gl_yichanggenzong", "供料异常跟踪表"),

    yl_duihunyunkuangfenpeibi("yl_duihunyunkuangfenpeibi", "堆混匀矿粉配比通知单"),
    yl_jinchangwuzijingmeihuayan("yl_jinchangwuzijingmeihuayan", "原料进厂物资精煤化验记录表"),
    yl_chejianzhongkongshiyuanshijilu("yl_chejianzhongkongshiyuanshijilu", "原料车间中控室原始记录表"),
    yl_chejianshengchanyunxing("yl_chejianshengchanyunxing", "原料车间生产运行记录表"),
    yl_chejianshengchanjiaoban("yl_chejianshengchanjiaoban", "原料车间生产交班表"),
    yl_shujujilu("yl_shujujilu", "原料数据记录表"),
    yl_hunyunkuangfenA4ganzhuanshi("yl_hunyunkuangfenA4ganzhuanshi", "原料混匀矿粉A4干转湿配比换算计算表");

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
