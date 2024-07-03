package com.matosic.Facebook.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.matosic.Facebook.model.Comment;
import com.matosic.Facebook.service.CommentService;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin("http://localhost:3000/")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@PostMapping("/add")
	public ResponseEntity<Comment> addComment(@RequestParam Long postId, @RequestParam Long userId,
			@RequestParam String text) {
		Comment comment = commentService.addComment(postId, userId, text);
		return ResponseEntity.ok(comment);
	}

	@PostMapping("/reply")
	public ResponseEntity<Comment> replyToComment(@RequestParam Long parentCommentId, @RequestParam Long userId,
			@RequestParam String text) {
		Comment reply = commentService.replyToComment(parentCommentId, userId, text);
		return ResponseEntity.ok(reply);
	}

	@PutMapping("/{commentId}/update")
	public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestParam Long userId,
			@RequestParam String newText) {
		boolean updated = commentService.updateComment(commentId, userId, newText);
		if (updated) {
			return ResponseEntity.ok("Comment updated successfully.");
		} else {
			return ResponseEntity.badRequest().body("Failed to update comment. Check your credentials or comment ID.");
		}
	}
}
