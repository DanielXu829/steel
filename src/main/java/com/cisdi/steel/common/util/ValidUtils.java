package com.cisdi.steel.common.util;

/**
 * <p>Description: 验证规则 (fork)  </p>
 * <p>email: ypasdf@163.com </p>
 * <p>Copyright: Copyright (c) 2018 </p>
 * <P>Date: 2018/3/25 </P>
 *
 * @author common
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ValidUtils {

    /**
     * 手机号（修改后）
     */
    private static final String mobile = "^(1)[0-9]{10}$";

    /**
     * 手机号 验证（修改后）
     */
    private static final String codeAndMobile = "^\\+[0-9]{2}\\-(1)[0-9]{10}$";

    /**
     * 整数
     */
    private static final String intege = "^-?[1-9]\\d*$";
    /**
     * 正整数
     */
    private static final String intege1 = "^[1-9]\\d*$";
    /**
     * 负整数
     */
    private static final String intege2 = "^-[1-9]\\d*$";
    /**
     * 数字
     */
    private static final String num = "^([+-]?)\\d*\\.?\\d+$";
    /**
     * 正数（正整数 + 0）
     */
    private static final String num1 = "^[1-9]\\d*|0$";
    /**
     * 负数（负整数 + 0）
     */
    private static final String num2 = "^-[1-9]\\d*|0$";
    /**
     * 浮点数
     */
    private static final String decmal = "^([+-]?)\\d*\\.\\d+$";
    /**
     * 正浮点数
     */
    private static final String decmal1 = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$";
    /**
     * 负浮点数
     */
    private static final String decmal2 = "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
    /**
     * 浮点数
     */
    private static final String decmal3 = "^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$";
    /**
     * 非负浮点数（正浮点数 + 0）
     */
    private static final String decmal4 = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$";
    /**
     * 非正浮点数（负浮点数 + 0）
     */
    private static final String decmal5 = "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$";
    /**
     * 邮件
     */
    private static final String email = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
    /**
     * 颜色
     */
    private static final String color = "^[quartz-fA-F0-9]{6}$";
    /**
     * url
     */
    private static final String url = "^http[s]?=\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$";
    /**
     * 仅中文
     */
    private static final String chinese = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
    /**
     * 仅ACSII字符
     */
    private static final String ascii = "^[\\x00-\\xFF]+$";
    /**
     * 邮编
     */
    private static final String zipcode = "^\\d{6}$";
    /**
     * ip地址
     */
    private static final String ip4 = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
    /**
     * 非空
     */
    private static final String notempty = "^\\S+$";
    /**
     * 图片
     */
    private static final String picture = "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$";
    /**
     * 压缩文件
     */
    private static final String rar = "(.*)\\.(rar|zip|7zip|tgz)$";
    /**
     * 日期
     */
    private static final String date = "^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$";
    /**
     * QQ号码
     */
    private static final String qq = "^[1-9]*[1-9][0-9]*$";
    /**
     * 电话号码的函数(包括验证国内区号;国际区号;分机号)
     */
    private static final String tel = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{1,}))?$";
    /**
     * 用来用户注册。匹配由数字、26个英文字母或者下划线组成的字符串
     */
    private static final String username = "^\\w+$";
    /**
     * 字母
     */
    private static final String letter = "^[A-Za-z]+$";
    /**
     * 字符串包含空格
     */
    private static final String letterAndSpace = "^[A-Za-z ]+$";
    /**
     * 大写字母
     */
    private static final String letter_u = "^[A-Z]+$";
    /**
     * 小写字母
     */
    private static final String letter_l = "^[quartz-z]+$";
    /**
     * 身份证
     */
    private static final String idcard = "^[1-9]([0-9]{14}|[0-9]{17})$";
    /**
     * 判断字符串是否为浮点数
     */
    private static final String isFloat = "^[-]?\\d+(\\.\\d+)?$";
    /**
     * 判断字符串是否为正浮点数
     */
    private static final String isUFloat = "^\\d+(\\.\\d+)?$";
    /**
     * 判断是否是整数
     */
    private static final String isInteger = "^[-]?\\d+$";
    /**
     * 判断是否是正整数
     */
    private static final String isUInteger = "^\\d+$";
    /**
     * 判断车辆Vin码
     */
    private static final String isCarVin = "^[1234567890WERTYUPASDFGHJKLZXCVBNM]{13}[0-9]{4}$";

    /**
     * 手机号
     */
    public static boolean isMobile(String input) {
        return matches(mobile, input);
    }

    /**
     * 是否手机号 086 11位手机号
     */
    public static boolean isCodeAndMobile(String input) {
        return matches(codeAndMobile, input);
    }

    /**
     * 整数
     */
    public static boolean isIntege(String input) {
        return matches(intege, input);
    }

    /**
     * 正整数
     */
    public static boolean isintege1(String input) {
        return matches(intege1, input);
    }

    /**
     * 负整数
     */
    public static boolean isIntege2(String input) {
        return matches(intege2, input);
    }

    /**
     * 数字
     */
    public static boolean isNum(String input) {
        return matches(num, input);
    }

    /**
     * 正数（正整数 + 0）
     */
    public static boolean isNum1(String input) {
        return matches(num1, input);
    }

    /**
     * 负数（负整数 + 0）
     */
    public static boolean isNum2(String input) {
        return matches(num2, input);
    }

    /**
     * 浮点数
     */
    public static boolean isDecmal(String input) {
        return matches(decmal, input);
    }

    /**
     * 正浮点数
     */
    public static boolean isDecmal1(String input) {
        return matches(decmal1, input);
    }

    /**
     * 负浮点数
     */
    public static boolean isDecmal2(String input) {
        return matches(decmal2, input);
    }

    /**
     * 浮点数
     */
    public static boolean isDecmal3(String input) {
        return matches(decmal3, input);
    }

    /**
     * 非负浮点数（正浮点数 + 0）
     */
    public static boolean isDecmal4(String input) {
        return matches(decmal4, input);
    }

    /**
     * 非正浮点数（负浮点数 + 0）
     */
    public static boolean isDecmal5(String input) {
        return matches(decmal5, input);
    }

    /**
     * 邮件
     */
    public static boolean isEmail(String input) {
        return matches(email, input);
    }

    /**
     * 颜色
     */
    public static boolean isColor(String input) {
        return matches(color, input);
    }

    /**
     * url
     */
    public static boolean isUrl(String input) {
        return matches(url, input);
    }

    /**
     * 中文
     */
    public static boolean isChinese(String input) {
        return matches(chinese, input);
    }

    /**
     * ascii码
     */
    public static boolean isAscii(String input) {
        return matches(ascii, input);
    }

    /**
     * 邮编
     */
    public static boolean isZipcode(String input) {
        return matches(zipcode, input);
    }

    /**
     * IP地址
     */
    public static boolean isIP4(String input) {
        return matches(ip4, input);
    }

    /**
     * 非空
     */
    public static boolean isNotEmpty(String input) {
        return matches(notempty, input);
    }

    /**
     * 图片
     */
    public static boolean isPicture(String input) {
        return matches(picture, input);
    }

    /**
     * 压缩文件
     */
    public static boolean isRar(String input) {
        return matches(rar, input);
    }

    /**
     * 日期
     */
    public static boolean isDate(String input) {
        return matches(date, input);
    }

    /**
     * qq
     */
    public static boolean isQQ(String input) {
        return matches(qq, input);
    }

    /**
     * 电话号码的函数(包括验证国内区号;国际区号;分机号)
     */
    public static boolean isTel(String input) {
        return matches(tel, input);
    }

    /**
     * 用来用户注册。匹配由数字、26个英文字母或者下划线组成的字符串
     */
    public static boolean isUserName(String input) {
        return matches(username, input);
    }

    /**
     * 字母
     */
    public static boolean isLetter(String input) {
        return matches(letter, input);
    }

    /**
     * 字符串 包含空格
     */
    public static boolean isLetterAndSpace(String input) {
        return matches(letterAndSpace, input);
    }

    /**
     * 小写字母
     */
    public static boolean isLowLetter(String input) {
        return matches(letter_l, input);
    }

    /**
     * 大写字母
     */
    public static boolean isUpperLetter(String input) {
        return matches(letter_u, input);
    }

    /**
     * 身份证
     */
    public static boolean isIDCard(String input) {
        return matches(idcard, input);
    }

    /**
     * 是否 float
     */
    public static boolean isFloat(String input) {
        return matches(isFloat, input);
    }

    /**
     * 判断字符串是否为正浮点数
     * @param input
     * @return
     */
    public static boolean isUFloat(String input) {
        return matches(isUFloat, input);
    }

    /**
     * 判断是否是整数
     */
    public static boolean isInteger(String input) {
        return matches(isInteger, input);
    }

    /**
     * 判断是否是正整数
     */
    public static boolean isUInteger(String input) {
        return matches(isUInteger, input);
    }

    /**
     * 判断车辆Vin码
     * @param carVin
     * @return
     */
    public static boolean isCarVin(String carVin) {
        return matches(isCarVin, carVin);
    }

    /**
     * 匹配
     * @param regex 规则
     * @param input 数据
     * @return 结果
     */
    public static boolean matches(String regex, String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }
        if (input.matches(regex)) {
            return true;
        }
        return false;
    }
}
