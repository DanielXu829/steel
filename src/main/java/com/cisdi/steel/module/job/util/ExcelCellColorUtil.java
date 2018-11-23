package com.cisdi.steel.module.job.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.util.*;

public class ExcelCellColorUtil {
    /**
     * 对cell进行分组
     *
     * @param cellList 单元格
     * @return 结果
     */
    public static Map<String, List<Cell>> groupByCell(List<Cell> cellList, Map<String, String> maps, String defaultUrl) {
        Map<String, List<Cell>> result = new HashMap<>();

        cellList.forEach(item -> {
            Color fore = item.getCellStyle().getFillForegroundColorColor();
            String rgbColor = getRgbColor(fore);
            String url = maps.getOrDefault(rgbColor, defaultUrl);
            List<Cell> list = result.get(url);
            if (Objects.isNull(list)) {
                list = new ArrayList<>();
                list.add(item);
                result.put(url, list);
            } else {
                list.add(item);
            }
        });
        return result;
    }

    public static String getRgbColor(Color color) {
        if (Objects.nonNull(color) && (color instanceof XSSFColor)) {
            XSSFColor a = (XSSFColor) color;
            return getStringRGB(a.getRGB());
        }
        return null;
    }

    public static String getStringRGB(byte[] rgb) {
        StringBuilder sb = new StringBuilder();
        for (byte c : rgb) {
            int i = c & 0xff;
            String cs = Integer.toHexString(i);
            if (cs.length() == 1) {
                sb.append('0');
            }
            sb.append(cs);
        }
        return sb.toString();
    }
}
