package com.niki.spi.java.impl;

import com.niki.spi.java.Transaction;

/**
 * 阿里的支付方法
 * Created by Niki on 2020/8/4 10:47 下午
 */
public class AliTransaction implements Transaction {
    @Override
    public void transaction() {
        System.out.println("支付宝支付");
    }
}
