package org.codewithzea.doccasetracker.service.impl;

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
}
