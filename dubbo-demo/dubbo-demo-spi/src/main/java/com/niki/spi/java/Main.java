package com.niki.spi.java;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 使用java的spi扩展，实现支付方式的选定（支付宝）
 * Created by Niki on 2020/8/4 10:49 下午
 */
public class Main {
    public static void main(String[] args) {
        ServiceLoader<Transaction> serviceLoaders = ServiceLoader.load(Transaction.class);

        Iterator<Transaction> iterator = serviceLoaders.iterator();

        while (iterator.hasNext()) {
            Transaction next = iterator.next();
            next.transaction();
        }
    }
}
