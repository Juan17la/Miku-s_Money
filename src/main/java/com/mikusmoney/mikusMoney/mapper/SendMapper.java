package com.mikusmoney.mikusMoney.mapper;

import com.mikusmoney.mikusMoney.dto.SendMoneyRequest;
import com.mikusmoney.mikusMoney.dto.SendResponse;
import com.mikusmoney.mikusMoney.entity.Send;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SendMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "sender", ignore = true) // Will be set in service
    @Mapping(target = "receiver", ignore = true) // Will be set in service
    Send toEntity(SendMoneyRequest request);

    @Mapping(target = "senderMikuId", source = "sender.id")
    @Mapping(target = "senderFullName", source = "sender.fullName")
    @Mapping(target = "receiverMikuId", source = "receiver.id")
    @Mapping(target = "receiverFullName", source = "receiver.fullName")
    SendResponse toResponse(Send send);
}
