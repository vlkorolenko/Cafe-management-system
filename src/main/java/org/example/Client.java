package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private int id;
    private String fullname;
    private LocalDate dateOfBirth;

    public Client(String name, LocalDate dateOfBirth) {}

    public Client(int id, String fullname) {
        this.id = id;
        this.fullname = fullname;
    }
}