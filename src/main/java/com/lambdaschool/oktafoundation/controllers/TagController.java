package com.lambdaschool.oktafoundation.controllers;


import com.lambdaschool.oktafoundation.models.Tag;
import com.lambdaschool.oktafoundation.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@RestController
public class TagController {

	private final TagService tagService;

	@Autowired
	public TagController(TagService tagService) {
		this.tagService = tagService;
	}

	@GetMapping(value = "/tags")
	public ResponseEntity<?> getAll() {
		List<Tag> tags = tagService.getAll();
		return new ResponseEntity<>(tags, HttpStatus.OK);
	}

	@GetMapping(value = "/tags/tag/{tagId}")
	public ResponseEntity<?> get(
			@PathVariable
					Long tagId
	) {
		Tag tag = tagService.get(tagId);
		return new ResponseEntity<>(tag, HttpStatus.OK);
	}

	@GetMapping(value = "/tags/program/{programId}")
	public ResponseEntity<?> getByProgram(
			@PathVariable
					Long programId
	) {
		List<Tag> tags = tagService.getByProgram(programId);
		return new ResponseEntity<>(tags, HttpStatus.OK);
	}

	@GetMapping(value = "/tags/program/name/{programName}")
	public ResponseEntity<?> getByProgram(
			@PathVariable
					String programName
	) {
		List<Tag> tags = tagService.getByProgram(programName);
		return new ResponseEntity<>(tags, HttpStatus.OK);
	}

	@PostMapping(value = "/tags/new-by-program/{programId}")
	public ResponseEntity<?> post(
			@PathVariable
					Long programId,
			@Valid
			@RequestBody
					Tag tag
	) {
		Tag savedTag = tagService.save(tag, programId);
		return new ResponseEntity<>(savedTag, HttpStatus.CREATED);
	}

	@PutMapping("/tags/tag/{tagId}")
	public ResponseEntity<?> replace(
			@PathVariable
					Long tagId,
			@Valid
			@RequestBody
					Tag tag
	) {
		Tag updatedTag = tagService.replace(tag, tagId);
		return new ResponseEntity<>(updatedTag, HttpStatus.OK);
	}

	@PutMapping("/tags/tag")
	public ResponseEntity<?> replace(
			@Valid
			@RequestBody
					Tag tag
	) {
		Tag updatedTag = tagService.replace(tag);
		return new ResponseEntity<>(updatedTag, HttpStatus.OK);
	}

	@PatchMapping("/tags/tag")
	public ResponseEntity<?> edit(
			@RequestBody
					Tag tag
	) {
		Tag updatedTag = tagService.update(tag);
		return new ResponseEntity<>(updatedTag, HttpStatus.OK);
	}

	@PatchMapping(value = "/tags/tag/{tagId}")
	public ResponseEntity<?> edit(
			@PathVariable
					Long tagId,
			@RequestBody
					Tag tag
	) {
		Tag updatedTag = tagService.update(tag, tagId);
		return new ResponseEntity<>(updatedTag, HttpStatus.OK);
	}

	@DeleteMapping("/tags/tag/{tagId}")
	public ResponseEntity<?> delete(
			@PathVariable
					Long tagId
	) {
		tagService.delete(tagId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
