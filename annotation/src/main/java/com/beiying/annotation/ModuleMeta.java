package com.beiying.annotation;

public class ModuleMeta {
    public String templet;
    public String moduleName;
    public String title;
    public LayoutLevel layoutLevel;
    public int extraLevel;

    public ModuleMeta(String templet, String moduleName, String title, int layoutLevel,
                      int extraLevel) {
        this.templet = templet;
        this.moduleName = moduleName;
        this.title = title;
        this.extraLevel = extraLevel;
        if (layoutLevel == 500) {
            this.layoutLevel = LayoutLevel.VERY_LOW;
        } else if(layoutLevel == 400) {
            this.layoutLevel = LayoutLevel.LOW;
        } else if(layoutLevel == 300) {
            this.layoutLevel = LayoutLevel.NORMAL;
        } else if(layoutLevel == 200) {
            this.layoutLevel = LayoutLevel.HIGHT;
        } else if(layoutLevel == 100) {
            this.layoutLevel = LayoutLevel.VERY_HIGHT;
        }
    }

    public ModuleMeta(ModuleUnit unit, String moduleName) {
        this.moduleName = moduleName;
        this.templet = unit.templet();
        this.layoutLevel = unit.layoutLevel();
        this.extraLevel = unit.extralevel();
        this.title = unit.title();
    }
}
