package com.unisofia.fmi.pfly.api.model;

import java.io.Serializable;

public class Project implements Serializable {

	private static final long serialVersionUID = -5059672515482196463L;
	
	private Long id;
	private String name;
	private String description;

	public Project() {
		id = 4545l;
		name = "Project";
		description = "Some description";
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
