package com.katouyi.tools.apattern.状态模式.core;

import com.katouyi.tools.apattern.状态模式.VendingMachine;

public class SoldState implements State {

    private final VendingMachine machine;
    public SoldState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCorn() {
        System.out.println("正在出货，请勿投币");
    }

    @Override
    public void backCorn() {
        System.out.println("正在出货，没有可退的钱");
    }

    @Override
    public void turnCrank() {
        System.out.println("正在出货，请勿重复转动手柄");
    }

    @Override
    public void dispense() {
        machine.dispense();
        if (machine.getCount() > 0) {
            machine.setCurrentState(machine.getNoMoneyState());
        } else {
            machine.setCurrentState(machine.getSoldOutState());
        }
    }
}
