package com.katouyi.tools.apattern.状态模式.core;

import com.katouyi.tools.apattern.状态模式.VendingMachine;

import java.util.Random;

public class HasMoneyState implements State {

    private final VendingMachine machine;
    private Random random = new Random();
    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCorn() {
        System.out.println("投币过了");
    }

    @Override
    public void backCorn() {
        System.out.println("正在退币");
        machine.setCurrentState(machine.getNoMoneyState());
    }

    @Override
    public void turnCrank() {
        System.out.println("转动了手柄现在");
        // 10%几率中奖winner状态
        int num = random.nextInt(100);
        if (num < 10 && machine.getCount() > 1) {
            machine.setCurrentState(machine.getWinnerState());
        } else {
            machine.setCurrentState(machine.getSoldState());
        }
    }

    @Override
    public void dispense() {
        System.out.println("不行的呀");
    }
}
