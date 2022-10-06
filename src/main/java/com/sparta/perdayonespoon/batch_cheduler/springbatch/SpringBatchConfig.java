package com.sparta.perdayonespoon.batch_cheduler.springbatch;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpringBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job chunkProcessingJob() {
        return jobBuilderFactory.get("chunkProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(chunkBaseStep(null))
                .build();
    }

    @JobScope
    @Bean       // 100개 의 데이터를 10개씩 10번 나눠서 처리하는 chunk step 작성
    public Step chunkBaseStep(@Value("#{jobParameters[chunkSize]}") String chunkSize) { // @JobScope,@Value, Jobparameters 를 이용해서 파라미터 값을 주입받아 사용 가능.
        return stepBuilderFactory.get("chunkBaseStep")
                .<String, String>chunk(StringUtils.isNotEmpty(chunkSize) ? Integer.parseInt(chunkSize) : 10)  // 제네릭 첫번째 String 은 reader 에서 반환되는 input, 두번째 String 은 processor 에서 반환되는 output.
                .reader(itemReader())                // chunkSize = 한묶음으로 처리하는 개수
                .processor(itemProcessor())     // 각 메소를 이용해서 reader,processor,writer 를 만들어준다.
                .writer(itemWriter())
                .build();
    }

    private ItemWriter<? super String> itemWriter() {
        return items -> log.info("chunk item size : {}", items.size());
//        return items -> items.forEach(log::info);
    }

    // processor 는 reader 에서 생성한 데이터를 가공 하거나 writer 로 넘길지 말지를 결정 한다. (null 을 반환할경우 writer 로 넘어갈 수 없다.)
    private ItemProcessor<? super String, String> itemProcessor() {
        return item -> item + ", Spring Batch"; // getItems() 메소드에서 정한 "i + hello" 에 Spring Batch 문자열을 추가 하는 프로세스
    }

    // Reader 는 배치처리 대상 객체를 읽는 역할. 예를 들어 파일 혹은 DB 로 부터 데이터를 읽는다.
    private ItemReader<String> itemReader() {
        return new ListItemReader<>(getItems());    // 자바 기본 제공인 ListItemReader 로 리스트를 생성자로 받아 아이템리더 역할을 함.
    }

    // chunk 랑 달리 단일 처리 프로세스 tasklet
//    @Bean
//    public Step taskBaseStep() {
//        return stepBuilderFactory.get("taskBaseStep")
//                .tasklet(this.tasklet())
//                .build();
//    }
//
//    private Tasklet tasklet() {
//
//        return (contribution, chunkContext) -> {
//            List<String> items = getItems();        // 테스트용 여러개의 item 을 가지고 있는 리스트 를 만들건데, getItems 메소드에서 생성 하겠다
//            log.info("task item size : {}", items.size());      // 리스트 의 size 를 로그로 찍어준다
//
//            return RepeatStatus.FINISHED;
//        };
//    }
//
    private List<String> getItems() {       // 테스트용 item 을 100개 까지 만든다
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(i + "hello");
        }
        return items;
    }
}
