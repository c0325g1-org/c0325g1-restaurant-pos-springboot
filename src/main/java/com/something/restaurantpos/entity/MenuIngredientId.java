package com.something.restaurantpos.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuIngredientId implements Serializable {
    private Integer menuItem;
    private Integer ingredient;
}
