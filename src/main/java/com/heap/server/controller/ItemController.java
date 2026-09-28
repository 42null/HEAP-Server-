// src/main/java/com/heap/server/controller/ItemController.java
package com.heap.server.controller;

import com.heap.server.dto.DailyDetailsPayload;
import com.heap.server.dto.ItemRequest;
import com.heap.server.dto.ItemResponse;
import com.heap.server.entity.*;
import com.heap.server.repository.DailyDetailsRepository;
import com.heap.server.repository.ItemMetadataRepository;
import com.heap.server.repository.ItemRepository;
import com.heap.server.repository.TagRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@Transactional
public class ItemController {

    private final ItemRepository itemRepository;
    private final ItemMetadataRepository itemMetadataRepository;
    private final DailyDetailsRepository dailyDetailsRepository;
private final TagRepository tagRepository;

    public ItemController(ItemRepository itemRepository,
                           ItemMetadataRepository itemMetadataRepository,
                           DailyDetailsRepository dailyDetailsRepository,
                           TagRepository tagRepository) {
        this.itemRepository = itemRepository;
        this.itemMetadataRepository = itemMetadataRepository;
        this.dailyDetailsRepository = dailyDetailsRepository;
        this.tagRepository = tagRepository;
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
        if (request.type() == ItemType.DAILY && request.daily() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "daily details are required when type = DAILY");
        }

        Item item = new Item();
        applyRequest(item, request);
        item = itemRepository.save(item);

        ItemMetadata metadata = new ItemMetadata();
        metadata.setItem(item);
        metadata.setVersion(1L);
        metadata.setCreatedBy(request.createdBy() != null ? request.createdBy() : "api-user");
        metadata.setSource(request.source() != null ? request.source() : "api");
        itemMetadataRepository.save(metadata);

        if (request.type() == ItemType.DAILY) {
            saveDailyDetails(item, request.daily());
        }

        return toResponse(itemRepository.findById(item.getId()).orElseThrow());
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        applyRequest(item, request);
        item = itemRepository.save(item);

        itemMetadataRepository.findById(item.getId()).ifPresent(m -> {
            m.setVersion(m.getVersion() + 1);
            itemMetadataRepository.save(m);
        });

        if (request.type() == ItemType.DAILY) {
            saveDailyDetails(item, request.daily());
        } else {
            dailyDetailsRepository.findById(item.getId()).ifPresent(dailyDetailsRepository::delete);
        }

        return toResponse(itemRepository.findById(item.getId()).orElseThrow());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        itemRepository.deleteById(id);
    }

    private void applyRequest(Item item, ItemRequest request) {
        item.setType(request.type());
        item.setTitle(request.title());
        item.setNotes(request.notes());
        item.setDueDate(request.dueDate());
        item.setPriority(request.priority());
        item.setStatus(request.status() != null ? request.status() : ItemStatus.BACKLOG);
        item.setTags(resolveTags(request.tagNames()));
    }

    private Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames == null) return tags;
        for (String name : tagNames) {
            if (name == null || name.isBlank()) continue;
            Tag tag = tagRepository.findByName(name)
                .orElseGet(() -> {
                    Tag t = new Tag();
                    t.setName(name);
                    return tagRepository.save(t);
                });
            tags.add(tag);
        }
        return tags;
    }

    private void saveDailyDetails(Item item, DailyDetailsPayload payload) {
        DailyDetails details = dailyDetailsRepository.findById(item.getId()).orElseGet(DailyDetails::new);
        details.setItem(item);
        details.setNotifyTime(payload.notifyTime());
        details.setRepeatsMonday(payload.monday());
        details.setRepeatsTuesday(payload.tuesday());
        details.setRepeatsWednesday(payload.wednesday());
        details.setRepeatsThursday(payload.thursday());
        details.setRepeatsFriday(payload.friday());
        details.setRepeatsSaturday(payload.saturday());
        details.setRepeatsSunday(payload.sunday());
        dailyDetailsRepository.save(details);
    }

    private ItemResponse toResponse(Item item) {
        DailyDetailsPayload dailyPayload = null;
        if (item.getType() == ItemType.DAILY) {
            dailyPayload = dailyDetailsRepository.findById(item.getId())
                .map(d -> new DailyDetailsPayload(
                    d.getNotifyTime(),
                    d.isRepeatsMonday(), d.isRepeatsTuesday(), d.isRepeatsWednesday(),
                    d.isRepeatsThursday(), d.isRepeatsFriday(), d.isRepeatsSaturday(),
                    d.isRepeatsSunday(), d.getStreakCount(), d.getLastCompletedDate()))
                .orElse(null);
        }

        ItemMetadata metadata = itemMetadataRepository.findById(item.getId()).orElse(null);

        return new ItemResponse(
            item.getId(), item.getType(), item.getTitle(), item.getNotes(),
            item.getDueDate(), item.getPriority(), item.getStatus(),
            item.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
            dailyPayload, item.getUpdatedAt(),
            metadata != null ? metadata.getCreatedAt() : null,
            metadata != null ? metadata.getCreatedBy() : null,
            metadata != null ? metadata.getSource() : null,
            metadata != null ? metadata.getVersion() : null
        );
    }
}