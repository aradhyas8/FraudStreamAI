package com.realtimefraud.fraud.controller;

import com.realtimefraud.fraud.service.TalkerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TalkerController {

    private final TalkerService talkerService;

    public TalkerController(TalkerService talkerService) {
        this.talkerService = talkerService;
    }

    @GetMapping("/api/talk")
    public String talk() {
        return talkerService.talk();
    }
}
