/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mowitnow.lawnmower.model;

import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author ihassiko-a
 */
public class Mower {

    /**
     * Directions
     *
     * @author ihassiko-a
     */
    public static enum Direction {

        /**
         * North
         */
        N(0),
        /**
         * EAST
         */
        E(90),
        /**
         * SOUTH
         */
        S(180),
        /**
         * WEST
         */
        W(270);

        private int angle;

        public int getAngle() {
            return this.angle;
        }

        public Direction getDirection(int angle) {
            for (Direction d : Direction.values()) {
                if ((d.getAngle() - angle) % 360 == 0) {
                    return d;
                }
            }
            return null;
        }

        Direction(int angle) {
            this.angle = angle;
        }
    };

    private int id;
    private final IntegerProperty x = new SimpleIntegerProperty();
    private final IntegerProperty y = new SimpleIntegerProperty();
    private Direction direction;
    private final IntegerProperty observableDirection = new SimpleIntegerProperty();
    private List<Command> commandList;

    public Mower(int id, int x, int y, Direction direction) {
        this.id = id;
        this.x.set(x);
        this.y.set(y);
        this.direction = direction;
        this.observableDirection.set(this.direction.getAngle());
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the x
     */
    public IntegerProperty x() {
        return this.x;
    }

    /**
     * @return the x value
     */
    public final int getX() {
        return x.get();
    }

    /**
     * @param x the x to set
     */
    public final void setX(int x) {
        this.x.set(x);
    }

    /**
     * @return the y
     */
    public IntegerProperty y() {
        return this.y;
    }

    /**
     * @return the y value
     */
    public final int getY() {
        return y.get();
    }

    /**
     * @param y the y to set
     */
    public final void setY(int y) {
        this.y.set(y);
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * @return the y
     */
    public IntegerProperty observableDirection() {
        return this.observableDirection;
    }

    /**
     * @return the commandList
     */
    public List<Command> getCommandList() {
        return commandList;
    }

    /**
     * @param commandList the commandList to set
     */
    public void setCommandList(List<Command> commandList) {
        this.commandList = commandList;
    }

    public void mow(Lawn lawn) {
        for (Command cmd : this.commandList) {
            if (Command.D.equals(cmd)) {
                final int angle = this.direction.getAngle();
                Direction d = this.direction.getDirection(angle + 90);
                this.setDirection(d);
                this.observableDirection.set(d.getAngle());
            } else if (Command.G.equals(cmd)) {
                final int angle = this.direction.getAngle();
                Direction d = this.direction.getDirection(angle - 90);
                this.setDirection(d);
                this.observableDirection.set(d.getAngle());
            } else if (Command.A.equals(cmd)) {
                if (Direction.N.equals(this.direction)) {
                    avanceN(lawn);
                } else if (Direction.E.equals(this.direction)) {
                    avanceE(lawn);
                } else if (Direction.S.equals(this.direction)) {
                    avanceS(lawn);
                } else if (Direction.W.equals(this.direction)) {
                    avanceW(lawn);
                }
            }
        }
    }

    private void avanceN(Lawn lawn) {
        int x = this.getX();
        int y = this.getY();
        if (y < lawn.getHeight() && isEmpty(lawn, x, y + 1)) {
            this.setY(y + 1);
        }
    }

    private void avanceE(Lawn lawn) {
        int x = this.getX();
        int y = this.getY();
        if (x < lawn.getWidth() && isEmpty(lawn, x + 1, y)) {
            this.setX(x + 1);
        }
    }

    private void avanceS(Lawn lawn) {
        int x = this.getX();
        int y = this.getY();
        if (y > 1 && isEmpty(lawn, x, y - 1)) {
            this.setY(y - 1);
        }
    }

    private void avanceW(Lawn lawn) {
        int x = this.getX();
        int y = this.getY();
        if (x > 1 && isEmpty(lawn, x - 1, y)) {
            this.setX(x - 1);
        }
    }

    private boolean isEmpty(final Lawn lawn, final int x, final int y) {
        List<Mower> mowerList = lawn.getMowerList();
        for (Mower mower : mowerList) {
            int mx = mower.getX();
            int my = mower.getY();
            if (mx == x && my == y) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mower [" + "getX " + getX() + " " + "getY " + getY() + " " + "id " + id + " " + "direction " + direction + "]";
    }

}
