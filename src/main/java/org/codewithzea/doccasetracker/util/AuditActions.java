package org.codewithzea.doccasetracker.util;

public final class AuditActions {
    private AuditActions() {}

    public static final String USER_REGISTERED = "USER_REGISTERED";
    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_LOGOUT = "USER_LOGOUT";

    public static final String PASSWORD_RESET_OTP_REQUESTED =
            "PASSWORD_RESET_OTP_REQUESTED";

    public static final String PASSWORD_RESET_OTP_VERIFIED =
            "PASSWORD_RESET_OTP_VERIFIED";

    public static final String PASSWORD_RESET =
            "PASSWORD_RESET";

    public static final String REFRESH_TOKEN_ROTATED =
            "REFRESH_TOKEN_ROTATED";

    public static final String USER_APPROVED =
            "USER_APPROVED";

    public static final String USER_REJECTED =
            "USER_REJECTED";

    public static final String USER_DELETED =
            "USER_DELETED";

    public static final String DOCTOR_CREATED = "DOCTOR_CREATED";

    public static final String DOCTOR_UPDATED = "DOCTOR_UPDATED";

    public static final String DOCTOR_DELETED = "DOCTOR_DELETED";

    public static final String DOCTOR_ACTIVATED = "DOCTOR_ACTIVATED";

    public static final String DOCTOR_DEACTIVATED = "DOCTOR_DEACTIVATED";
}
