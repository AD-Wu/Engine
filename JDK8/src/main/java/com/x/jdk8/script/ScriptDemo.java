package com.x.jdk8.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author AD
 * @date 2022/5/31 17:29
 */
public class ScriptDemo {

    public static void main(String[] args) {
        display();
    }

    private static ScriptEngine getScriptEngine(){
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine engine = sem.getEngineByName("Nashorn");
        return engine;
    }
    private static void display(){
        ScriptEngine engine = getScriptEngine();
        System.out.println(engine.getFactory().getEngineName());
    }
}
