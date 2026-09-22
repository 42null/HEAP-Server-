package com.heap.server.dto;

import java.time.LocalTime;

// Optional block on an ItemRequest/ItemResponse - only present when type = DAILY
public record DailyDetailsPayload(
    LocalTime notifyTime,
    boolean monday,
    boolean tuesday,
    boolean wednesday,
    boolean thursday,
    boolean friday,
    boolean saturday,
    boolean sunday,
    Integer streakCount,
    java.time.LocalDate lastCompletedDate
) {}
