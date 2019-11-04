package com.cisdi.steel.common.enums;

public enum EditStatusEnum {

    Release(0),

    Locked(1);

    private int editStatus = 0;

    private EditStatusEnum(int value) {

        editStatus = value;
    }

    public int getEditStatus() {

        return editStatus;
    }

}
