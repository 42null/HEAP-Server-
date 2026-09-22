package com.heap.server.dto;

import com.heap.server.entity.ItemType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ItemResponse(
    Long id,
    ItemType type,
    String title,
    String notes,
    LocalDate dueDate,
    Integer priority,
    boolean done,
    List<String> tags,
    DailyDetailsPayload daily,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
