package com.katouyi.tools.apattern.命令模式.base.command.sub;

import com.katouyi.tools.apattern.命令模式.base.command.Command;
import com.katouyi.tools.apattern.命令模式.base.device.Door;

public class DoorOpenCommand implements Command {
	private final Door door;

	public DoorOpenCommand(Door door)
	{
		this.door = door;
	}
 
	@Override
	public void execute()
	{
		door.open();
	}
 
}