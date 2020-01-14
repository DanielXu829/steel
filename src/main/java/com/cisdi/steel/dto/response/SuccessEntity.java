package com.cisdi.steel.dto.response;

import lombok.Data;

@Data
public class SuccessEntity<T> {
    private T data;
}
