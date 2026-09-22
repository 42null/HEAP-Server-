package com.heap.server.repository;

import com.heap.server.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // Priority sort: nulls last, highest priority first, ties broken by due date then created_at
    List<Item> findAllByOrderByPriorityDescDueDateAscCreatedAtAsc();
}
