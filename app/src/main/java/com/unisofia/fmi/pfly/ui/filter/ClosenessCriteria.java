package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ClosenessCriteria implements Criteria{
    @Override
    public List<Task> meetCriteria(List<Task> tasks) {
        List<Task> results = new ArrayList<Task>();
        for (Task task : tasks) {
            if(task.getCloseness()!= null && task.getCloseness()> 0){
                results.add(task);
            }
        }
        return results;
    }
}
