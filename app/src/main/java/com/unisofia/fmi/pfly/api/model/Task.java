package com.unisofia.fmi.pfly.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class Task implements Serializable {

    private static final long serialVersionUID = -8810075597143046213L;

    private Long id;
    private String name;
    private Date dateCreated;
    private String description;
    private TaskStatus status;
    private Date dateFinished;
    private String importance;
    private String closeness;
    private String simplicity;
    private Date taskDeadline;
    private String desiredOutcome;
    private long dependOn;
    private String notes;
    private Long projectId;
    private int flyScore;

    public Task() {
        id = 4545l;
        name = "Task";
        description = "Some description";
        flyScore = new Random().nextInt(40);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getCloseness() {
        return closeness;
    }

    public void setCloseness(String closeness) {
        this.closeness = closeness;
    }

    public String getSimplicity() {
        return simplicity;
    }

    public void setSimplicity(String simplicity) {
        this.simplicity = simplicity;
    }

    public Date getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(Date taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public String getDesiredOutcome() {
        return desiredOutcome;
    }

    public void setDesiredOutcome(String desiredOutcome) {
        this.desiredOutcome = desiredOutcome;
    }

    public long getDependOn() {
        return dependOn;
    }

    public void setDependOn(long dependOn) {
        this.dependOn = dependOn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public int getFlyScore() {
        return flyScore;
    }

    public void setFlyScore(int flyScore) {
        this.flyScore = flyScore;
    }

    public enum TaskStatus {INITIALIZING, IN_PROGRESS, COMPLETE}

    ;

}
