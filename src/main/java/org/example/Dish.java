package org.example;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dish {
    private int id;
    private String type;
    private String name;
    private BigDecimal price;
    private LocalDate expiryDate;

    public Dish(String type, String name, LocalDate expiryDate, BigDecimal price) {
        this.type = type;
        this.name = name;
        this.expiryDate = expiryDate;
        this.price = price;
    }
    public Dish(int id,String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public void printDetails() {
        System.out.println("ID: " + id);
        System.out.println("Тип: " + type);
        System.out.println("Назва: " + name);
        System.out.println("Ціна: " + price);
        System.out.println("Термін придатності: " + expiryDate);
    }

}
