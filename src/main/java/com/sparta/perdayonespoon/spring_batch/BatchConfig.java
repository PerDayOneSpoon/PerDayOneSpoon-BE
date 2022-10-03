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
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    private ItemReader<Object> reader() {
        return
    }
}
