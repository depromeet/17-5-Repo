// package com.ogd.stockdiary.application.config.batch;
//
// import org.springframework.ai.document.Document;
// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.Step;
// import
// org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
// import org.springframework.batch.core.job.builder.JobBuilder;
// import org.springframework.batch.core.repository.JobRepository;
// import org.springframework.batch.core.step.builder.StepBuilder;
// import org.springframework.batch.item.*;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.task.TaskExecutor;
// import org.springframework.transaction.PlatformTransactionManager;
//
// import com.ogd.stockdiary.domain.stock.entity.Stock;
//
// @Configuration
// @EnableBatchProcessing
// public class BatchConfig {
//
// private final ItemReader reader;
// private final ItemProcessor<Stock, Document> processor;
// private final ItemWriter writer;
// private final TaskExecutor taskExecutor;
//
// public BatchConfig(
// ItemReader<Stock> reader,
// ItemProcessor<Stock, Document> processor,
// ItemWriter<Document> writer,
// @Qualifier("batchTaskExecutor") TaskExecutor taskExecutor) {
//
// this.reader = reader;
// this.processor = processor;
// this.writer = writer;
// this.taskExecutor = taskExecutor;
// }
//
// @Bean
// public ItemReader<Stock> itemReader() {
// return new ItemReader<Stock>() {
// @Override
// public Stock read()
// throws Exception, UnexpectedInputException, ParseException,
// NonTransientResourceException {
// return null;
// }
// }; // 또는 생성자 호출, 또는 여러 구현체
// }
//
// @Bean
// public ItemProcessor<Stock, Document> itemProcessor() {
// return new ItemProcessor<Stock, Document>() {
// @Override
// public Document process(Stock item) throws Exception {
// return null;
// }
// };
// }
// @Bean
// public ItemWriter<Document> itemWriter() {
// return new ItemWriter<Document>() {
// @Override
// public void write(Chunk<? extends Document> chunk) throws Exception {
//
// }
// };
// }
//
// @Bean
// public Job stockDataJob(JobRepository jobRepository, Step step1) {
// return new JobBuilder("stockDataJob", jobRepository)
// .start(step1)
// .build();
// }
//
// @Bean
// @Deprecated(since = "5.0", forRemoval = true)
// public Step stockDataStep(JobRepository jobRepository,
// PlatformTransactionManager transactionManager) {
// return new StepBuilder("stockDataStep", jobRepository)
// .<String, String>chunk(10, transactionManager)
// .reader(reader)
// .writer(writer)
// .taskExecutor(taskExecutor)
// .build();
// }
// }
