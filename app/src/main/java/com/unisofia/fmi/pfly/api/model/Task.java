package com.unisofia.fmi.pfly.api.model;

import com.unisofia.fmi.pfly.R;

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
    private boolean intImportant;
    private boolean extImportant;
    private boolean closed;
    private boolean simplified;
    private boolean cleared;
    private Date taskDeadline;
    private String desiredOutcome;
    private Long dependOn;
    private String notes;
    private Long projectId;
    private int flyScore;
    private TaskAction actionTaken;
    private Long eventId;

    public Task() {
        id = 4545l;
        name = "Task";
        description = "Some description";
        flyScore = new Random().nextInt(40);
        intImportant = getRandomBoolean();
        extImportant = getRandomBoolean();
        cleared = getRandomBoolean();
        simplified = getRandomBoolean();
        closed = getRandomBoolean();
        actionTaken = TaskAction.CLARIFY;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
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

    public boolean isIntImportant() {
        return intImportant;
    }

    public void setIntImportant(boolean intImportant) {
        this.intImportant = intImportant;
    }

    public boolean isExtImportant() {
        return extImportant;
    }

    public void setExtImportant(boolean extImportant) {
        this.extImportant = extImportant;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isSimplified() {
        return simplified;
    }

    public void setSimplified(boolean simplified) {
        this.simplified = simplified;
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
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

    public boolean getRandomBoolean(){
        return Math.random() < 0.5;
    }

    public TaskAction getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(TaskAction actionTaken) {
        this.actionTaken = actionTaken;
    }

    public static enum TaskAction {
        TRASH_NOTIFY("Trash & Notify", R.color.taskDarkBlue),
        TRANSFER_NOTIFY("Transfer & Notify", R.color.taskLightBlue),
        DELEGATE_FOLLOW_UP("Delegate & follow-up", R.color.taskDarkGreen),
        SCHEDULE_DEFER("Schedule & Defer", R.color.taskLightGreen),
        CLARIFY("Clarify", R.color.taskOrange),
        SIMPLIFY("Simplify", R.color.taskOrange),
        EXECUTE("Execute", R.color.taskRed);

        private final String text;
        private final int color;

        private TaskAction(final String text, int color){
            this.text = text;
            this.color = color;
        }

        @Override
        public String toString(){
            return text;
        }

        public int getColor() {
            return color;
        }
    }
}

