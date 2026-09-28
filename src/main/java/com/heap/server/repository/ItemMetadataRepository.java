package com.heap.server.repository;

import com.heap.server.entity.ItemMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemMetadataRepository extends JpaRepository<ItemMetadata, Long> {
}