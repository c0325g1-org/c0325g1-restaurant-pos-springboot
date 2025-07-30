package com.something.restaurantpos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class VNPayComBackController {
    @GetMapping("/vnpay_return")
    @ResponseBody
    public String vnpayReturn(@RequestParam Map<String, String> params) {
        return "VNPAY response: " + params.toString();
    }
}
