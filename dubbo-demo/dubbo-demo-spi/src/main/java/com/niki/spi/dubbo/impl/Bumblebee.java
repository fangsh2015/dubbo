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

    /**
     * 扩展接口依赖接口编程，如果方法不是接口中的方法则可能无法使用到， 特别是有扩展包装类的时候
     */
    public void bumblebee() {
        System.out.println("正宗暖男大黄蜂");
    }
}
