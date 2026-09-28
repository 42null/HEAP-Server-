package com.heap.server.dto;

import com.heap.server.entity.ItemStatus;
import com.heap.server.entity.ItemType;
import java.time.LocalDate;
import java.util.List;

public record ItemRequest(
    ItemType type,
    String title,
    String notes,
    LocalDate dueDate,
    Integer priority,
    ItemStatus status,       // optional — defaults to BACKLOG
    List<String> tagNames,
    DailyDetailsPayload daily,
    String createdBy,        // optional — defaults to "api-user"
    String source            // optional — defaults to "api"
) {}