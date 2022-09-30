package com.sparta.perdayonespoon.quartzscheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MyScheduler {

    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;


    @PostConstruct  //
    public void start() throws SchedulerException{
        schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();

        // 수행할 Job 의 인스턴스 를 정의 한다.
        JobDetail jobDetail = JobBuilder.newJob(TestJob.class)
                .withIdentity("TestJob")    // 필요 할 경우 그룹을 지정 가능.
                .usingJobData("JobKey", "My First Job")
                .requestRecovery()
                .build();       // 필요하면 .usingJobData 로 데이터를 넣을 수 있다.

        // 작업이 수행될 일정을 정의 한다.
        Trigger trigger = TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")
                        .withMisfireHandlingInstructionFireAndProceed())
                .forJob("TestJob")
                .startNow()
                .build();

//         cron 표현식 말고도 단순하게 지정 할 수도 있음.
//        startAt과 endAt을 사용해 job 스케쥴의 시작, 종료 시간도 지정할 수 있다.
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .startAt(startDateTime)
//                .endAt(EndDateTime)
//                .withSchedule(CronScheduleBuilder.cronSchedule("*/1 * * * *"))
//                .build();

        scheduler.scheduleJob(jobDetail, trigger);

    }

}
