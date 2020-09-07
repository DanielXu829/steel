package com.cisdi.steel.module.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

@Data
@Accessors(chain = true)
@Builder(toBuilder = true)
public class WordTitleConfigDTO {
    private String title;
    private String text;
    private String color;
    private ParagraphAlignment paragraphAlignment; // 位置
    private Boolean isBold;
    private Integer fontSize;
    private String fontFamily;

    public static WordTitleConfigDTO getDefaultWordTitleConfigDTO() {
        WordTitleConfigDTO wordTitleConfigDTO = WordTitleConfigDTO.builder()
                .color("000000")
                .fontFamily("")
                .fontSize(24)
                .isBold(true)
                .paragraphAlignment(ParagraphAlignment.CENTER)
                .build();
        return wordTitleConfigDTO;
    }
}
