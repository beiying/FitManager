package com.beiying.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于记录每个Module配置相关信息
 * */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ModuleUnit {
    String templet() default "normal";
    String title() default "FitManager";
    LayoutLevel layoutLevel() default LayoutLevel.NORMAL;
    int extralevel() default 0;
}
