package com.test4x.lib.oauth_jwt.oauth;


public final class OAuthConstants {
    public static final String OAUTH_RESPONSE_TYPE = "response_type";
    public static final String OAUTH_CLIENT_ID = "client_id";
    public static final String OAUTH_CLIENT_SECRET = "client_secret";
    public static final String OAUTH_REDIRECT_URI = "redirect_uri";
    public static final String OAUTH_SCOPE = "scope";
    public static final String OAUTH_STATE = "state";
    public static final String OAUTH_GRANT_TYPE = "grant_type";
    public static final String OAUTH_HEADER_NAME = "Bearer";
    public static final String OAUTH_CODE = "code";
    public static final String OAUTH_ACCESS_TOKEN = "access_token";
    public static final String OAUTH_EXPIRES_IN = "expires_in";
    public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
    public static final String OAUTH_TOKEN_TYPE = "token_type";
    public static final String OAUTH_TOKEN = "oauth_token";

    public enum ParameterStyle {
        BODY("body"),
        QUERY("query"),
        HEADER("header");

        private String parameterStyle;

        private ParameterStyle(String parameterStyle) {
            this.parameterStyle = parameterStyle;
        }

        public String toString() {
            return this.parameterStyle;
        }
    }


    public static final class HttpMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";

        public HttpMethod() {
        }
    }

    public enum GrantType {
        // NONE("none"),
        AUTHORIZATION_CODE("authorization_code"),
        IMPLICIT("implicit"),
        PASSWORD("password"),
        REFRESH_TOKEN("refresh_token"),
        CLIENT_CREDENTIALS("client_credentials"),
        JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer");

        private String grantType;

        GrantType(String grantType) {
            this.grantType = grantType;
        }

        @Override
        public String toString() {
            return grantType;
        }
    }

}
