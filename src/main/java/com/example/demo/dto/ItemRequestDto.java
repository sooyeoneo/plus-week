package com.example.demo.dto;

import lombok.Getter;

@Getter
public class ItemRequestDto {
    private String name;
    private String description;
    private Long managerId;
    private Long ownerId;
}
