package com.unisofia.fmi.pfly.api.model;

import com.unisofia.fmi.pfly.R;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Task implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long taskId;
	private String name;
	private String notes;
	private String description;
	private String desiredOutcome;

	private Integer flyScore;
	private Integer intImportance;
	private Integer extImportance;
	private Integer closeness;
	private Integer simplicity;
	private Integer clearness;

	private Date dateCreated;
	private Date dateFinished;
	private Date deadline;
	private Date lastResponsibleMoment;

	private Integer status;
	private Integer recommendedAction;
	private Integer takenAction;
	private Long transferedTo;
	private Long delegatedTo;
	private Long dependOn;

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


	public Integer getCloseness() {
		return this.closeness;
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

	public Long getDelegatedTo() {
		return this.delegatedTo;
	}

	public void setDelegatedTo(Long delegatedTo) {
		this.delegatedTo = delegatedTo;
	}

	public Long getDependOn() {
		return this.dependOn;
	}

	public void setDependOn(Long dependOn) {
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

	public Integer getRecommendedAction() {
		return this.recommendedAction;
	}

	public void setRecommendedAction(Integer recommendedAction) {
		this.recommendedAction = recommendedAction;
	}

	public Integer getSimplicity() {
		return this.simplicity;
	}


	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Integer getTakenAction() {
		return this.takenAction;
	}

	public void setTakenAction(Integer takenAction) {
		this.takenAction = takenAction;
	}

	public Long getTransferedTo() {
		return this.transferedTo;
	}

	public void setTransferedTo(Long transferedTo) {
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

	public void setFlyScore(Integer flyScore) {
		this.flyScore = flyScore;
	}

	public Integer getIntImportance() {
		return intImportance;
	}

	public void setIntImportance(Integer intImportance) {
		this.intImportance = intImportance;
	}

	public Integer getExtImportance() {
		return extImportance;
	}

	public void setExtImportance(Integer extImportance) {
		this.extImportance = extImportance;
	}

	public void setCloseness(Integer closeness) {
		this.closeness = closeness;
	}

	public void setSimplicity(Integer simplicity) {
		this.simplicity = simplicity;
	}

	public Integer getClearness() {
		return clearness;
	}

	public void setClearness(Integer clearness) {
		this.clearness = clearness;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public enum TaskAction {
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

		TaskAction(final String text, int color, int index){
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

		public static TaskAction getAction(int index){
			return map.get(index);
		}

	}
}