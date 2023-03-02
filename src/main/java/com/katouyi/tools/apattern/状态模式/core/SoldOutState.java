package com.katouyi.tools.apattern.状态模式.core;

import com.katouyi.tools.apattern.状态模式.VendingMachine;

public class SoldOutState implements State {

    private final VendingMachine machine;
    public SoldOutState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCorn() {
        System.out.println("投币失败，商品已售罄");
    }

    @Override
    public void backCorn() {
        System.out.println("您未投币，想退钱么？...");
    }

    @Override
    public void turnCrank() {
        System.out.println("商品售罄，转动手柄也木有用");
    }

    @Override
    public void dispense() {
        throw new IllegalStateException("非法状态！");
    }
}
