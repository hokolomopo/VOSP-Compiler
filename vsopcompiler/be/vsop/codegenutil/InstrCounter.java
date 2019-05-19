package be.vsop.codegenutil;

import java.util.HashMap;

/**
 * This class is used to generate unique ids for temporary variables, while following the rules of llvm
 * (start from id 1 and then increment)
 */
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

    /**
     * Returns the next llvm id to use as a temporary variable
     *
     * @return a string representing the llvm id
     */
    public String getNextLlvmId() {
        return "%" + counter++;
    }

    /**
     * Returns a HashMap containing the next labels for a if expression, to be used with the constants above for
     * accessing the labels
     *
     * @return a HashMap<String, String> mapping the constants above to conditional labels
     */
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

    /**
     * Returns a HashMap containing the next labels for a while expression, to be used with the constants above for
     * accessing the labels
     *
     * @return a HashMap<String, String> mapping the constants above to loop labels
     */
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
