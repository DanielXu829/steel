package com.cisdi.steel.common.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 *
 * @author chen
 * @date 2017/8/16
 * <p>
 * Email 122741482@qq.com
 * <p>
 * Describe: 拼音工具类
 */
@SuppressWarnings("ALL")
public class PinyinUtils {

    /**
     * 获取第一个字符串拼音的第一个字母
     *
     * @param chinese 中文
     * @return 结果
     */
    public static String toFirstStringChar(String chinese) {
        String pinyinStr = "#";
        if (StringUtils.isEmpty(chinese)) {
            return pinyinStr;
        }
        chinese = chinese.substring(0, 1);
        return toFirstChar(chinese);
    }


    /**
     * 获取所有字符串拼音的第一个字母
     *
     * @param chinese 中文
     * @return 结果
     */
    public static String toFirstChar(String chinese) {
        StringBuilder pinyinStr = new StringBuilder();
        //转为单个字符
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char aNewChar : newChar) {
            if (aNewChar > 128) {
                try {
                    pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(aNewChar, defaultFormat)[0].charAt(0));
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr.append(aNewChar);
            }
        }
        pinyinStr = new StringBuilder(pinyinStr.toString().toUpperCase());
        return pinyinStr.toString();
    }

    /**
     * 汉字转为拼音
     *
     * @param chinese 中文
     * @return 结果
     */
    public static String toPinyin(String chinese) {
        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char aNewChar : newChar) {
            if (aNewChar > 128) {
                try {
                    pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(aNewChar, defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr.append(aNewChar);
            }
        }
        return pinyinStr.toString();
    }
}
