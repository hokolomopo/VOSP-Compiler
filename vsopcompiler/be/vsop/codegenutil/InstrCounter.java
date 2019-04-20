package be.vsop.codegenutil;

import java.util.HashMap;

public class InstrCounter {
    private int counter = 1;
    private int condLabelCounter = 1;
    private int loopCounter = 1;

    public final static String COND_ID = "COND_LABEL";
    public final static String COND_IF_LABEL = "COND_IF";
    public final static String COND_ELSE_LABEL = "COND_ELSE";
    public final static String COND_END_LABEL = "COND_END";


    public final static String LOOP_COND_LABEL = "LOOP_COND_LABEL";
    public final static String LOOP_START_LABEL = "LOOP_START_LABEL";
    public final static String LOOP_END_LABEL = "LOOP_END_LABEL";

    public InstrCounter() {
    }

    public String getNextLlvmId() {
        return "%" + counter++;
    }

    public String getLastLlvmId() {
        return "%" + (counter - 1);
    }

    public HashMap<String, String> getNextCondLabels(){
        HashMap<String, String> map = new HashMap<>();

        if(condLabelCounter != 1){
            map.put(COND_ID, "cond" + condLabelCounter);
            map.put(COND_IF_LABEL, "cond"+ condLabelCounter + ".true");
            map.put(COND_ELSE_LABEL, "cond" + condLabelCounter + ".false");
            map.put(COND_END_LABEL, "cond" + condLabelCounter + ".end");
        }
        else{
            map.put(COND_ID, "cond");
            map.put(COND_IF_LABEL, "cond.true");
            map.put(COND_ELSE_LABEL, "cond.false");
            map.put(COND_END_LABEL, "cond.end");
        }

        condLabelCounter++;

        return map;
    }

    public HashMap<String, String> getNextLoopLabel(){
        HashMap<String, String> loopLabels = new HashMap<>();

        if(loopCounter == 1){
            loopLabels.put(LOOP_START_LABEL, "loop.start");
            loopLabels.put(LOOP_END_LABEL, "loop.end");
            loopLabels.put(LOOP_COND_LABEL, "loop.cond");
        }
        else{
            loopLabels.put(LOOP_START_LABEL, "loop" + loopCounter + ".start");
            loopLabels.put(LOOP_END_LABEL, "loop" + loopCounter + ".end");
            loopLabels.put(LOOP_COND_LABEL, "loop" + loopCounter + ".cond");
        }

        loopCounter++;

        return loopLabels;
    }

}
