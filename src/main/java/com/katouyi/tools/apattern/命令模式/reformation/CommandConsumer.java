package com.katouyi.tools.apattern.命令模式.reformation;

import java.util.function.Consumer;

public class CommandConsumer<T> {

    private final T command;

    private final Consumer<T> consumer;

    public CommandConsumer(T command, Consumer<T> consumer) {
        this.command = command;
        this.consumer = consumer;
    }

    public void exec() {
        this.consumer.accept(command);
    }

}
