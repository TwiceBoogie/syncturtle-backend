package dev.twiceb.workspace_service.enums;

public enum WorkspaceRole {
    ADMIN(20), MEMBER(15), GUEST(5);

    public final int code;

    WorkspaceRole(int c) {
        this.code = c;
    }

    public static WorkspaceRole from(int c) {
        for (WorkspaceRole r : values()) {
            if (r.code == c) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unkown role code: " + c);
    }
}
