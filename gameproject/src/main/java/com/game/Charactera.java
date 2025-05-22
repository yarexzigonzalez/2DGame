package com.game;

/* public abstract class Character {
    public int currentHealth;
    public int maxHealth;
    public double moveSpeed;
    public int power;
    public boolean isDead;
}*/

import java.util.ArrayList;

public abstract class Charactera{
    private int hp;
    private int atk;
    private double moveSpeed;
    private double jumpHeight;
    protected ArrayList<Item> inventory;
    private double weaponRange;
    private int weaponSetting;
    private double atkDelay;
    private int atkSetting;
    private int level;
    private int exp;

    public Charactera(int hp, int atk, double moveSpeed, double jumpHeight, double weaponRange, double atkDelay) {
        this.hp = hp;
        this.atk = atk;
        this.moveSpeed = moveSpeed;
        this.jumpHeight = jumpHeight;
        this.weaponRange = weaponRange;
        this.atkDelay = atkDelay;
        this.inventory = new ArrayList<>();
        this.level = 0;
        this.exp = 0;
    }

    // CHARACTER HP
    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getHp() {
        return hp;
    }
    public void addHp(int hp) {
        this.hp += hp;
    }
    public void subHp(int hp) {
        this.hp -= hp;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    // CHARACTER ATK
    public void setAtk(int atk) {
        this.atk = atk;
    }
    public int getAtk() {
        return atk;
    }
    public void addAtk(int atk) {
        this.atk += atk;
    }
    public void subAtk(int atk) {
        this.atk -= atk;
        if (this.atk < 0) {
            this.atk = 0;
        }
    }

    // CHARACTER MOVESPEED
    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    public double getMoveSpeed() {
        return moveSpeed;
    }
    public void addMoveSpeed(double moveSpeed) {
        this.moveSpeed += moveSpeed;
    }
    public void subMoveSpeed(double moveSpeed) {
        this.moveSpeed -= moveSpeed;
        if (this.moveSpeed < 0) {
            this.moveSpeed = 0;
        }
    }

    // CHARACTER JUMPHEIGHT
    public void setJumpHeight(double jumpHeight) {
        this.jumpHeight = jumpHeight;
    }
    public double getJumpHeight() {
        return jumpHeight;
    }
    public void addJumpHeight(double jumpHeight) {
        this.jumpHeight += jumpHeight;
    }
    public void subJumpHeight(double jumpHeight) {
        this.jumpHeight -= jumpHeight;
        if (this.jumpHeight < 0) {
            this.jumpHeight = 0;
        }
    }
    
    // CHARACTER WEAPONRANGE W/ WEAPONSETTINGS
    public void setWeaponRange(double weaponRange) {
        this.weaponRange = weaponRange;
    }
    public double getWeaponRange() {
        return weaponRange;
    }
    public void addWeaponRange(double weaponRange) {
        this.weaponRange += weaponRange;
    }
    public void subWeaponRange(double weaponRange) {
        this.weaponRange -= weaponRange;
        if (this.weaponRange < 0) {
            this.weaponRange = 0;
        }
    }
    public void setWeaponSetting(int weaponSetting) {
        double dagger = 1;
        double sword = 2;
        double hammer = 3;
        if (weaponSetting >= 0 & weaponSetting <= 3) {
            switch (weaponSetting) {
                case 0:
                    this.weaponRange = 0;
                    break;
                case 1: // DAGGER
                    this.weaponRange = dagger;
                    break;
                case 2: // SWORD
                    this.weaponRange = sword;
                    break;
                case 3: // HAMMER
                    this.weaponRange = hammer;
                    break;
                default:
                    this.weaponRange = sword;
            }
        }
    }

    // CHARACTER ATTACKSPEED - use timer to delay each attack
    public void setAtkDelay(double atkDelay) {
        this.atkDelay = atkDelay;
    }
    public double getAtkDelay() {
        return atkDelay;
    }
    public void addAtkDelay(double atkDelay) {
        this.atkDelay += atkDelay;
    }
    public void subAtkDelay(double atkDelay) {
        this.atkDelay -= atkDelay;
    }
    public void setAtkSetting(int atkSetting) {
        double sTime = 1;
        double mTime = 2;
        double lTime = 3;
        if (atkSetting >= 0 & atkSetting <= 3) {
            switch (atkSetting) {
                case 0:
                    this.atkDelay = 0;
                    break;
                case 1: // FAST
                    this.atkDelay = sTime;
                    break;
                case 2: // NORMAL
                    this.atkDelay = mTime;
                    break;
                case 3: // SLOW
                    this.atkDelay = lTime;
                    break;
                default:
                    this.atkDelay = mTime;
            }
        }
    }

    // CHARACTER LEVEL + EXP
    public void setLevel(int level) {
        this.level = level;
    }
    public int getLevel() {
        if (level < 0) {
            this.level = 0;
        } else if (level > 100) {
            this.level = 100;
        }
        return level;
    }
    public void levelUp(int exp) {
        if (exp >= 100) {
            this.level += 1;
            this.exp = 0;
        }
    }
    // EXP
    public void setExp(int exp) {
        if (exp < 0) {
            this.exp = 0;
        }
        this.exp = exp;
    }
    public int getExp(int exp) {
        return exp;
    }
    public void addExp(int exp) {
        this.exp += exp;
        if (this.exp >= 100) {
            levelUp(this.exp);
        }
    }

    // CHARACTER TOSTRING
    public String toString() {
        return String.format("Character [hp=%d, atk=%d, moveSpeed=%.2f, jumpHeight=%.2f, weaponRange=%.2f, atkDelay=%.2f, level=%d, exp=%d]", 
            hp, atk, moveSpeed, jumpHeight, weaponRange, atkDelay, level, exp);
    }
}
