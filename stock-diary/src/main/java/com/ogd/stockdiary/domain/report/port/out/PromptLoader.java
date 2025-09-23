package com.ogd.stockdiary.domain.report.port.out;

import org.springframework.core.io.ResourceLoader;

public class PromptLoader {

    public PromptLoader(ResourceLoader resourceLoader)throws Exception{
        resourceLoader.getResource("classpath:RetrospectionForReportPrompt.txt").getInputStream();
    }
}
