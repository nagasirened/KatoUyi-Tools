package com.katouyi.tools.apattern.命令模式.base.command.sub;

import com.katouyi.tools.apattern.命令模式.base.command.Command;
import com.katouyi.tools.apattern.命令模式.base.device.Light;

public class LightOffCommand implements Command {
	private final Light light;
	
	public LightOffCommand(Light light)
	{
		this.light = light;
	}
 
	@Override
	public void execute()
	{
		light.off();
	}
 
}