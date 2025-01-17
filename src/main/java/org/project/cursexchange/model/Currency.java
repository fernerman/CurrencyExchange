package org.project.cursexchange.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Currency {
    private long id;
    private String code;
    private String name;
    private String sign;
}
