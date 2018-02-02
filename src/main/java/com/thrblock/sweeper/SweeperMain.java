package com.thrblock.sweeper;

import org.springframework.context.support.AbstractApplicationContext;

import com.thrblock.cino.frame.AWTFrameFactory;
import com.thrblock.springcontext.CinoInitor;

public class SweeperMain {
    public static void main(String[] args) {
        AbstractApplicationContext context = CinoInitor.getCustomContext(SweeperContext.class);
        AWTFrameFactory frame = context.getBean(AWTFrameFactory.class);
        frame.buildFrame();
    }
}
