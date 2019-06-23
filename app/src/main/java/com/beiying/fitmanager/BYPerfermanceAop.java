package com.beiying.fitmanager;

import com.beiying.fitmanager.core.LeLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by beiying on 2019/6/12.
 */

@Aspect
public class BYPerfermanceAop {

    @Around("call(* com.beiying.fitmanager.BYApplication.**(..))")
    public void traceMethodRunTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        long startTime = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        LeLog.e(methodName + " run :" + (System.currentTimeMillis() - startTime));
    }

    @Around("execution(* android.app.Activity.setConetentView(..))")
    public void traceSetContentRunTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        long startTime = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        LeLog.e(methodName + " run :" + (System.currentTimeMillis() - startTime));
    }

}
