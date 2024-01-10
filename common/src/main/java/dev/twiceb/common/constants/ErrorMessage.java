package dev.twiceb.common.constants;

public class ErrorMessage {
    public static final String JWT_TOKEN_EXPIRED = "JWT token is expired or invalid";

    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ID_NOT_FOUND = "User (id:%s) not found";
    public static final String AUTHENTICATION_ERROR = "Authentication required to access this resource.";
    public static final String AUTHORIZATION_ERROR = "Access Denied: You are not authorized to view/access this resource.";

    public static final String EMAIL_NOT_FOUND = "Email not found";
    public static final String EMAIL_NOT_VALID = "Please enter a valid email address.";
    public static final String EMAIL_HAS_ALREADY_BEEN_TAKEN = "Email has already been taken.";
    public static final String BLANK_NAME = "What’s your name?";
    public static final String NAME_NOT_VALID = "Please enter a valid name.";
    public static final String FUll_NAME_NOT_VALID = "Please enter a valid name.";

    public static final String PASSWORDS_NOT_MATCH = "Passwords do not match.";
    public static final String PASSWORD_NOT_VALID = "Please enter a valid password";
    public static final String INCORRECT_PASSWORD = "The password you entered was incorrect.";
    public static final String INVALID_PASSWORD_RESET_CODE = "Password reset code is invalid!";
    public static final String PASSWORD_LENGTH_ERROR = "Your password needs to be at least 8 characters";
    public static final String EMPTY_PASSWORD = "Password cannot be empty.";
    public static final String EMPTY_CURRENT_PASSWORD = "Current password cannot be empty.";
    public static final String EMPTY_PASSWORD_CONFIRMATION = "Password confirmation cannot be empty.";
    public static final String SHORT_PASSWORD = "Your password needs to be at least 8 characters. Please enter a longer one.";
    public static final String SAME_SAVED_PASSWORD = "Your new password cannot be the same as your old password";
    public static final String NO_DOMAIN_PASSWORDS = "You do not have any passwords saved.";
    public static final String NO_PASSWORD_FOR_DOMAIN = "No password exists for ";
    public static final String DOMAIN_ALREADY_EXIST = "Domain Already Exist";
    public static final String EMPTY_DOMAIN = "Domain cannot be empty.";

    public static final String EMPTY_GOAL_TITLE = "Please enter a valid title.";
    public static final String EMPTY_GOAL_TARGET_DATE = "Goal must have a target date.";
    public static final String EXCEED_GOAL_TITLE_SIZE = "Title must not exceed 100 characters.";
    public static final String EXCEED_GOAL_DESC_SIZE = "Description must not exceed 255 characters.";
    public static final String WRONG_DATE_PATTERN = "Please make sure date is formated like -> yyyy-MM-dd.";
    public static final String EMPTY_TARGET_DATE = "Please enter a target date.";

    public static final String EMPTY_SUBGOAL_TITLE = "Please enter a valid title.";
    public static final String EMPTY_SUBGOAL_TARGET_DATE = "Goal must have a target date.";
    public static final String EXCEED_SUBGOAL_TITLE_SIZE = "Title must not exceed 100 characters.";
    public static final String EXCEED_SUBGOAL_DESC_SIZE = "Description must not exceed 255 characters.";
    public static final String INVALID_ID_PROVIDED = "Please enter a valid goal id.";

    public static final String EXCEED_SUBGOAL_SIZE = "You can only have a max of 10 subgoals for each goal";
    public static final String EXCEED_SUBTASK_SIZE = "You can only have a max of 10 Subtasks per task.";

    public static final String NO_TASK_FOUND = "No task has been found.";
    public static final String EMPTY_TASK_TITLE = "Please enter a valid title.";
    public static final String EMPTY_TASK_TARGET_DATE = "Goal must have a target date.";
    public static final String EMPTY_TASK_DESC = "Please enter a valid Description.";
    public static final String EXCEED_TASK_TITLE_SIZE = "Title must not exceed 100 characters.";
    public static final String EXCEED_TASK_DESC_SIZE = "Description must not exceed 255 characters.";
    public static final String EMPTY_DUE_DATE = "Please enter a due date.";
    public static final String EMPTY_RECURRENCE_PATTERN = "Please enter 1 of the following options provided.";
    public static final String DUPLICATE_RECURRENCE_TASK = "A recurring task with the same name already exist.";

    public static final String EMPTY_SUBTASK_TITLE = "Please enter a valid title.";
    public static final String EMPTY_SUBTASK_DUE_DATE = "Goal must have a due date.";
    public static final String EXCEED_SUBTASK_TITLE_SIZE = "Title must not exceed 100 characters.";
    public static final String EXCEED_SUBTASK_DESC_SIZE = "Description must not exceed 255 characters.";
    public static final String INVALID_ID_PROVIDED_TASK = "Please enter a valid task id.";

    public static final String ACTIVATION_CODE_NOT_FOUND = "Activation code not found.";
    public static final String ACCOUNT_ALREADY_VERIFIED = "Account already verified.";
    public static final String ACTIVATION_CODE_EXPIRED = "Activation code expired.";
    public static final String ACTIVATION_CODE_GENERATION_FAIL = "Error generating activation code, please try again later.";

    public static final String INTERNAL_SERVER_ERROR = "Internal server error occurred, please try again later.";
    public static final String UNAUTHORIZED = "Unauthorized access to user resource.";
}
