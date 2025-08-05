package com.example.reminder.config;

public class KeycloakConstants {

    private KeycloakConstants() {}

    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ACCESS_TOKEN = "access_token";

    public static final String TOKEN_PATH = "/realms/master/protocol/openid-connect/token";
    public static final String LOGOUT_PATH_TEMPLATE = "/realms/%s/protocol/openid-connect/logout";
    public static final String USERS_PATH_TEMPLATE = "/admin/realms/%s/users";

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String CREDENTIAL_TYPE = "type";
    public static final String CREDENTIAL_PASSWORD = "password";
    public static final String CREDENTIAL_VALUE = "value";
    public static final String CREDENTIAL_TEMPORARY = "temporary";
}