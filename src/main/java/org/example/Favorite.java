package org.example;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favorite {
    private Client client;
    private List<Dish> favoriteDishes;
}
