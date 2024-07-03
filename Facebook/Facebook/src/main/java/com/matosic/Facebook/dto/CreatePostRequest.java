package com.matosic.Facebook.dto;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

@Data
public class CreatePostRequest {
	private Long userId;
	private MultipartFile pdfFile;
	private Long userGroupId;
	private String content;
}
