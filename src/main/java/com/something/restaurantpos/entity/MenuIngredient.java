package com.something.restaurantpos.entity;

import com.something.restaurantpos.entity.base.AuditMetadata;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MenuIngredientId.class)
public class MenuIngredient extends AuditMetadata {

    @Id
    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @Id
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private BigDecimal quantity;
}
