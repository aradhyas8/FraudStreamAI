package com.realtimefraud.fraud.service;

import org.springframework.stereotype.Service;

@Service
public class TalkerService {
    private final EarthService earthService;

    public TalkerService(EarthService earthService) {
        this.earthService = earthService;
    }
    public String talk() {
        return "Talker says:" + earthService.getMessage();
    }
}
