package vttp.proj2.backend.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.proj2.backend.dtos.LinkCodeDTO;
import vttp.proj2.backend.dtos.TelegramMessageDTO;
import vttp.proj2.backend.services.TelegramBotService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
public class TelegramController {

    private final TelegramBotService telegramBot;

    @Autowired
    public TelegramController(TelegramBotService telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping("/telegram/store-link-code")
    public ResponseEntity<?> storeLinkCode(@RequestBody LinkCodeDTO request) {
        System.out.println("TelegramController: storeLinkCode endpoint was reached");
        System.out.println("LinkCodeDTO " + request);
        boolean stored = telegramBot.storeLinkCode(request.getLinkCode(), request.getUserId());
        Map<String, String> resp = new HashMap<>();
        if (stored) {
            resp.put("message", "🟢 Link code stored successfully");
            return ResponseEntity.ok().body(resp);
        } else {
            resp.put("message", "🔴 Failed to store link code");
            return ResponseEntity.badRequest().body(resp);
        }
    }
}
