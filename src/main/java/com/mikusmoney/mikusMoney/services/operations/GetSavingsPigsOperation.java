package com.mikusmoney.mikusMoney.services.operations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mikusmoney.mikusMoney.dto.savingsDTOs.SavingsPigResponse;
import com.mikusmoney.mikusMoney.entity.Miku;
import com.mikusmoney.mikusMoney.entity.SavingsPig;
import com.mikusmoney.mikusMoney.mapper.SavingsPigMapper;
import com.mikusmoney.mikusMoney.repository.SavingsPigRepository;
import com.mikusmoney.mikusMoney.services.AuthContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetSavingsPigsOperation implements SavingsOperation<Void, List<SavingsPigResponse>> {

    private final SavingsPigRepository savingsPigRepository;
    private final SavingsPigMapper savingsPigMapper;
    private final AuthContextService authContextService;

    @Override
    public List<SavingsPigResponse> execute(Void request) {
        // VALIDATE AUTH
        Miku miku = authContextService.getAuthenticatedMiku();
        
        List<SavingsPig> pigsList = savingsPigRepository.findPigsByMikuId(miku.getId());

        return pigsList.stream()
                .map(savingsPigMapper::toResponse)
                .toList();
    }
    
}
