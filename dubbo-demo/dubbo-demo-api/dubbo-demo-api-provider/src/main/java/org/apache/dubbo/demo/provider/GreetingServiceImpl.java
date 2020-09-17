package org.apache.dubbo.demo.provider;

import org.apache.dubbo.demo.GreetingService;

/**
 * Created by Niki on 2020/9/15 14:31
 */
public class GreetingServiceImpl implements GreetingService {
    @Override
    public String hello() {
        return "hello greeting";
    }
}
