package dev.twiceb.common.constants;

public class PathConstants {
    public static final String AUTH_USER_ID_HEADER = "X-auth-user-id";
    public static final String USER_SERVICE = "localhost";
    public static final String DOMAIN = "localhost:8080";

    public static final String API_V1 = "/api/v1";
    public static final String UI_V1 = "/ui/v1";

    public static final String AUTH = "/auth";
    public static final String DASHBOARD = "/dashboard";
    public static final String UI_V1_AUTH = UI_V1 + AUTH;
    public static final String API_V1_AUTH = API_V1 + AUTH;
    public static final String UI_V1_DASHBOARD = UI_V1 + DASHBOARD;
    public static final String API_V1_DASHBOARD = API_V1 + DASHBOARD;

    public static final String USER_EMAIL = "/user/{email}";
    public static final String LOGIN = "/login";
    public static final String REGISTRATION_CHECK = "/registration/check";
    public static final String REGISTRATION_CODE = "/registration/code";
    public static final String REGISTRATION_ACTIVATE_CODE = "/registration/activate/{code}";
    public static final String REGISTRATION_CONFIRM = "/registration/confirm";

    public static final String PASSWORD = "/password";
    public static final String API_V1_PASSWORD = API_V1 + PASSWORD;
    public static final String UI_V1_PASSWORD = UI_V1 + PASSWORD;
    public static final String GET_PASSWORD_WITH_CRITERIA = "/{criteria}";
    public static final String TABLE_DOMAIN_PASSWORDS = "/domain/{domain}";
    public static final String UPDATE_DOMAIN_PASSWORD = "/password/update";
    public static final String GET_DECRYPTED_PASSWORD = "/password/{passwordId}";

    public static final String CREATE_GOAL = "/goals/new";
    public static final String UPDATE_GOAL = "/goals/update/{goalId}";
    public static final String DELETE_GOAL = "/goals/delete/{goalId}";
    public static final String DELETE_ALL_GOALS = "/goals/delete/all";
    public static final String GET_ALL_GOALS = "/goals/all";

    public static final String CREATE_SUBGOAL = "/subgoal/new/{goalId}";
    public static final String UDPATE_SUBGOAL = "/subgoal/update/{subgoalId}";
    public static final String DELETE_SUBGOAL = "/subgoal/delete/{subgoalId}";
    public static final String DELETE_ALL_SUBGOAL = "/subgoal/delete/all/{goalId}";


    public static final String TASK = "/task";
    public static final String UI_V1_TASK = UI_V1 + TASK;
    public static final String API_V1_TASK = API_V1 + TASK;
    public static final String UPDATE_TASK = "/task/update";
    public static final String DELETE_TASK = "/task/delete/{taskId}";
    public static final String DELETE_ALL_TASKS = "/task/delete/all";
    public static final String GET_TASKS = "/task";

    public static final String CREATE_SUBTASK = "/subtask/{taskId}";
    public static final String UDPATE_SUBTASK = "/subtask/{subtaskId}";
    public static final String DELETE_SUBTASK = "/subtask/delete/{subtaskId}";
    public static final String DELETE_ALL_SUBTASK = "/subtask/delete/all/{taskId}";
    public static final String GET_SUBTASKS_FOR_TASK = "/subtask/{taskId}";

    public static final String RECURRING_TASK = "/recurring";
    public static final String CREATE_RECURRING_TASK = "/recurring";
    public static final String GET_RECURRING_TASKS = "/reccuringtask";

    public static final String GET_ALL_TAGS = "/tags/all";
    public static final String GET_TAGS_TASK = "/tags/{task}";

    public static final String CREATE_CONTACT = "/contact/new";
    public static final String UPDATE_CONTACT = "/contact/{contactId}";
    public static final String DELETE_CONTACT = "/contact/delete/{contactId}";
    public static final String DELETE_ALL_CONTACTS = "/contact/delete/all";
    public static final String GET_CONTACTS = "/contact/all";
}
