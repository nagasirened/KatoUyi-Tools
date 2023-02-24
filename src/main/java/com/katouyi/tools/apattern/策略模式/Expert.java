package com.katouyi.tools.apattern.策略模式;

import lombok.Setter;

/**
 * 高手，修炼一种防御功夫和一种攻击功夫
 */
public abstract class Expert {

    /**
     * Attack和Defend 分别是攻击功夫和防守功夫，不同的Expert可以组装不同的功夫。但是必须先set再出招
     *
     * Attack和Defend可以有很多的实现类，把他们相互组合起来即可
     *
     * 可以搞个默认值
     */

    @Setter
    private Attack attack;
    @Setter
    private Defend defend;

    public Expert(Attack attack, Defend defend) {
        this.attack = attack;
        this.defend = defend;
    }

    public void knockout() {
        if (attack == null) return;
        attack.knockout();
    }

    public void resist() {
        if (defend == null) return;
        defend.resist();
    }

}
