package com.cisdi.steel.module.report.enums;

/**
 * <p>Description:   语言类型      </p>
 * <p>email: ypasdf@163.com</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <P>Date: 2018/10/23 </P>
 *
 * @author leaf
 * @version 1.0
 */
public enum LanguageEnum {
    /**
     * 中文
     */
    cn_zh("cn_zh", "cn_zh"),
    /**
     * 英文
     */
    en("en", "en"),
    /**
     * 中国-台湾
     */
    cn_tw("cn_tw", "cn_tw"),
    /**
     * 无
     */
    none("", "");

    /**
     * 所属语言编码
     */
    private String lang;

    /**
     * 生成的目录名称
     */
    private String name;

    LanguageEnum(String lang, String name) {
        this.lang = lang;
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取 指定的语言 类型
     *
     * @param lang 编码
     * @return 结果
     */
    public static LanguageEnum getByLang(String lang) {
        LanguageEnum[] values = LanguageEnum.values();
        for (LanguageEnum value : values) {
            if (value.getLang().equals(lang)) {
                return value;
            }
        }
        // 默认无 防止错误
        return LanguageEnum.none;
    }

    @Override
    public String toString() {
        return this.lang;
    }
}
