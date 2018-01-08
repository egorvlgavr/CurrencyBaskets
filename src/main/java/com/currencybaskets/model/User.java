package com.currencybaskets.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private Long groupId;
    private String name;
    private String surname;
    private String color;
}
