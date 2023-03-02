package com.katouyi.tools.apattern.状态模式;

import com.katouyi.tools.apattern.状态模式.core.NoMoneyState;
import com.katouyi.tools.apattern.状态模式.core.State;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendingMachine {

    private State noMoneyState;     // 没有投币的状态
    private State hasMoneyState;    // 已投币的状态
    private State soldState;        // 出货的状态
    private State soldOutState;     // 售罄的状态
    private State winnerState;      // 中间，出两个

    private State currentState;
    private int count = 0;

    public VendingMachine(int count) {
        noMoneyState = new NoMoneyState(this);

        if (count > 0) {
            this.count = count;
            currentState = noMoneyState;
        }
    }

    public void insertCorn() {
        currentState.insertCorn();
    }

    public void backCorn() {
        currentState.backCorn();
    }

    public void turnCrank() {
        currentState.turnCrank();
    }

    public void dispense() {
        System.out.println("shoot");
        if (count > 0) {
            count -= 1;
        }
    }

}
