package com.unisofia.fmi.pfly.api.model;

import com.unisofia.fmi.pfly.R;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Task implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long taskId;
	private int closeness;
	private Date dateCreated;
	private Date dateFinished;
	private Date deadline;
	private Integer delegatedTo;
	private Integer dependOn;
	private String description;
	private String desiredOutcome;
	private int flyScore;
	private int importance;
	private Date lastResponsibleMoment;
	private String name;
	private String notes;
	private String recommendedAction;
	private int simplicity;
	private int status;
	private String takenAction;
	private Integer transferedTo;
	private Account account;
	private Project project;
	private Long eventId;



	public Task() {
	}

	public Long getTaskId() {
		return this.taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}


	public int getCloseness() {
		return this.closeness;
	}

	public void setCloseness(int closeness) {
		this.closeness = closeness;
	}

	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateFinished() {
		return this.dateFinished;
	}

	public void setDateFinished(Date dateFinished) {
		this.dateFinished = dateFinished;
	}

	public Date getDeadline() {
		return this.deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Integer getDelegatedTo() {
		return this.delegatedTo;
	}

	public void setDelegatedTo(Integer delegatedTo) {
		this.delegatedTo = delegatedTo;
	}

	public Integer getDependOn() {
		return this.dependOn;
	}

	public void setDependOn(Integer dependOn) {
		this.dependOn = dependOn;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDesiredOutcome() {
		return this.desiredOutcome;
	}

	public void setDesiredOutcome(String desiredOutcome) {
		this.desiredOutcome = desiredOutcome;
	}

	public int getFlyScore() {
		return this.flyScore;
	}

	public void setFlyScore(int flyScore) {
		this.flyScore = flyScore;
	}


	public int getImportance() {
		return this.importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}

	public Date getLastResponsibleMoment() {
		return this.lastResponsibleMoment;
	}

	public void setLastResponsibleMoment(Date lastResponsibleMoment) {
		this.lastResponsibleMoment = lastResponsibleMoment;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getRecommendedAction() {
		return this.recommendedAction;
	}

	public void setRecommendedAction(String recommendedAction) {
		this.recommendedAction = recommendedAction;
	}

	public int getSimplicity() {
		return this.simplicity;
	}

	public void setSimplicity(int simplicity) {
		this.simplicity = simplicity;
	}


	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTakenAction() {
		return this.takenAction;
	}

	public void setTakenAction(String takenAction) {
		this.takenAction = takenAction;
	}

	public Integer getTransferedTo() {
		return this.transferedTo;
	}

	public void setTransferedTo(Integer transferedTo) {
		this.transferedTo = transferedTo;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Project getProject() {
		return this.project;
	}
	
	public void setProject(Project project) {
		this.project = project;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public static enum TaskAction {
		TRASH_NOTIFY("Trash & Notify", R.color.taskDarkBlue, 0),
		TRANSFER_NOTIFY("Transfer & Notify", R.color.taskLightBlue, 1),
		DELEGATE_FOLLOW_UP("Delegate & follow-up", R.color.taskDarkGreen, 2),
		SCHEDULE_DEFER("Schedule & Defer", R.color.taskLightGreen, 3),
		CLARIFY("Clarify", R.color.taskOrange, 4),
		SIMPLIFY("Simplify", R.color.taskOrange, 5),
		EXECUTE("Execute", R.color.taskRed, 6);

		private static Map<Integer, TaskAction> map = new HashMap<>();
		static {
			for (TaskAction taskAction : TaskAction.values()){
				map.put(taskAction.getIndex(), taskAction);
			}
		}

		private final String text;
		private final int color;
		private final int index;

		private TaskAction(final String text, int color, int index){
			this.text = text;
			this.color = color;
			this.index = index;
		}

		@Override
		public String toString(){
			return text;
		}

		public int getColor() {
			return color;
		}

		public int getIndex(){
			return index;
		}
	}
}