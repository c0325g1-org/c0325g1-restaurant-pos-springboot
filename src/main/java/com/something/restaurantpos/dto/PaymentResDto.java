package com.something.restaurantpos.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class PaymentResDto implements Serializable {
    private String status;
    private String message;
    private String URL;

}
