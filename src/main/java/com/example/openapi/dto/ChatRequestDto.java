package com.example.openapi.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRequestDto {
    private String model;
    private List<Map<String, String>> messages;
    private float temperature;

    @Builder
    public ChatRequestDto(String model, List<Map<String, String>> messages, float temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }
}
