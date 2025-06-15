package com.example.openapi.service;

import com.example.openapi.dto.ChatRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public interface ChatGPTService {
    List<Map<String, Object>> modelList();
    Map<String, Object> prompt(ChatRequestDto chatRequestDto);
    Map<String, Object> isValidModel(String modelName);
}