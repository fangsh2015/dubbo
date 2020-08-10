package com.niki.spi.dubbo.impl;

import com.niki.spi.dubbo.Robot;
import org.apache.dubbo.common.URL;

/**
 * Robot的包装类
 * Created by Niki on 2020/8/9 5:47 下午
 */
public class RobotWrapper implements Robot {

    private Robot robot;

    public RobotWrapper(Robot robot) {
        this.robot = robot;
    }


    @Override
    public void sayHello(URL url) {
        System.out.println("变形金刚变身");
        robot.sayHello(url);
    }
}
