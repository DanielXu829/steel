package com.cisdi.steel.common.util.math;

import java.math.BigDecimal;

/**
 * 1、double 金额计算 千亿内
 * 2、BigDecimal安全加减乘除  默认 四舍五入
 * <p>Description:  金额计算 </p>
 * <p>email: ypasdf@163.com </p>
 * <p>Copyright: Copyright (c) 2018 </p>
 * <P>Date: 2018/3/25 </P>
 *
 * @author common
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class NumberArithmeticUtils {

    /**
     * double 的小数位数
     */
    private static final int DEF_DIV_SCALE = 10;
    /**
     * 默认保留2位小数
     */
    private static final int SCALE = 2;

    /**
     * BigDecimal的加法运算封装 SCALE位
     *
     * @param b1 参数1
     * @param bn 参数2
     * @return 结果
     */
    public static BigDecimal safeAdd(BigDecimal b1, BigDecimal... bn) {
        if (null == b1) {
            b1 = BigDecimal.ZERO;
        }
        if (null != bn) {
            for (BigDecimal b : bn) {
                b1 = b1.add(null == b ? BigDecimal.ZERO : b);
            }
        }
        return b1.setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Integer加法运算的封装
     *
     * @param b1 第一个数
     * @param bn 需要加的加法数组
     * @return 结果
     * @注 ： Optional  是属于com.google.common.base.Optional<T> 下面的class
     */
    public static Integer safeAdd(Integer b1, Integer... bn) {
        if (null == b1) {
            b1 = 0;
        }
        Integer r = b1;
        if (null != bn) {
            for (Integer b : bn) {
                r += ((b == null)? 0 : b);
            }
        }
        return r > 0 ? r : 0;
    }

    /**
     * 计算金额方法 减法
     *
     * @param b1 参数1
     * @param bn 参数2
     * @return 结果
     */
    public static BigDecimal safeSubtract(BigDecimal b1, BigDecimal... bn) {
        return safeSubtract(true, b1, bn);
    }

    /**
     * BigDecimal的安全减法运算
     *
     * @param isZero 减法结果为负数时是否返回0，true是返回0（金额计算时使用），false是返回负数结果
     * @param b1     被减数
     * @param bn     需要减的减数数组
     * @return 结果
     */
    public static BigDecimal safeSubtract(Boolean isZero, BigDecimal b1, BigDecimal... bn) {
        if (null == b1) {
            b1 = BigDecimal.ZERO;
        }
        BigDecimal r = b1;
        if (null != bn) {
            for (BigDecimal b : bn) {
                r = r.subtract((null == b ? BigDecimal.ZERO : b));
            }
        }
        BigDecimal result = isZero ? (r.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : r) : r;
        return result.setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 整型的减法运算，小于0时返回0
     *
     * @param b1 参数1
     * @param bn 多个整数
     * @return 结果
     */
    public static Integer safeSubtract(Integer b1, Integer... bn) {
        if (null == b1) {
            b1 = 0;
        }
        Integer r = b1;
        if (null != bn) {
            for (Integer b : bn) {
                r -= ((b == null)? 0 : b);
            }
        }
        return null != r && r > 0 ? r : 0;
    }

    /**
     * 金额除法计算，返回2位小数（具体的返回多少位大家自己看着改吧）
     *
     * @param b1 参数1
     * @param b2 参数2
     * @return 结果
     */
    public static <T extends Number> BigDecimal safeDivide(T b1, T b2) {
        return safeDivide(b1, b2, BigDecimal.ZERO);
    }

    /**
     * BigDecimal的除法运算封装，如果除数或者被除数为0，返回默认值
     * 默认返回小数位后2位，用于金额计算
     *
     * @param b1           参数1
     * @param b2           参数2
     * @param defaultValue 默认值
     * @return 结果
     */
    public static <T extends Number> BigDecimal safeDivide(T b1, T b2, BigDecimal defaultValue) {
        if (null == b1 || null == b2) {
            return defaultValue;
        }
        try {
            return BigDecimal.valueOf(b1.doubleValue()).divide(BigDecimal.valueOf(b2.doubleValue()), SCALE, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * BigDecimal的乘法运算封装 保留2位
     *
     * @param b1 参数1
     * @param b2 参数2
     * @return 结果
     */
    public static <T extends Number> BigDecimal safeMultiply(T b1, T b2) {
        if (null == b1 || null == b2) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(b1.doubleValue()).multiply(BigDecimal.valueOf(b2.doubleValue())).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * * 两个Double数相加 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double add(Double v1, Double v2) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null) {
            v2 = 0d;
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.add(b2).doubleValue());
    }

    /**
     * * 两个Double数相减 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double sub(Double v1, Double v2) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null) {
            v2 = 0d;
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.subtract(b2).doubleValue());
    }

    /**
     * * 两个Double数相乘 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double mul(Double v1, Double v2) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null) {
            v2 = 0d;
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.multiply(b2).doubleValue());
    }

    /**
     * * 两个Double数相除 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double div(Double v1, Double v2) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null) {
            v2 = 0d;
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP)
                .doubleValue());
    }

    /**
     * * 两个Double数相除，并保留scale位小数 *
     *
     * @param v1    *
     * @param v2    *
     * @param scale *
     * @return Double
     */
    public static Double div(Double v1, Double v2, int scale) {
        if (v1 == null) {
            v1 = 0d;
        }
        if (v2 == null) {
            v2 = 0d;
        }
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be quartz positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    /**
     * 四舍五入保留两位小数
     *
     * @param v
     * @return
     */
    public static Double rounding2(Double v) {
        if (v == null) {
            v = 0d;
        }
        BigDecimal b = new BigDecimal(v);
        return b.setScale(SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 将金额转为大写
     *
     * @param input
     * @return
     */
    public static String numToChinese(String input) {
        String s1 = "零壹贰叁肆伍陆柒捌玖";
        String s4 = "分角整元拾佰任万拾佰任亿拾佰任";
        String temp = "";
        String result = "";
        if (input == null) {
            return "输入字串不是数字串只能包括以下字符('0'-'9')，输入字串最大只能精确到仟亿，小数点只能两位！";
        }
        temp = input.trim();
        float f;
        try {
            f = Float.parseFloat(temp);
        } catch (Exception e) {
            return "输入字串不是数字串只能包括以下字符('0'-'9')，输入字串最大只能精确到仟亿，小数点只能两位！";
        }
        int len = 0;
        if (temp.indexOf(".") == -1) {
            len = temp.length();
        } else {
            len = temp.indexOf(".");
        }
        if (len > s4.length() - 3) {
            return "输入字串最大只能精确到仟亿，小数点只能两位！";
        }
        int n1, n2 = 0;
        String num = "";
        String unit = "";
        for (int i = 0; i < temp.length(); i++) {
            if (i > len + 2) {
                break;
            }
            if (i == len) {
                continue;
            }
            n1 = Integer.parseInt(String.valueOf(temp.charAt(i)));
            num = s1.substring(n1, n1 + 1);
            n1 = len - i + 2;
            unit = s4.substring(n1, n1 + 1);
            result = result.concat(num).concat(unit);
        }

        if ((len == temp.length()) || (len == temp.length() - 1)) {
            result = result.concat("整");
        }
        if (len == temp.length() - 2) {
            result = result.concat("零分");
        }
        return result;
    }

}