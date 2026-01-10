package com.mikusmoney.mikusMoney.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mikusmoney.mikusMoney.dto.savingsDTOs.SavingsPigCreationRequest;
import com.mikusmoney.mikusMoney.dto.savingsDTOs.SavingsPigResponse;
import com.mikusmoney.mikusMoney.services.SavingsPigService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v0.1/savings/pig/")
@RequiredArgsConstructor
public class SavingsPigController {
    
    private final SavingsPigService savingsPigService;

    @PostMapping("create")
    public ResponseEntity<SavingsPigResponse> createSavingsPig(@Valid @RequestBody SavingsPigCreationRequest request) {
        
        return ResponseEntity.ok(savingsPigService.createSavingsPig(request));
    }

    @GetMapping("all")
    public ResponseEntity<List<SavingsPigResponse>> getAllSavingsPigs() {
        return ResponseEntity.ok(savingsPigService.getSavingsPigs());
    }
}
