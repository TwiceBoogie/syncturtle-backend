package dev.twiceb.common.constants;

public class PathConstants {
    public static final String AUTH_USER_ID_HEADER = "X-auth-user-id";
    public static final String AUTH_USER_AGENT_HEADER = "X-User-Agent";
    public static final String AUTH_USER_IP_HEADER = "X-User-Ip";
    public static final String AUTH_USER_DEVICE_KEY = "X-User-DeviceKey";
    public static final String USER_SERVICE = "localhost";

    public static final String API_V1 = "/api/v1";
    public static final String UI_V1 = "/ui/v1";

    // USER_SERVICE
    public static final String AUTH = "/auth";
    public static final String UI_V1_AUTH = UI_V1 + AUTH;
    public static final String LOGIN = "/login";
    public static final String REGISTRATION_CHECK = "/registration/check";
    public static final String REGISTRATION_CODE = "/registration/code";
    public static final String REGISTRATION_ACTIVATE_CODE = "/registration/activate/{code}";
    public static final String REGISTRATION_CONFIRM = "/registration/confirm";
    public static final String FORGOT = "/forgot";
    public static final String FORGOT_USERNAME = FORGOT + "/username";
    public static final String FORGOT_PASSWORD = FORGOT + "/password";
    public static final String VERIFY_OTP = "/verify-otp";
    public static final String VERIFY_DEVICE_VERIFICATION = "verify/device/{token}";
    public static final String RESET = "/reset/{token}";
    public static final String RESET_CODE = RESET + "/{code}";
    public static final String RESET_CURRENT = RESET + "/current";


    public static final String USER = "/user";
    public static final String API_V1_AUTH = API_V1 + AUTH;
    public static final String GET_USER_EMAIL = "/{userId}";
    public static final String API_V1_USER = API_V1 + USER;
    public static final String USER_EMAIL = "/user/{email}";
    public static final String NOTIFICATION_USER_ID = "/notification/{userId}";
    public static final String ADD_NOTIFICATION = NOTIFICATION_USER_ID + "/add";
    public static final String SUB_NOTIFICATION = NOTIFICATION_USER_ID + "/subtract";
    public static final String TOKEN = "/token";

    public static final String UI_V1_USER = UI_V1 + USER;
    public static final String UI_V1_USER_SETTINGS_UPDATE = UI_V1_USER + "/settings/update";
    public static final String USERNAME = "/username";
    public static final String EMAIL = "/email";
    public static final String PHONE = "/phone";
    public static final String COUNTRY = "/country";
    public static final String GENDER = "/gender";
    public static final String AVATAR = "/avatar";
    public static final String SET_AVATAR = AVATAR + "/{userProfileId}";

    // PASSWORD_SERVICE
    public static final String PASSWORD = "/password";
    public static final String UI_V1_PASSWORD = UI_V1 + PASSWORD;
    public static final String GET_PASSWORD_INFO = "/{keychainId}/info";
    public static final String FAVORITE_PASSWORD = "/favorite/{passwordId}";
    public static final String GET_PASSWORD_WITH_CRITERIA = "/{criteria}";
    public static final String GET_DECRYPTED_PASSWORD = "/decrypt/{passwordId}";
    public static final String DELETE_ALL = "/delete";
    public static final String UPDATE_PASSWORD = "/update/{passwordId}";
    public static final String UPDATE_PASSWORD_USERNAME = "/username/update/{passwordId}";
    public static final String UPDATE_PASSWORD_NOTES = "/notes/update/{passwordId}";
    public static final String DELETE_PASSWORD = "/delete/{passwordId}";
    public static final String GENERATE_RANDOM_PASSWORD = "/generate/{length}";
    public static final String SEARCH_BY_QUERY = "/search";
    public static final String API_V1_PASSWORD = API_V1 + PASSWORD;

    // GOALS_SERVICE
    public static final String GOAL = "/goals";
    public static final String UI_V1_GOAL = UI_V1 + GOAL;
    public static final String UPDATE_GOAL = "/update/{goalId}";
    public static final String DELETE_GOAL = "/delete/{goalId}";
    public static final String DELETE_ALL_GOALS = "/delete/all";
    public static final String SUB_GOAL = "/subgoal";
    public static final String CREATE_SUB_GOAL = SUB_GOAL + "/{goalId}";
    public static final String UPDATE_SUB_GOAL = SUB_GOAL + "/update/{subgoalId}";
    public static final String DELETE_SUB_GOAL = SUB_GOAL + "/delete/{subgoalId}";
    public static final String DELETE_ALL_SUB_GOAL = SUB_GOAL + "/delete/all/{goalId}";

    // TASK_SERVICE
    public static final String TASK = "/task";
    public static final String UI_V1_TASK = UI_V1 + TASK;
    public static final String UPLOAD_ATTACHMENTS = "/upload/attachments/{taskId}";
    public static final String GET_ATTACHMENT_FILE = "/file/{taskAttachmentId}";
    public static final String UPDATE_TASK = "/update/{taskId}";
    public static final String DELETE_TASK = "/delete/{taskId}";
    public static final String DELETE_ALL_TASKS = "/delete/all";
    public static final String SUB_TASK = "/subtask";
    public static final String CREATE_SUBTASK = SUB_TASK + "/{taskId}";
    public static final String UPDATE_SUBTASK = SUB_TASK + "/{subtaskId}";
    public static final String DELETE_SUBTASK = SUB_TASK + "/delete/{subtaskId}";
    public static final String DELETE_ALL_SUBTASK = SUB_TASK + "/delete/{taskId}";
    public static final String GET_SUBTASKS_FOR_TASK = SUB_TASK + "/{taskId}";
    public static final String API_V1_TASK = API_V1 + TASK;

    public static final String RECURRING_TASK = "/recurring";
    public static final String CREATE_RECURRING_TASK = "/recurring";
    public static final String GET_RECURRING_TASKS = "/reccuringtask";

    // TAG_SERVICE ???
    public static final String GET_ALL_TAGS = "/tags/all";
    public static final String GET_TAGS_TASK = "/tags/{task}";

    // CONTACT_SERVICE
    public static final String CREATE_CONTACT = "/contact/new";
    public static final String UPDATE_CONTACT = "/contact/{contactId}";
    public static final String DELETE_CONTACT = "/contact/delete/{contactId}";
    public static final String DELETE_ALL_CONTACTS = "/contact/delete/all";
    public static final String GET_CONTACTS = "/contact/all";

    // NOTIFICATION_SERVICE
    public static final String NOTIFICATION = "/notification";
    public static final String UI_V1_NOTIFICATION = UI_V1 + NOTIFICATION;
    public static final String LIST = "/list";
    public static final String UPDATE_READ_STATE = "/{notificationId}";
    public static final String API_V1_NOTIFICATION = API_V1 + NOTIFICATION;
    public static final String BATCH_NOTIFICATION = "/batch";

    // FILE_SERVICE
    public static final String API_V1_FILE = API_V1 + "/file";
    public static final String UPLOAD = "/upload/{bucket}";
    public static final String UPLOAD_MULTIPLE = "/upload/multiple/{bucket}";
    public static final String GET_FILE_IMAGE = "/{bucket}";
    public static final String DELETE_FILE_IMAGE = "/delete/{bucket}";

}
