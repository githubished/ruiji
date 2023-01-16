package com.example.reggie.commmon;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static  void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentid(){
        return threadLocal.get();
    }


}