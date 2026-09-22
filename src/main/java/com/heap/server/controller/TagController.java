package com.heap.server.controller;

import com.heap.server.entity.Tag;
import com.heap.server.repository.TagRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public List<Tag> listTags() {
        return tagRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tag createTag(@RequestBody CreateTagRequest request) {
        return tagRepository.findByName(request.name())
            .orElseGet(() -> {
                Tag tag = new Tag();
                tag.setName(request.name());
                return tagRepository.save(tag);
            });
    }

    public record CreateTagRequest(@NotBlank String name) {}
}
