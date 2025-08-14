package com.dpm05.common.application.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SampleController {
    @PostMapping("/sample")
    public String sampleEndpoint(
            @RequestBody List<Integer> requestBody
    ) {
        return "Sample endpoint response";
    }
}
