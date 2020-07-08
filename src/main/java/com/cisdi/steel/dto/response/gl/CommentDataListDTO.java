package com.cisdi.steel.dto.response.gl;

import com.cisdi.steel.dto.response.gl.res.CommentData;
import lombok.Data;

import java.util.List;

@Data
public class CommentDataListDTO {
    private List<CommentData> data;
}
