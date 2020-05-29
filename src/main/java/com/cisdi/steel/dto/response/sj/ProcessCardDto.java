package com.cisdi.steel.dto.response.sj;

import java.util.List;

public class ProcessCardDto {
    private ProcessCard processCard;

//    private List<ProcessKey> keys;

    private List<ProcessGoal> goals;

    public ProcessCard getProcessCard() {
        return processCard;
    }

    public void setProcessCard(ProcessCard processCard) {
        this.processCard = processCard;
    }

//    public List<ProcessKey> getKeys() {
//        return keys;
//    }
//
//    public void setKeys(List<ProcessKey> keys) {
//        this.keys = keys;
//    }

    public List<ProcessGoal> getGoals() {
        return goals;
    }

    public void setGoals(List<ProcessGoal> goals) {
        this.goals = goals;
    }
}
