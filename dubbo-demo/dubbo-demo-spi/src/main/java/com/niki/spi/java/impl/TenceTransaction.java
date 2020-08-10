package com.niki.spi.java.impl;

import com.niki.spi.java.Transaction;

/**
 * Created by Niki on 2020/8/4 10:48 下午
 */
public class TenceTransaction implements Transaction {
    @Override
    public void transaction() {
        System.out.println("微信支付");
    }
}
