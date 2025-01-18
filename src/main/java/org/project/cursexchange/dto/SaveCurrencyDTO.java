package org.project.cursexchange.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SaveCurrencyDTO {
    private String code;
    private String name;
    private String sign;
}
