package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import lombok.Value;

@Value
public class CardStatusUpdateDto {

    Status status;

    public boolean isAllowedForUpdate() {
        return status == Status.ACTIVE || status == Status.BLOCKED;
    }
}
