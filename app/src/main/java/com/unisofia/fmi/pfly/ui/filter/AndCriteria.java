package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AndCriteria implements Criteria{
    Set<Criteria> criterias;

    public AndCriteria(Set<Criteria> criterias) {
        this.criterias = criterias;
    }

    @Override
    public List<Task> meetCriteria(List<Task> tasks, String filter) {
        List<Task> results = tasks;
        for (Criteria criteria : criterias){
            results = criteria.meetCriteria(results, filter);
        }
        return results;
    }
}
