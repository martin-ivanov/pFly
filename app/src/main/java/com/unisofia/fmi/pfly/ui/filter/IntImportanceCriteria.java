package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.List;

public class IntImportanceCriteria implements Criteria{
    private static IntImportanceCriteria intImportanceCriteria;

    private IntImportanceCriteria() {}

    public static IntImportanceCriteria getInstance(){
        if (intImportanceCriteria == null){
            intImportanceCriteria = new IntImportanceCriteria();
        }
        return intImportanceCriteria;
    }

    @Override
    public List<Task> meetCriteria(List<Task> tasks, String filter) {
        List<Task> results = new ArrayList<Task>();
        for (Task task : tasks) {
            if(task.getIntImportance() != null && task.getIntImportance() > 0){
                results.add(task);
            }
        }
        return results;
    }
}
