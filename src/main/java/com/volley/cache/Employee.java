package com.volley.cache;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class Employee {
    private String id;

    private String firstName;

    private String lastName;

    private String joinedDate;

    private BigDecimal salary;


}
