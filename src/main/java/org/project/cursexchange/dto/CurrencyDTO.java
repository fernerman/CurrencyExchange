package org.project.cursexchange.dto;

import org.project.cursexchange.model.Currency;

public class CurrencyDTO {

    private String code;
    private String name;
    private String sign;

    public CurrencyDTO(String code, String name, String sign) {
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
    public Currency toEntity() {
        return new Currency(0,this.code,this.name,this.sign);
    }
}
