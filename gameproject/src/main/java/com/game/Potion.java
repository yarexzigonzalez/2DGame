package com.game;
public class Potion implements Item{
    private String name;
    private String type;
    private int quantity;
    private String description;
    private int hp;
    private int atk;
    private double moveSpeed;
    private double jumpHeight;
    private double atkDelay;
    private int atkSetting;

    Potion(String name, String type, int quantity, String description) {//, int hp, int atk, double moveSpeed, double jumpHeight){
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.description = description;
        /*this.hp = hp;
        this.atk = atk;
        this.moveSpeed = moveSpeed;
        this.jumpHeight = jumpHeight;*/
    }

    // POTION NAME + TYPE + QUANTITY
    public String getName() { return name; }
    public String getType() { return type; }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getQuantity() {
        return quantity;
    }
    public void addQuantity(int quantity) {
        this.quantity += 1;
    }
    public void subQuantity(int quantity) {
        this.quantity -= 1;
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }

    // POTION DESCRIPTION
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    // CHARACTER HP
    public void setHp(int hp) {
        this.hp = hp;
    }
    public int getHp(int hp) {
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
    public int getAtk(int atk) {
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
    public double getMoveSpeed(double moveSpeed) {
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
    public double getJumpHeight(double jumpHeight) {
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

    // CHARACTER ATTACKSPEED - use timer to delay each attack
    public void setAtkDelay(double atkDelay) {
        this.atkDelay = atkDelay;
    }
    public double getAtkDelay(double atkDelay) {
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

    public String toString() {
        return String.format("\nPotion{\n   name='%s'\n   type='%s'\n   quantity=%d\n   description='%s'\n}\n", name, type, quantity, description);
    }
}
