package com.niki.spi.java;

/**
 * 交易接口，可能存在不同支付方式交易
 * Created by Niki on 2020/8/4 10:44 下午
 */
public interface Transaction {

    /**
     * 交易方法
     *
     */
    public void transaction();

}
