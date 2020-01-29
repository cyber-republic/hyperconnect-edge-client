package com.hyper.connect.util;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;

public class TaskUtil{

    public static <T> Task<T> create(Callable<T> callable){
        return new Task<T>(){
            @Override
            public T call() throws Exception{
                return callable.call();
            }
        };
    }

}
