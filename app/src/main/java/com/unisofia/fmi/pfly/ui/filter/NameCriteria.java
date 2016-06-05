package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.List;

public class NameCriteria implements Criteria{
    private static NameCriteria nameCriteria;

    private NameCriteria() {}

    public static NameCriteria getInstance() {
        if (nameCriteria == null) {
            nameCriteria = new NameCriteria();
        }
        return nameCriteria;
    }

    @Override
    public List<Task> meetCriteria(List<Task> tasks, String filterString) {
        List<Task> results = new ArrayList<>();
        for (Task task : tasks) {
            if(task.getName().toLowerCase().contains(filterString.toLowerCase())){
                results.add(task);
            }
        }
        return results;
    }
}
