package com.x.plugin.data;

/**
 * Bean数据
 * @author AD
 * @date 2022/5/13 22:22
 */
public class BeanData {

    /**
     * bean名称
     */
    private String name;
    /**
     * 是否修改bean名称
     */
    private boolean modifyName;
    /**
     * bean原始名称
     */
    private String srcName;
    /**
     * bean类名
     */
    private String classname;
    /**
     * 作用域
     */
    private String scope;

    public String getSrcName() {
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public boolean isModifyName() {
        return modifyName;
    }

    public void setModifyName(boolean modifyName) {
        this.modifyName = modifyName;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
