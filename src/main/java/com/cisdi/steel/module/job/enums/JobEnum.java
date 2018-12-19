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
 * gl:代表供料
 * yl:代表原料
 * ygl:代表原供料
 *
 * @author leaf
 * @version 1.0
 */
public enum JobEnum {
    // 6高炉
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


    // 焦化
//    jh_peimeizuoyequ("jh_peimeizuoyequ", "配煤作业区报表设计"),
//    jh_huachan("jh_huachan", "化产报表设计"),
//    jh_ganxijiao("jh_ganxijiao", "干熄焦报表设计"),
//    jh_shaojiao("jh_shaojiao", "炼焦报表设计"),
//    jh_lianjiaoluwen("jh_lianjiaoluwen", "炼焦炉温报表设计"),
    jh_zidongpeimei("jh_zidongpeimei", "CK67-自动配煤（班）报表"),
    jh_fensuixidu("jh_fensuixidu", "CK67-粉碎细度（月）报表"),
    jh_cdqcaozuo("jh_cdqcaozuo", "CK67-CDQ操作运行报表（日）报表"),
    jh_chujiaochuchen("jh_chujiaochuchen", "CK67-出焦除尘报表"),
    jh_zhuangmeichuchen("jh_zhuangmeichuchen", "CK67-装煤除尘报表"),
    jh_cdqchuchen("jh_cdqchuchen", "CK67-CDQ除尘报表"),
    jh_shaijiaochuchen("jh_shaijiaochuchen", "CK67-筛焦除尘报表"),
    jh_gufenglengning("jh_gufenglengning", "CK67-鼓风冷凝(日)报表"),
    jh_zhilengxunhuanshui("jh_zhilengxunhuanshui", "CK67-制冷循环水（日）报表"),
    jh_zhengan("jh_zhengan", "CK67-蒸氨（日）报表"),
    jh_liuan("jh_liuan", "CK67-硫铵（日）报表"),
    jh_chubenzhengliu("jh_chubenzhengliu", "CK67-粗苯蒸馏（日）报表"),
    jh_zhonglengxiben("jh_zhonglengxiben", "CK67-终冷洗苯报表"),
    jh_tuoliujiexi("jh_tuoliujiexi", "CK67-脱硫解吸（日）"),
    jh_zhisuancaozuo("jh_zhisuancaozuo", "CK67-制酸操作（日）"),
    jh_lianjiaoribao("jh_lianjiaoribao", "CK67-炼焦日报表（班日、月）报表"),
    jh_jiaolujiare6("jh_jiaolujiare6", "CK67-6#焦炉加热制度表（日）报表"),
    jh_jiaolujiare7("jh_jiaolujiare7", "CK67-7#焦炉加热制度表（日）报表"),
    jh_luwenjilu6("jh_luwenjilu6", "CK67-6#炉温记录报表"),
    jh_luwenjilu7("jh_luwenjilu7", "CK67-7#炉温记录报表"),

    // 烧结
    sj_tuoliu("sj_tuoliu", "脱硫系统运行日报"),
    sj_gengzongbiao("sj_gengzongbiao", "五烧六烧主抽电耗跟踪表"),
    sj_tuoliutuoxiaogongyicaiji("sj_tuoliutuoxiaogongyicaiji", "脱硫脱硝工艺参数采集"),
    sj_tuoxiaoyunxingjilu("sj_tuoxiaoyunxingjilu", "脱硝运行记录表"),
    sj_shaojieji_day("sj_shaojieji_day", "烧结机生产日报"),
    sj_liushaogycanshu("sj_liushaogycanshu", "4小时发布-主要工艺参数及实物质量情况日报"),

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
    nj_guifengjimeiyaji("nj_guifengjimeiyaji", "柜区风机煤压机时间统计表");

    private String code;
    private String name;

    private DateQuery dateQuery;

    // TODO: 暂时保留 上面写完后 删除
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
