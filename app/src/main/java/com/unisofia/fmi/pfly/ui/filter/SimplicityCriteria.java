package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.List;

public class SimplicityCriteria implements Criteria{
    private static SimplicityCriteria simplicityCriteria;

    private SimplicityCriteria() {}

    public static SimplicityCriteria getInstance() {
        if (simplicityCriteria == null) {
            simplicityCriteria = new SimplicityCriteria();
        }
        return simplicityCriteria;
    }

    @Override
    public List<Task> meetCriteria(List<Task> tasks, String filter) {
        List<Task> results = new ArrayList<Task>();
        for (Task task : tasks) {
            if(task.getSimplicity()!= null && task.getSimplicity() > 0){
                results.add(task);
            }
        }
        return results;
    }
}
