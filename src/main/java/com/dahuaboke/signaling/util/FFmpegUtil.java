package com.dahuaboke.signaling.util;

import com.dahuaboke.signaling.constants.Constant;
import com.dahuaboke.signaling.model.Person;
import com.dahuaboke.signaling.model.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FFmpegUtil {

    public static void joinStream(Room room, String joinStreamKey,String fileName) {
        // 本地 MP3 文件路径
        String localMp3Path = Constant.LOCAL_ROOT_PATH + fileName;
        // 输出流的 SRS 地址
        String srsOutputUrl = Constant.SRS_RTMP_HOST + joinStreamKey;
        System.out.println("推送地址 = " + srsOutputUrl);
        // 构建 ffmpeg 命令
        String[] command = {
                Constant.FFMPEG_PATH,
                "-re",
                "-i", localMp3Path,
                "-c:a", "aac",
                "-b:a","64k",
                "-f", "flv", srsOutputUrl
        };

        try {
            // 执行 ffmpeg 命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            room.setProcess(process);
            // 读取 ffmpeg 的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待 ffmpeg 进程结束
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("FFmpeg process completed successfully.");
            } else {
                System.err.println("FFmpeg process exited with error code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void terminateProcess(Process process) {
        if (process != null) {
            process.destroyForcibly(); // 强制终止进程
            System.out.println("FFmpeg process has been terminated.");
        }
    }
}
