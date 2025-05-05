package com.cf.smartq.scoring;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Inherited
public @interface ScoringStrategyConfig {
    /**
     * 应用类型
     */
    int appType();

    /**
     * 策略类型
     */
    int scoringStrategy();
}
