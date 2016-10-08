package com.unisofia.fmi.pfly.api;

public class ApiConstants {

    private ApiConstants() {
        // forbid instantiation
    }

    public static final String API_BASE_URL = "https://192.168.0.105:8443/rest/api";
    public static final String PROJECT_API_METHOD = "/projects";
    public static final String ACCOUNT_API_METHOD = "/accounts";
    public static final String TASK_API_METHOD = "/tasks";
    public static final String NO_AUTH_API_METHOD = "/auth";

}
