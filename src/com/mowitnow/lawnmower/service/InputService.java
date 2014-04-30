/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mowitnow.lawnmower.service;

import com.google.common.base.Preconditions;
import com.mowitnow.lawnmower.model.Command;
import com.mowitnow.lawnmower.model.Lawn;
import com.mowitnow.lawnmower.model.Mower;
import com.mowitnow.lawnmower.model.Mower.Direction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ihassiko-a
 */
public final class InputService {

    private static volatile InputService instance = null;
    private final AtomicInteger mowerCounter = new AtomicInteger(0);
    static LogService logger = LogService.getInstance();

    private InputService(String input) {
        super();
    }

    public final static InputService getInstance(String input) {
        if (InputService.instance == null) {
            synchronized (InputService.class) {
                if (InputService.instance == null) {
                    InputService.instance = new InputService(input);
                }
            }
        }
        checkInput(input);
        return InputService.instance;
    }

    public Lawn createLawn(String input) {
        final String lines[] = StringUtils.split(input, System.getProperty("line.separator"));
        final String size[] = StringUtils.split(lines[0], " ");
        final int width = Integer.valueOf(size[0]);
        final int height = Integer.valueOf(size[1]);
        return Lawn.getInstance(width, height);
    }

    public List<Mower> createMowerList(String input) {
        this.mowerCounter.lazySet(0);
        final List<Mower> mowerList = new ArrayList<Mower>();
        final String lines[] = StringUtils.split(input, System.getProperty("line.separator"));
        for (int i = 1; i < lines.length - 1; i = i + 2) {
            final String conf[] = StringUtils.split(lines[i], " ");
            final int id = this.mowerCounter.incrementAndGet();
            final int x = Integer.valueOf(conf[0]);
            final int y = Integer.valueOf(conf[1]);
            final Direction d = Direction.valueOf(conf[2]);
            final Mower mower = new Mower(id, x, y, d);

            final List<Command> commandList = new ArrayList<Command>();
            final String[] cmds = lines[i + 1].split("");
            for (int j = 1; j < cmds.length; j++) {
                Command cmd = Command.valueOf(cmds[j]);
                commandList.add(cmd);
            }
            mower.setCommandList(commandList);
            mowerList.add(mower);
        }
        return mowerList;
    }

    private static void checkInput(String input) {
        try {
            logger.log(null);
            Preconditions.checkArgument(StringUtils.isNotBlank(input), "Empty input is not allowed.");
            String lines[] = StringUtils.split(input, System.getProperty("line.separator"));
            Preconditions.checkArgument((lines.length % 2) == 1, "The number of input lines must be odd.");
            Preconditions.checkArgument(lines[0].matches("[0-9]+ [0-9]+"), "Lawn size is invalid.");
            for (int i = 1; i < lines.length - 1; i = i + 2) {
                Preconditions.checkArgument(lines[i].matches("[0-9]+ [0-9]+ [NESW]"), "Invalid position or direction for mower " + (i + 1) / 2 + ".");
                Preconditions.checkArgument(lines[i + 1].matches("[DGA]*"), "Invalid command for mower " + (i + 1) / 2 + ".");
            }
        } catch (IllegalArgumentException iex) {
            logger.log(iex.getMessage());
        }
    }
}
