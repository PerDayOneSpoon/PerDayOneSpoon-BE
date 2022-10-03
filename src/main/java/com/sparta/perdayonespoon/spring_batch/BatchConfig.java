package com.sparta.perdayonespoon.spring_batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


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
    
    
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .<Object, Object>chunk(10)
                .reader(new CustomReader<>(getItems()))     // getItem 메소드는 임시로 만든것, 필요한 메소드로 교체
                .processor(processor())     // 중간 가공이 필요할 경우 사용, 필요없으면 삭제
                .writer(writer())
                .build();
    }

    private Object getItems() {
    }

}
