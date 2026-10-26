package com.heap.server.controller;

import com.heap.server.dto.ItemRequest;
import com.heap.server.dto.ItemResponse;
import com.heap.server.entity.*;
import com.heap.server.repository.ItemRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/items")
@Transactional
public class ItemController {

    private final ItemRepository itemRepository;
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public List<ItemResponse> listItems() {
        return itemRepository.findAllByOrderByPriorityDescDueDateAscIdAsc().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return toResponse(item);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody ItemRequest request) {
        Item item = new Item();
        // item.setType(request.type());
        item.setTitle(request.title());
        item.setNotes(request.notes());
        item.setDueDate(request.dueDate());
        item.setPriority(request.priority());
        // item.setStatus(request.status() != null ? request.status() : ItemStatus.BACKLOG);

        Item savedItem = itemRepository.save(item);
        return toResponse(savedItem);
    }

    @PutMapping("/{id}/title")
    public ItemResponse updateTitle(@PathVariable Long id, @RequestBody String title) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        item.setTitle(title);
        Item savedItem = itemRepository.save(item);
        return toResponse(savedItem);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        itemRepository.deleteById(id);
    }
    private ItemResponse toResponse(Item item) {
        return new ItemResponse(
            item.getId(), item.getType(), item.getTitle(), item.getNotes(),
            item.getDueDate(), item.getPriority(), item.getStatus(),
            // item.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
            null, item.getUpdatedAt(), null, null, null, null
        );
    }
}