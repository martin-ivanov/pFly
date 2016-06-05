package com.unisofia.fmi.pfly.api.model;

import java.io.Serializable;
import java.util.Date;

public class Project implements Serializable {

	private static final long serialVersionUID = -5059672515482196463L;

	private Long projectId;
	private Long creatorId;
	private Date dateCreated;
	private String description;
	private String name;

	public Project() {}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "[" + projectId + "] " + name;
	}
}
