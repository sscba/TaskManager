package com.taskmanager.taskmanagerapp.exception;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        System.err.println("Async Exception Detected!");
        System.err.println("Exception message - " + ex.getMessage());
        System.err.println("Method name - " + method.getName());
        for (Object param : params) {
            System.err.println("Parameter value - " + param);
        }
    }
}
