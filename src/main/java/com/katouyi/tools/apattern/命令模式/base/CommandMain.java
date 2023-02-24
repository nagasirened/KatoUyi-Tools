package com.katouyi.tools.apattern.命令模式.base;

import com.katouyi.tools.apattern.命令模式.base.command.CommandPanel;
import com.katouyi.tools.apattern.命令模式.base.command.sub.*;
import com.katouyi.tools.apattern.命令模式.base.device.Computer;
import com.katouyi.tools.apattern.命令模式.base.device.Door;
import com.katouyi.tools.apattern.命令模式.base.device.Light;

public class CommandMain {

    public static void main(String[] args) {
        Light light = new Light();
        Door door = new Door();
        Computer computer = new Computer();

        CommandPanel commandPanel = new CommandPanel(6);
        commandPanel.setCommand(0, new LightOnCommand(light));
        commandPanel.setCommand(1, new LightOffCommand(light));
        commandPanel.setCommand(2, new DoorOpenCommand(door));
        commandPanel.setCommand(3, new DoorCloseCommand(door));
        commandPanel.setCommand(4, new ComputerOnCommand(computer));
        commandPanel.setCommand(5, new ComputerOffCommand(computer));

        for (int i = 0; i < 6; i++) {
            commandPanel.click( i );
        }

    }

}
