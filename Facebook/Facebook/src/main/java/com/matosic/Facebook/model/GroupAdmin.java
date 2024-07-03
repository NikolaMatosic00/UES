package com.matosic.Facebook.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Slf4j
public class GroupAdmin extends Administrator {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;

    public void someMethod() {
        log.info("Executing someMethod in GroupAdmin class");
        // method logic here
    }
    // Additional fields and methods for GroupAdmin
}

