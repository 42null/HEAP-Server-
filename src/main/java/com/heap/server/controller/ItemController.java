package com.heap.server.controller;

import com.heap.server.dto.DailyDetailsPayload;
import com.heap.server.dto.ItemRequest;
import com.heap.server.dto.ItemResponse;
import com.heap.server.entity.DailyDetails;
import com.heap.server.entity.Item;
import com.heap.server.entity.ItemType;
import com.heap.server.entity.Tag;
import com.heap.server.repository.DailyDetailsRepository;
import com.heap.server.repository.ItemRepository;
import com.heap.server.repository.TagRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;
    private final DailyDetailsRepository dailyDetailsRepository;
    private final TagRepository tagRepository;

    public ItemController(ItemRepository itemRepository,
                           DailyDetailsRepository dailyDetailsRepository,
                           TagRepository tagRepository) {
        this.itemRepository = itemRepository;
        this.dailyDetailsRepository = dailyDetailsRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public List<ItemResponse> listItems() {
        return itemRepository.findAllByOrderByPriorityDescDueDateAscCreatedAtAsc()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return toResponse(item);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody ItemRequest request) {
        if (request.type() == ItemType.DAILY && request.daily() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "daily details are required when type = DAILY");
        }

        Item item = new Item();
        applyRequest(item, request);
        item = itemRepository.save(item);

        if (request.type() == ItemType.DAILY) {
            saveDailyDetails(item, request.daily());
        }

        return toResponse(itemRepository.findById(item.getId()).orElseThrow());
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        applyRequest(item, request);
        item = itemRepository.save(item);

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

    // --- helpers ---

    private void applyRequest(Item item, ItemRequest request) {
        item.setType(request.type());
        item.setTitle(request.title());
        item.setNotes(request.notes());
        item.setDueDate(request.dueDate());
        item.setPriority(request.priority());
        item.setDone(request.done() != null && request.done());
        item.setTags(resolveTags(request.tagNames()));
    }

    private Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames == null) {
            return tags;
        }
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
        DailyDetails details = dailyDetailsRepository.findById(item.getId())
            .orElseGet(DailyDetails::new);
        details.setItem(item);
        details.setNotifyTime(payload.notifyTime());
        details.setRepeatsMonday(payload.monday());
        details.setRepeatsTuesday(payload.tuesday());
        details.setRepeatsWednesday(payload.wednesday());
        details.setRepeatsThursday(payload.thursday());
        details.setRepeatsFriday(payload.friday());
        details.setRepeatsSaturday(payload.saturday());
        details.setRepeatsSunday(payload.sunday());
        // streak_count / last_completed_date are managed by completion logic,
        // not overwritten wholesale on every edit - keep existing values if present.
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

        return new ItemResponse(
            item.getId(),
            item.getType(),
            item.getTitle(),
            item.getNotes(),
            item.getDueDate(),
            item.getPriority(),
            item.isDone(),
            item.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
            dailyPayload,
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }
}
