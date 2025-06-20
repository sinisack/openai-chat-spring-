package com.example.openapi.service.impl;

import com.example.openapi.config.ChatGPTConfig;
import com.example.openapi.dto.ChatRequestDto;
import com.example.openapi.service.ChatGPTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatGPTServiceImpl implements ChatGPTService {

    private final ChatGPTConfig chatGPTConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("${openai.model}")
    private String model;

    public ChatGPTServiceImpl(ChatGPTConfig chatGPTConfig) {
        this.chatGPTConfig = chatGPTConfig;
    }

    @Override
    public List<Map<String, Object>> modelList() {
        log.debug("[+] 모델 리스트를 조회합니다.");
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        ResponseEntity<String> response = chatGPTConfig.restTemplate().exchange(
                "https://api.openai.com/v1/models",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), new TypeReference<>() {});
            return (List<Map<String, Object>>) responseMap.get("data");
        } catch (JsonProcessingException e) {
            log.error("모델 리스트 파싱 실패", e);
            throw new RuntimeException("모델 리스트 파싱 실패", e);
        }
    }

    @Override
    public Map<String, Object> isValidModel(String modelName) {
        log.debug("[+] 모델 유효성 확인: {}", modelName);
        HttpHeaders headers = chatGPTConfig.httpHeaders();

        ResponseEntity<String> response = chatGPTConfig.restTemplate().exchange(
                "https://api.openai.com/v1/models/" + modelName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("모델 응답 파싱 실패", e);
            throw new RuntimeException("모델 유효성 파싱 실패", e);
        }
    }

    @Override
    public Map<String, Object> prompt(ChatRequestDto chatRequestDto) {
        log.debug("[+] chat-completions 프롬프트 요청 수행");

        HttpHeaders headers = chatGPTConfig.httpHeaders();

        //
        List<Map<String, String>> updatedMessages = new ArrayList<>();
        updatedMessages.add(Map.of(
                "role", "system",
                "content", "모든 응답은 반드시 한국어로 작성해주세요." +
                        "코드와 관련 된 질문만 받고 설명해주세요." +
                        "코드와 관련 되지 않는 질문을 하면 '코드와 관련되지 않는 질문은 받지 않고 있습니다.'라는 문구를 출력해 주세요" +
                        "사용자를 위해 가독성 있게 작성해 주세요."
        ));
        updatedMessages.addAll(chatRequestDto.getMessages());


        ChatRequestDto requestDto = ChatRequestDto.builder()
                .model(model)
                .messages(updatedMessages)
                .temperature(
                        chatRequestDto.getTemperature() > 0 ? chatRequestDto.getTemperature() : 0.1f
                )
                .build();

        try {
            String requestBody = objectMapper.writeValueAsString(requestDto);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = chatGPTConfig.restTemplate().exchange(
                    openAiApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("프롬프트 처리 실패: {}", e.getMessage());
            throw new RuntimeException("chat-completion 처리 중 오류", e);
        }
    }
}
