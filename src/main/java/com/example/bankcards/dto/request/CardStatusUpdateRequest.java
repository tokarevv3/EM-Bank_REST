package com.example.bankcards.dto.request;

import com.example.bankcards.entity.Status;
import com.example.bankcards.util.validation.annotation.AllowedStatus;
import lombok.Value;

@Value
public class CardStatusUpdateRequest {

    @AllowedStatus(message = "Статус может быть только ACTIVE или BLOCKED")
    Status status;
}
