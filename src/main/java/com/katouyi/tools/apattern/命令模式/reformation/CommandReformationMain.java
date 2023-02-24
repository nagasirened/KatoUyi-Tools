package com.katouyi.tools.apattern.命令模式.reformation;

import com.katouyi.tools.apattern.命令模式.base.command.Command;
import com.katouyi.tools.apattern.命令模式.base.command.CommandPanel;
import com.katouyi.tools.apattern.命令模式.base.command.sub.*;
import com.katouyi.tools.apattern.命令模式.base.device.Computer;
import com.katouyi.tools.apattern.命令模式.base.device.Door;
import com.katouyi.tools.apattern.命令模式.base.device.Light;

public class CommandReformationMain {

    public static void main(String[] args) {
        Light light = new Light();
        Door door = new Door();
        Computer computer = new Computer();

        CommandReformPanel reformPanel = new CommandReformPanel(6);
        reformPanel.setCommand(0, new CommandConsumer<>(light, Light::on));
        reformPanel.setCommand(1, new CommandConsumer<>(light, Light::off));
        reformPanel.setCommand(2, new CommandConsumer<>(door, Door::open));
        reformPanel.setCommand(3, new CommandConsumer<>(door, Door::close));
        reformPanel.setCommand(4, new CommandConsumer<>(computer, Computer::on));
        reformPanel.setCommand(5, new CommandConsumer<>(computer, Computer::off));

        for (int i = 0; i < 6; i++) {
            reformPanel.click( i );
        }
    }

}
