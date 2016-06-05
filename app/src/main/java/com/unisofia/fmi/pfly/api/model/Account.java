package com.unisofia.fmi.pfly.api.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long accountId;
	private String deviceId;
	private String email;
	private String name;
	private String userId;
	private List<Task> tasks;

	public Account() {
	}

	public Long getAccountId() {
		return this.accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	//bi-directional many-to-one association to Task

	public List<Task> getTasks() {
		return this.tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public Task addTask(Task task) {
		getTasks().add(task);
		task.setAccount(this);

		return task;
	}

	public Task removeTask(Task task) {
		getTasks().remove(task);
		task.setAccount(null);

		return task;
	}


	@Override
	public String toString() {
		return "[" + accountId + "] " + name;
	}
}