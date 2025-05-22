package com.game;

public interface Entity {
    public String name = null;
    public String type = null;
    public int hp = 0;
    public int atk = 0;
    public double moveSpeed = 0.0;
    public double jumpHeight = 0.0;
    
    // ENTITY HP
    public void setHp(int hp);
    public int getHp(int hp);
    public void addHp(int hp);
    public void subHp(int hp);

    // ENTITY ATK
    public void setAtk(int atk);
    public int getAtk(int atk);
    public void addAtk(int atk);
    public void subAtk(int atk);

    // ENTITY MOVESPEED
    public void setMoveSpeed(double moveSpeed);
    public double getMoveSpeed(double moveSpeed);
    public void addMoveSpeed(double moveSpeed);
    public void subMoveSpeed(double moveSpeed);

    // ENTITY JUMPHEIGHT
    public void setJumpHeight(double jumpHeight);
    public double getJumpHeight(double jumpHeight);
    public void addJumpHeight(double jumpHeight);
    public void subJumpHeight(double jumpHeight);
}
