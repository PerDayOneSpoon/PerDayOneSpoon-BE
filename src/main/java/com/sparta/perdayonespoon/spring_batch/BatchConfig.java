package com.sparta.perdayonespoon.spring_batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;


    public Job firstJob() {
        return this.jobBuilderFactory.get("firstJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    // 모든 Object 클래스 는 임시로 생성된것, 실제 프로젝트에 필요한 클래스로 교체 필요

    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<Object, Object>chunk(10)
                .reader(new CustomReader<>(getItems()))     // getItem 메소드는 임시로 만든것, 필요한 메소드로 교체
                .processor(processor())     // 중간 가공이 필요할 경우 사용, 필요없으면 삭제
                .writer(writer())
                .build();
    }

    private ItemWriter<Object> writer() {       // 임시 writer 메소드, 이것도 추후 필요한 형태로 변경 필요.
        return items -> log.info(items.stream()
                .map(Object::getName)
                .collect(Collectors.joining(", ")));
    }

}

    private List<Object> getItems() {       // 예를 들어 만들어놓은 임시 메소드
        List<Object> items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            items.add(new Object());
        }

        return items;
    }

}
