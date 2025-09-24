package com.ogd.stockdiary.domain.report.port.out;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PromptLoader {

    private final String prompt;

    public PromptLoader(ResourceLoader resourceLoader)throws Exception{

        Resource resource = resourceLoader.getResource("classpath:RetrospectionForReportPrompt.txt");

        this.prompt = new String(resource.getInputStream().readAllBytes(), "UTF-8");

    }
}
