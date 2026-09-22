package com.heap.server.dto;

import com.heap.server.entity.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ItemRequest(
    @NotNull ItemType type,
    @NotBlank String title,
    String notes,
    LocalDate dueDate,
    Integer priority,
    Boolean done,
    List<String> tagNames,
    // Required when type = DAILY, ignored otherwise
    DailyDetailsPayload daily
) {}
