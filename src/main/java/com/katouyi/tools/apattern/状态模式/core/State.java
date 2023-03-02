package com.katouyi.tools.apattern.状态模式.core;


/**
 * 机器状态的接口，下面四个方式是机器的四个操作
 */
public interface State {

    /** 投币 */
    void insertCorn();

    /** 退币 */
    void backCorn();

    /** 转动手柄 */
    void turnCrank();

    /** 出货 */
    void dispense();

}
