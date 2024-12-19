package com.example.demo.dto;

import com.example.demo.entity.Item;

public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;

    public ItemResponseDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
    }
}
