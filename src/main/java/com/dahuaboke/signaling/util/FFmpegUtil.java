package com.dahuaboke.signaling.util;

import com.dahuaboke.signaling.constants.Constant;
import com.dahuaboke.signaling.model.Person;
import com.dahuaboke.signaling.model.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FFmpegUtil {

    //ffmpeg -re -i rtmp://192.168.43.190/live/livestream -f lavfi -i "anullsrc=channel_layout=stereo:sample_rate=44100" -i bcd.mp3
    // -filter_complex "[2:a]aformat=channel_layouts=stereo:sample_fmts=fltp:sample_rates=44100,adelay=0|0[a1];  [1:a]adelay=0|0[a2];  [0:a][a1][a2]amix=inputs=3:duration=first:dropout_transition=3[outa]"
    // -map 0:v -map "[outa]"  -c:v copy -c:a aac -ac 2 -f flv rtmp://192.168.43.190/live/abc02

//    public static void joinStream(String streamKey, String joinStreamKey) {
//        // 输入流的 SRS 地址
//        String srsInputUrl = Constant.SRS_RTMP_HOST + streamKey;
//        // 本地 MP3 文件路径
//        String localMp3Path = "C:/Users/23195/Desktop/upload/abc.mp3";
//        // 输出流的 SRS 地址
//        String srsOutputUrl = Constant.SRS_RTMP_HOST + joinStreamKey;
//
//        // 构建 ffmpeg 命令
//        String[] command = {
//                "C:\\soft\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe",
//                "-re",
//                "-i", srsInputUrl,
//                "-f", "lavfi",
//                "-i", "anullsrc=channel_layout=stereo:sample_rate=44100",
//                "-i", localMp3Path,
//                "-filter_complex", "[2:a]aformat=channel_layouts=stereo:sample_fmts=fltp:sample_rates=44100,adelay=0|0[a1];  [1:a]adelay=0|0[a2];  [0:a][a1][a2]amix=inputs=3:duration=first:dropout_transition=3[outa]",
//                "-map", "0:v",
//                "-map", "[outa]",
//                "-c:v", "copy",
//                "-c:a", "aac",
//                "-ac", "2",
//                "-f", "flv", srsOutputUrl
//        };
//
//        try {
//            // 执行 ffmpeg 命令
//            ProcessBuilder processBuilder = new ProcessBuilder(command);
//            Process process = processBuilder.start();
//
//            // 读取 ffmpeg 的输出
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//
//            // 等待 ffmpeg 进程结束
//            int exitCode = process.waitFor();
//            if (exitCode == 0) {
//                System.out.println("FFmpeg process completed successfully.");
//            } else {
//                System.err.println("FFmpeg process exited with error code: " + exitCode);
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    public static void joinStream(Room room, String joinStreamKey) {
        // 本地 MP3 文件路径
        String localMp3Path = Constant.localMp3Path;
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
