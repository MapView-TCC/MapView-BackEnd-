package com.MapView.BackEnd.dtos.AccessHistory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.time.LocalDateTime;

public record AccessHistoryCreateDTO(@Positive(message = "user id cannot be negative.")
                                     @Min(1)
                                     Long user_id,
                                     LocalDateTime logout_dateTime
) {
}
