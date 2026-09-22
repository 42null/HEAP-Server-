package com.heap.server.repository;

import com.heap.server.entity.DailyDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyDetailsRepository extends JpaRepository<DailyDetails, Long> {
}
