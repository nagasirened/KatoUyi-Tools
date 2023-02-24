package com.katouyi.tools.apattern.命令模式.reformation;

import com.katouyi.tools.apattern.命令模式.base.command.sub.NoCommand;

public class CommandReformPanel {

    public Integer size;
    public CommandConsumer<?>[] consumers;

    public CommandReformPanel(Integer size) {
        this.size = size;
        consumers = new CommandConsumer[size];
        // 定义操作NoCommand
        for (int i = 0; i < size; i++) {
            consumers[i] = new CommandConsumer<>(new NoCommand(), NoCommand::execute);
        }
    }

    public void setCommand(int index, CommandConsumer<?> commandConsumer) {
        consumers[index] = commandConsumer;
    }

    public void click(int index) {
        consumers[index].exec();
    }

}