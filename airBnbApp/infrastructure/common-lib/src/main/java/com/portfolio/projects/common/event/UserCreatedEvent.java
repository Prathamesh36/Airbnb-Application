package com.portfolio.projects.common.event;

import com.portfolio.projects.common.enums.Gender;
import com.portfolio.projects.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedEvent {
    private Long id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
    private Set<Role> roles;
}
