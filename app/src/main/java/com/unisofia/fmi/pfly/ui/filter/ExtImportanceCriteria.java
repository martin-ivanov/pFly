package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ExtImportanceCriteria implements Criteria {
    private static ExtImportanceCriteria extImportanceCriteria;

    private ExtImportanceCriteria() {}

    public static ExtImportanceCriteria getInstance(){
        if (extImportanceCriteria == null){
            extImportanceCriteria = new ExtImportanceCriteria();
        }
        return extImportanceCriteria;
    }

    @Override
    public List<Task> meetCriteria(List<Task> tasks, String filter) {
        List<Task> results = new ArrayList<Task>();
        for (Task task : tasks) {
            if (task.getExtImportance() != null && task.getExtImportance() > 0) {
                results.add(task);
            }
        }
        return results;
    }
}
