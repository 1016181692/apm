package com.allianty.annotation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allianty.entity.LoggerDTO;
import com.allianty.util.HttpUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@Aspect
public class LogAspect {

    private static  final Logger logger= LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("@annotation(com.allianty.annotation.AopLogger)")
    public void doLog(){

    }

    @Around("doLog()")
    public void around(ProceedingJoinPoint point) throws Throwable {
        //检测特定系统是否进行监测
        String monitoringState=HttpUtils.httpRequest("http://localhost:8081/data/doCheck","GET","","");

        System.out.println("当前状态："+monitoringState);
        if("Y".equals(monitoringState)){//执行监测
            long begin=System.currentTimeMillis();
            Date createTime=new Date(begin);//进入方法的时间
            Object obj=point.proceed();
            String methodName=point.getSignature().getName();//获取方法的名字
            String className=point.getSignature().getDeclaringTypeName();//获取方法的类名
            //获取参数并转换为json字符串
            Object[] args = point.getArgs();
            String params = JSONArray.toJSONString(args);
            long end=System.currentTimeMillis();
            //获取方法执行所消耗的时间
            String expendTime=String.valueOf(end-begin);
            System.out.println("类名："+className+"方法名"+methodName+"耗时"+expendTime);
            LoggerDTO loggerDTO=new LoggerDTO();
            loggerDTO.setClassName(className);
            loggerDTO.setMethodName(methodName);
            loggerDTO.setParams(params);
            loggerDTO.setExpendTime(expendTime);
            loggerDTO.setCreateTime(createTime);
            //转换为jsonString
            String json=JSONObject.toJSONString(loggerDTO);
            //发送请求
            HttpUtils.doPostJson("http://localhost:8081/data/doLogger", JSON.parseObject(json),null);
            return;
        }
            Object  obj=point.proceed();
    }
}
