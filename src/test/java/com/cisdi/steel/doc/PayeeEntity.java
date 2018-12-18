package com.cisdi.steel.doc;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.List;

@Data
public class PayeeEntity {

    @Excel(name = "全称")
    private String name;

    @Excel(name = "银行账号")
    private String bankAccount;

    @Excel(name = "开户银行")
    private String bankName;

    private int index;

    private List<ChartValue> chartValues;

    public PayeeEntity(String name, String bankAccount, String bankName) {
        super();
        this.name = name;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
    }

    public PayeeEntity(String name, String bankAccount, String bankName,
                       int index) {
        super();
        this.name = name;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.index = index;
    }
}