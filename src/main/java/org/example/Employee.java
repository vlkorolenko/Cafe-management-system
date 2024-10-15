package org.example;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private int id;
    private String fullName;
    private LocalDate dateOfBirth;

    public Employee(String fullName, LocalDate dateOfBirth) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }
    public Employee(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }
}