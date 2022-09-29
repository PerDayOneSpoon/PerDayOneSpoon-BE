package com.sparta.perdayonespoon.quartzscheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class TestJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {

        System.out.println("This is the Job I want");
    }
}
