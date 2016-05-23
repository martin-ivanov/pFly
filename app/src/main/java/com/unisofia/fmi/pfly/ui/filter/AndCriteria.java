package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;

import java.util.ArrayList;
import java.util.List;

public class AndCriteria implements Criteria{
    List<Criteria> criterias;

    public AndCriteria(List<Criteria> criterias) {
        this.criterias = criterias;
    }

    @Override
    public List<Task> meetCriteria(List<Task> tasks) {
        List<Task> results = tasks;
        for (Criteria criteria : criterias){
            results = criteria.meetCriteria(results);
        }
        return results;
    }
}
