package com.katouyi.tools.apattern.状态模式.core;

import com.katouyi.tools.apattern.状态模式.VendingMachine;

public class NoMoneyState implements State {

    private final VendingMachine machine;
    public NoMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCorn() {
        System.out.println("投币成功");
        machine.setCurrentState( machine.getHasMoneyState() );
    }

    @Override
    public void backCorn() {
        System.out.println("没投币不能退");
    }

    @Override
    public void turnCrank() {
        System.out.println("没投币按了没用");
    }

    @Override
    public void dispense() {
        System.out.println("不能出货");
    }
}
