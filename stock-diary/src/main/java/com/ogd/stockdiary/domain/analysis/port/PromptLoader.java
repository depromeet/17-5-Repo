package com.ogd.stockdiary.domain.analysis.port;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class PromptLoader {
    private final String prompt;

    public PromptLoader(ResourceLoader resourceLoader) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:market-analysis.txt");
        try (InputStream is = resource.getInputStream()) {
            this.prompt = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
