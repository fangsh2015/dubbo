package com.niki.spi.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * 默认的实现是大黄蜂
 * Created by Niki on 2020/8/9 1:17 下午
 */
@SPI(value = "bumbelbee")
public interface Robot {
    @Adaptive(value = "roboteName")
    void sayHello(URL url);

}
