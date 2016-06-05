package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ClosenessCriteria implements Criteria{
    private static ClosenessCriteria closenessCriteria;

    private ClosenessCriteria() {}

    public static ClosenessCriteria getInstance(){
        if (closenessCriteria == null){
            closenessCriteria = new ClosenessCriteria();
        }

        return closenessCriteria;
    }

    @Override
    public List<Task> meetCriteria(List<Task> tasks, String filter) {
        List<Task> results = new ArrayList<Task>();
        for (Task task : tasks) {
            if(task.getCloseness()!= null && task.getCloseness()> 0){
                results.add(task);
            }
        }
        return results;
    }
}
