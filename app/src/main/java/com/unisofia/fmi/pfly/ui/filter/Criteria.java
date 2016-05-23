package com.unisofia.fmi.pfly.ui.filter;

import com.unisofia.fmi.pfly.api.model.Task;
import java.util.List;

public interface Criteria {
    List<Task> meetCriteria(List<Task> tasks);
}
