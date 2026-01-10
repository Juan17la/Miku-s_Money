package com.mikusmoney.mikusMoney.services.operations;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.mikusmoney.mikusMoney.dto.savingsDTOs.SavingsPigCreationRequest;
import com.mikusmoney.mikusMoney.dto.savingsDTOs.SavingsPigResponse;
import com.mikusmoney.mikusMoney.entity.Miku;
import com.mikusmoney.mikusMoney.entity.SavingsPig;
import com.mikusmoney.mikusMoney.mapper.SavingsPigMapper;
import com.mikusmoney.mikusMoney.repository.SavingsPigRepository;
import com.mikusmoney.mikusMoney.services.AuthContextService;
import com.mikusmoney.mikusMoney.validations.SavingsValidations;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateSavingsPigOperation implements SavingsOperation<SavingsPigCreationRequest, SavingsPigResponse> {
    
    private final AuthContextService authContextService;
    private final SavingsPigRepository savingsPigRepository;
    private final SavingsPigMapper savingsPigMapper;
    private final SavingsValidations savingsValidations;
    
    @Override
    public SavingsPigResponse execute(SavingsPigCreationRequest request) {
        
        // VALIDATE AUTH
        Miku miku = authContextService.getAuthenticatedMiku();

        // VALIDATIONS
        savingsValidations.createPigValidation(miku.getId(), request.getGoal());

        SavingsPig savingsPig = savingsPigMapper.toEntity(request);
        savingsPig.setMiku(miku);
        SavingsPig savedPig = savingsPigRepository.save(savingsPig);
        
        return savingsPigMapper.toResponse(savedPig);
    }
    
}
