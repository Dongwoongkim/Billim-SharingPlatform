package dblab.sharing_platform.config.oauth.provider;


public class OAuthInfo {
    public static final String KAKAO = "kakao";
    public static final String GOOGLE = "google";
    public static final String NAVER = "naver";
    public static final String KAKAO_ACCOUNT = "kakao_account";
    public static final String PROFILE_NICKNAME = "profile_nickname";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String ACCESS_TOKEN_URL_KAKAO = "https://kauth.kakao.com/oauth/token";
    public static final String ACCESS_TOKEN_URL_GOOGLE = "https://oauth2.googleapis.com/token";
    public static final String ACCESS_TOKEN_URL_NAVER = "https://nid.naver.com/oauth2.0/token";
    public static final String USER_INFO_URL_KAKAO = "https://kapi.kakao.com/v2/user/me";
    public static final String USER_INFO_URL_GOOGLE = "https://www.googleapis.com/oauth2/v1/userinfo";
    public static final String USER_INFO_URL_NAVER = "https://openapi.naver.com/v1/nid/me";
    public static final String UNLINK_URL_KAKAO = "https://kapi.kakao.com/v1/user/unlink";
    public static final String UNLINK_URL_GOOGLE = "https://oauth2.googleapis.com/revoke?token=";
    public static final String UNLINK_URL_NAVER_FRONT = "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=nX6jP4IgsXNpJRqbg0Q5&client_secret=1vNwHm8Yri&access_token=";
    public static final String UNLINK_URL_NAVER_END = "&service_provider=NAVER";
    public static final String GRANT_TYPE = "grant_type=authorwization_code";
    public static final String CLIENT_ID = "&client_id=";
    public static final String CLIENT_SECRET = "&client_secret=";
    public static final String REDIRECT_URI = "&redirect_uri=";
    public static final String CODE = "&code=";
}
