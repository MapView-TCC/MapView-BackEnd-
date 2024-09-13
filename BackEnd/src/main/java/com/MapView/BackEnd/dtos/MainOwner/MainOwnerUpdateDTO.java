package com.MapView.BackEnd.dtos.MainOwner;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record MainOwnerUpdateDTO(
                                 String owner_name,
                                 @Min(value = 1, message = "CostCenter Id cannot be smaller than 0.")
                                 @Positive(message = "CostCenter Id must be Positive.")
                                 Long id_cost_center) {
}