package com.game;
public class Weapon extends Item {
    private String name;
    private String type;
    private int atk;
    private double range;
    private int weaponSetting;
    private double atkDelay;
    private int atkSetting;


    public Weapon(String name, String type, int atk, double range, double atkDelay) {
        this.type = type;
        this.atk = atk;
        this.range = range;
        this.atkDelay = atkDelay;
    }

    // WEAPON NAME + TYPE
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
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

    // CHARACTER WEAPONRANGE W/ WEAPONSETTINGS
    public void setRange(double range) {
        this.range = range;
    }
    public double getRange(double range) {
        return range;
    }
    public void addRange(double range) {
        this.range += range;
    }
    public void subWeaponRange(double range) {
        this.range -= range;
        if (this.range < 0) {
            this.range = 0;
        }
    }
    public void setWeaponSetting(int weaponSetting) {
        double dagger = 1;
        double sword = 2;
        double hammer = 3;
        if (weaponSetting >= 0 & weaponSetting <= 3) {
            switch (weaponSetting) {
                case 0:
                    this.range = 0;
                    break;
                case 1: // DAGGER
                    this.range = dagger;
                    break;
                case 2: // SWORD
                    this.range = sword;
                    break;
                case 3: // HAMMER
                    this.range = hammer;
                    break;
                default:
                    this.range = sword;
            }
        }
    }

    // CHARACTER ATTACKDELAY - use timer to delay each attack
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
        return String.format("\nWeapon: %s\nType: %s\nATK: %d\nRange: %.2f\nATK Delay: %.2f\n", name, type, atk, range, atkDelay);
    }
}
