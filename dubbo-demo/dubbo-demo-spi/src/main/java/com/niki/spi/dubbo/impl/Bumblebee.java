package com.niki.spi.dubbo.impl;

import com.niki.spi.dubbo.Robot;
import org.apache.dubbo.common.URL;

/**
 * Created by Niki on 2020/8/9 1:18 下午
 */
public class Bumblebee implements Robot {
    @Override
    public void sayHello(URL name) {
        System.out.println("大家好，我是大黄蜂");
    }
}
