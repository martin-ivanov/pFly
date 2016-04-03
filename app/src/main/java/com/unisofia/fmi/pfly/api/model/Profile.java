package com.unisofia.fmi.pfly.api.model;

import com.google.gson.annotations.SerializedName;

public class Profile {

	@SerializedName("id")
	private long id;

	@SerializedName("name")
	private String name;

	@SerializedName("email")
	private String email;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
