package com.niki.spi.dubbo;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.extension.factory.SpiExtensionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niki on 2020/8/9 1:21 下午
 */
public class Main {

    public static void main(String[] args) {
        ExtensionLoader<Robot> robotExtensionLoader = ExtensionLoader.getExtensionLoader(Robot.class);

        Map<String, String> params = new HashMap<>();

        params.put("roboteName", "optimusPrime");

        URL roboteUrl = new URL("dubbo","127.0.0.",0000,params);

        // 大黄蜂
        Robot bumbelbee = robotExtensionLoader.getExtension("bumbelbee");
        bumbelbee.sayHello(roboteUrl);

        Robot optimusPrime = robotExtensionLoader.getExtension("optimusPrime");
        optimusPrime.sayHello(roboteUrl);

        // 通过adaptive自选择的实例
        Robot adaptiveExtension = robotExtensionLoader.getAdaptiveExtension();
        adaptiveExtension.sayHello(roboteUrl);

        Robot adaptiveExtension1 = robotExtensionLoader.getAdaptiveExtension();

        SpiExtensionFactory spiExtensionFactory = new SpiExtensionFactory();
        Robot bumbelbee1 = spiExtensionFactory.getExtension(Robot.class, "bumbelbee");
        roboteUrl = roboteUrl.addParameter("roboteName","bumbelbee");
        bumbelbee1.sayHello(roboteUrl);

    }
}
