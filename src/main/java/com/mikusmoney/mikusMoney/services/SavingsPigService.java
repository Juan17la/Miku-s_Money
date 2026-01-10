package com.mikusmoney.mikusMoney.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mikusmoney.mikusMoney.dto.savingsDTOs.SavingsPigCreationRequest;
import com.mikusmoney.mikusMoney.dto.savingsDTOs.SavingsPigResponse;
import com.mikusmoney.mikusMoney.services.operations.CreateSavingsPigOperation;
import com.mikusmoney.mikusMoney.services.operations.GetSavingsPigsOperation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingsPigService {
    

    private final CreateSavingsPigOperation createSavingsPig;
    private final GetSavingsPigsOperation getSavingsPigsOperation;
    
    public SavingsPigResponse createSavingsPig(SavingsPigCreationRequest request) {
        return createSavingsPig.execute(request);
    }

    public List<SavingsPigResponse> getSavingsPigs() {
        return getSavingsPigsOperation.execute(null);
    }
}
