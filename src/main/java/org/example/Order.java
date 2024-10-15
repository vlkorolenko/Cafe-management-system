package org.example;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int id;
    private Client client;
    private Employee employee;
    private List<Dish> dishes;
    private LocalDate orderDate;
    private BigDecimal totalPrice;
}