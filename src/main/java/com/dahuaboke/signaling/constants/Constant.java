package com.dahuaboke.signaling.constants;

import java.io.IOException;
import java.util.Properties;

public class Constant {

    public static final String SUCCESS_CODE = "00";

    public static final String FAIL_CODE = "01";

    public static int SERVER_PORT;

    public static String FILE_ROOT_PATH;

    public static String LOCAL_ROOT_PATH;

    public static String WATER_PNG;

    public static String localMp3Path;

    public static String FILE_DOWNLOAD_URL;

    public static String SRS_SERVER_HOST;

    public static int SRS_SERVER_SSH_PORT = 22;

    public static String SRS_SERVER_USERNAME;

    public static String SRS_SERVER_PASSWORD;

    public static String SRS_RTMP_HOST;

    public static String FFMPEG_PATH;


    static{
        Properties properties = new Properties();
        try {
            properties.load(Constant.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SERVER_PORT = Integer.parseInt(properties.getProperty("serverPort"));
        FILE_ROOT_PATH = properties.getProperty("fileRootPath");
        LOCAL_ROOT_PATH = properties.getProperty("localRootPath");
        WATER_PNG = properties.getProperty("waterPng");
        localMp3Path = properties.getProperty("localMp3Path");
        FILE_DOWNLOAD_URL = properties.getProperty("fileDownloadUrl");
        SRS_SERVER_HOST = properties.getProperty("srsServerHost");
        SRS_SERVER_SSH_PORT = Integer.parseInt(properties.getProperty("srsServerSshPort"));
        SRS_SERVER_USERNAME = properties.getProperty("srsServerUsername");
        SRS_SERVER_PASSWORD = properties.getProperty("srsServerPassword");
        SRS_RTMP_HOST = properties.getProperty("srsRtmpHost");
        FFMPEG_PATH = properties.getProperty("ffmpegPath");
    }


}
