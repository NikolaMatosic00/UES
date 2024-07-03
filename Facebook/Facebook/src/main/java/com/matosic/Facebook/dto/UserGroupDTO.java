package com.matosic.Facebook.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserGroupDTO {
    private String name;
    private String description;
    private MultipartFile descriptionPdf;
    private Long adminId;
}
