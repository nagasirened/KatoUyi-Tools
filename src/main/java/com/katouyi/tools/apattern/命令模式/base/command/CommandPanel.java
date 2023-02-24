package com.katouyi.tools.apattern.命令模式.base.command;

import com.katouyi.tools.apattern.命令模式.base.command.sub.NoCommand;

public class CommandPanel {

    public Integer SIZE;

    public Command[] commands;

    public CommandPanel(Integer size) {
        this.SIZE = size;
        this.commands = new Command[ SIZE ];
        // 定义操作NoCommand
        for (int i = 0; i < SIZE; i++) {
            commands[i] = new NoCommand();
        }
    }

    public void setCommand(int index, Command command) {
        commands[index] = command;
    }

    public void click(int index) {
        commands[index].execute();
    }

}
