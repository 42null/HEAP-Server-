package com.heap.server.repository;

import com.heap.server.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOrderByPriorityDescDueDateAscIdAsc();
}