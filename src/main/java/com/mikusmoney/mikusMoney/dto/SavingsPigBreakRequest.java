package com.mikusmoney.mikusMoney.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsPigBreakRequest {

    @NotNull(message = "Miku ID is required")
    @Positive(message = "Miku ID must be positive")
    private Long mikuId;

    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^\\d{4,6}$", message = "PIN code must be 4-6 digits")
    private String pinCode;
}
