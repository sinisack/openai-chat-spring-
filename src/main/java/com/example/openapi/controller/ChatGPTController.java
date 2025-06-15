package com.example.openapi.controller;

import com.example.openapi.dto.ChatRequestDto;
import com.example.openapi.service.ChatGPTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/v1/chatGpt")
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    /**
     * 사용 가능한 ChatGPT 모델 목록을 조회합니다.
     */
    @GetMapping("/modelList")
    public ResponseEntity<List<Map<String, Object>>> selectModelList() {
        List<Map<String, Object>> result = chatGPTService.modelList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 특정 모델 이름이 유효한지 확인합니다.
     * 예: /model?modelName="모델명"
     */
    @GetMapping("/model")
    public ResponseEntity<Map<String, Object>> isValidModel(@RequestParam(name = "modelName") String modelName) {
        Map<String, Object> result = chatGPTService.isValidModel(modelName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * ChatGPT에게 프롬프트를 보내고 응답을 반환합니다.
     */
    @PostMapping("/prompt")
    public ResponseEntity<Map<String, Object>> selectPrompt(@RequestBody ChatRequestDto chatRequestDto) {
        Map<String, Object> result = chatGPTService.prompt(chatRequestDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
