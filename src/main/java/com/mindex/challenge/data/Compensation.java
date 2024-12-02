package com.mindex.challenge.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Compensation {
    private Double salary;
    private String effectiveDate;
}
