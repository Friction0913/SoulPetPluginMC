package com.pets.plugin.pet;

public class PetStats {
    private int combatLevel;
    private double combatXp;
    private int miningLevel;
    private double miningXp;
    private int woodcuttingLevel;
    private double woodcuttingXp;
    private int huntingLevel;
    private double huntingXp;
    private int enduranceLevel;
    private double enduranceXp;

    public PetStats() {
        this.combatLevel = 1;
        this.combatXp = 0;
        this.miningLevel = 1;
        this.miningXp = 0;
        this.woodcuttingLevel = 1;
        this.woodcuttingXp = 0;
        this.huntingLevel = 1;
        this.huntingXp = 0;
        this.enduranceLevel = 1;
        this.enduranceXp = 0;
    }

    public double xpNeeded(int level) {
        return 100 * Math.pow(level, 1.5);
    }

    private void levelUpIfNeeded(int[] level, double[] xp) {
        double needed = xpNeeded(level[0]);
        while (xp[0] >= needed) {
            xp[0] -= needed;
            level[0]++;
            needed = xpNeeded(level[0]);
        }
    }

    public void addCombatXp(double amount) {
        int[] lvl = {combatLevel};
        double[] xp = {combatXp};
        xp[0] += amount;
        levelUpIfNeeded(lvl, xp);
        combatLevel = lvl[0];
        combatXp = xp[0];
    }

    public void addMiningXp(double amount) {
        int[] lvl = {miningLevel};
        double[] xp = {miningXp};
        xp[0] += amount;
        levelUpIfNeeded(lvl, xp);
        miningLevel = lvl[0];
        miningXp = xp[0];
    }

    public void addWoodcuttingXp(double amount) {
        int[] lvl = {woodcuttingLevel};
        double[] xp = {woodcuttingXp};
        xp[0] += amount;
        levelUpIfNeeded(lvl, xp);
        woodcuttingLevel = lvl[0];
        woodcuttingXp = xp[0];
    }

    public void addHuntingXp(double amount) {
        int[] lvl = {huntingLevel};
        double[] xp = {huntingXp};
        xp[0] += amount;
        levelUpIfNeeded(lvl, xp);
        huntingLevel = lvl[0];
        huntingXp = xp[0];
    }

    public void addEnduranceXp(double amount) {
        int[] lvl = {enduranceLevel};
        double[] xp = {enduranceXp};
        xp[0] += amount;
        levelUpIfNeeded(lvl, xp);
        enduranceLevel = lvl[0];
        enduranceXp = xp[0];
    }

    public int getCombatLevel() { return combatLevel; }
    public double getCombatXp() { return combatXp; }
    public int getMiningLevel() { return miningLevel; }
    public double getMiningXp() { return miningXp; }
    public int getWoodcuttingLevel() { return woodcuttingLevel; }
    public double getWoodcuttingXp() { return woodcuttingXp; }
    public int getHuntingLevel() { return huntingLevel; }
    public double getHuntingXp() { return huntingXp; }
    public int getEnduranceLevel() { return enduranceLevel; }
    public double getEnduranceXp() { return enduranceXp; }

    public void setCombatLevel(int combatLevel) { this.combatLevel = combatLevel; }
    public void setCombatXp(double combatXp) { this.combatXp = combatXp; }
    public void setMiningLevel(int miningLevel) { this.miningLevel = miningLevel; }
    public void setMiningXp(double miningXp) { this.miningXp = miningXp; }
    public void setWoodcuttingLevel(int woodcuttingLevel) { this.woodcuttingLevel = woodcuttingLevel; }
    public void setWoodcuttingXp(double woodcuttingXp) { this.woodcuttingXp = woodcuttingXp; }
    public void setHuntingLevel(int huntingLevel) { this.huntingLevel = huntingLevel; }
    public void setHuntingXp(double huntingXp) { this.huntingXp = huntingXp; }
    public void setEnduranceLevel(int enduranceLevel) { this.enduranceLevel = enduranceLevel; }
    public void setEnduranceXp(double enduranceXp) { this.enduranceXp = enduranceXp; }
}
