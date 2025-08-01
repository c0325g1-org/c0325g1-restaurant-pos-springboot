package com.something.restaurantpos.controller;

import com.something.restaurantpos.service.impl.QrCodeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
@Controller
public class QrCodeController {
    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping("/qr-code")
    public void qrCode(@RequestParam("text") String text, HttpServletResponse response) throws IOException {
        byte[] qrImage = qrCodeService.generateQRCode(text, 150, 150);
        response.setContentType("image/png");
        response.getOutputStream().write(qrImage);
        response.getOutputStream().flush();
    }
}
