package com.example.sow2.translate.configuration;

public final class Constants {

    public class Server {
        public static final String BASE_URL_API = "http://128.199.204.51";
        public static final String BACKUP_SERVER_URL_API = "http://192.168.0.101";
        public static final String PORT = "3000";
        public static final String URL_API = BASE_URL_API + ":" + PORT + "/";
        public static final String CHECK_STATUS_URL = URL_API + "status";
        public static final String BACKUP_URL_API = BACKUP_SERVER_URL_API + ":" + PORT + "/";
        public static final String SOURCE_TALK = URL_API + "talk/";
        public static final String BACKUP_SOURCE_TALK = BACKUP_URL_API + "talk/";
        public static final String SOURCE_TRANSLATE = URL_API + "translate/";
        public static final String BACKUP_SOURCE_TRANSLATE = BACKUP_URL_API + "translate/";
    }

    public class Google {
        public static final String YOUTUBE_API_KEY = "AIzaSyA05lS54Cdc0U0w4HYp6ZjnVvOPSUbJADc";
    }

}
