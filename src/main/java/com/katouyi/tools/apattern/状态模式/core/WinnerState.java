package com.katouyi.tools.apattern.状态模式.core;

import com.katouyi.tools.apattern.状态模式.VendingMachine;

public class WinnerState implements State {

    private final VendingMachine machine;
    public WinnerState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCorn() {
        System.out.println("中奖了，别这样搞");
    }

    @Override
    public void backCorn() {
        System.out.println("中奖了，别这样搞");
    }

    @Override
    public void turnCrank() {
        System.out.println("中奖了，别这样搞");
    }

    @Override
    public void dispense() {
        System.out.println("你中奖了，恭喜你，将得到2件商品");
        machine.dispense();
        if (machine.getCount() == 0) {
            System.out.println("商品已经售罄");
            machine.setCurrentState(machine.getSoldOutState());
        } else {
            machine.dispense();
            if (machine.getCount() > 0) {
                machine.setCurrentState(machine.getNoMoneyState());
            } else {
                System.out.println("商品已经售罄");
                machine.setCurrentState(machine.getSoldOutState());
            }

        }
    }
}
