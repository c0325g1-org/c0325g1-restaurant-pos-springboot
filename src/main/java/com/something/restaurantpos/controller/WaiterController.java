package com.something.restaurantpos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/waiter")
public class WaiterController {

    @GetMapping
    public String dashboard() {
        return "pages/waiter/dashboard";
    }
}
