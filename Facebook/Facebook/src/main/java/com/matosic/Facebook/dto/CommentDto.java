package com.matosic.Facebook.dto;

import com.matosic.Facebook.model.Comment;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private LocalDate timestamp;
    private boolean isDeleted;
    private Long userId; // ID korisnika koji je napisao komentar
    private Long parentId; // ID roditeljskog komentara (ako postoji)

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.timestamp = comment.getTimestamp();
        this.isDeleted = comment.isDeleted();
        this.userId = comment.getUser().getId();
        
        // Postavljanje roditeljskog komentara ako postoji
        if (comment.getParentComment() != null) {
            this.parentId = comment.getParentComment().getId();
        }
    }
}
