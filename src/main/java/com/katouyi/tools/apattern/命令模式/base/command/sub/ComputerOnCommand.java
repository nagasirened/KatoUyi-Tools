package com.katouyi.tools.apattern.命令模式.base.command.sub;

import com.katouyi.tools.apattern.命令模式.base.command.Command;
import com.katouyi.tools.apattern.命令模式.base.device.Computer;

public class ComputerOnCommand implements Command {
	private final Computer computer;

	public ComputerOnCommand(Computer computer)
	{
		this.computer = computer;
	}
 
	@Override
	public void execute()
	{
		computer.on();
	}
 
}