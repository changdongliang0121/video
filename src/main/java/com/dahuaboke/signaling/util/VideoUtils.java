package com.dahuaboke.signaling.util;

import com.dahuaboke.signaling.constants.Constant;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * ClassName: VideoUtils
 * Package: com.example.srstest
 * Description:
 *
 * @Author zhangdalu
 * @Create 2025/1/14 16:41
 * @Version 1.0
 */
public class VideoUtils {

    public static void main(String[] args) {
        AddWatermark("12");
    }



    /**
     * 下载文件到本地，加水印，抽帧
     * @param relativePath
     */
    public static void AddWatermark(String relativePath) {
        List<String> fileList = null;
        //获取全mp4文件列表
        while (true) {
            fileList = findFileNames(Constant.FILE_ROOT_PATH + relativePath);
            if (fileList != null) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }

        if (fileList == null) {
            return;
        }

        File localDir = new File(Constant.LOCAL_ROOT_PATH + relativePath);
        if (!localDir.exists()) {
            localDir.mkdirs();
        }
        for (int i = 0; i < fileList.size(); i++) {
            String fileName = fileList.get(i);
            String basename = fileName.substring(0, fileName.lastIndexOf(".mp4"));
            //远程文件路径
            String remoteFilePath = Constant.FILE_DOWNLOAD_URL + relativePath + "/" + fileName;
            //远程文件下载到本地的文件夹，每个远程文件单独建一个文件夹
            String localFileDir = Constant.LOCAL_ROOT_PATH + relativePath + "/" + basename;
            //本地写出的全路径名
            String localFilePath = localFileDir + "/" + fileName;
            //本地水印文件的全路径名
            String waterFullPath = localFilePath.substring(0, localFilePath.lastIndexOf(".mp4")) + "_water.mp4";
            File filedir = new File(localFileDir);
            if (!filedir.exists()) {
                filedir.mkdirs();
            }
            //下载文件到本地
            downloadFile(remoteFilePath, localFilePath);

            if(localFilePath.indexOf("_") == -1){
                //加水印
                videoWaterMaker(localFilePath, waterFullPath, Constant.WATER_PNG);
                //抽帧
                videoFrameExtractor(waterFullPath, localFileDir, 5000);
            }
        }
    }

    /**
     * 从远程服务器下载文件到本地
     *
     * @param remoteUrl 远程地址
     * @param localPath 本地路径
     */
    public static void downloadFile(String remoteUrl, String localPath) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(remoteUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(localPath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定路径下全部mp4文件，只有当全部文件均不是tmp时才一次性全部返回，否则返回null
     *
     * @param remotePath 全路径
     * @return 文件名列表
     */
    public static List<String> findFileNames(String remotePath) {
        List<String> filelist = new ArrayList<>();
        String host = Constant.SRS_SERVER_HOST;
        int port = Constant.SRS_SERVER_SSH_PORT;
        String user = Constant.SRS_SERVER_USERNAME;
        String password = Constant.SRS_SERVER_PASSWORD;

        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remotePath);
            for (ChannelSftp.LsEntry file : files) {
                if (file.getFilename().endsWith(".tmp")) {
                    return null;
                } else if (file.getFilename().endsWith(".mp4")) {
                    filelist.add(file.getFilename());
                }
            }
            channelSftp.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filelist;
    }

    /**
     * 视频加水印
     *
     * @param inputFile         视频文件
     * @param outputFile        输出文件
     * @param watermarkImageUrl 水印照片
     */
    public static void videoWaterMaker(String inputFile, String outputFile, String watermarkImageUrl) {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile)) {
            grabber.start();
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels())) {
                recorder.setFormat("mp4");
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setFrameRate(grabber.getFrameRate());
                recorder.setTimestamp(grabber.getTimestamp());
                recorder.setVideoBitrate(grabber.getVideoBitrate());
                recorder.setVideoCodec(grabber.getVideoCodec());
                recorder.start();

                BufferedImage watermarkImage = loadWatermarkImage(watermarkImageUrl);

                Frame frame;
                while ((frame = grabber.grab()) != null) {
                    if (frame.image != null) {
                        Java2DFrameConverter converter = new Java2DFrameConverter();
                        BufferedImage bufferedImage = converter.getBufferedImage(frame);

                        // 在BufferedImage上绘制图片水印
                        Graphics2D g = bufferedImage.createGraphics();
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                        int x = 10; // 左边距10像素
                        int y = 30; // 上边距30像素
                        g.drawImage(watermarkImage, x, y, null);
                        g.dispose();
                        frame = converter.convert(bufferedImage);
                    }
                    recorder.record(frame);
                }
                recorder.stop();
                recorder.release();
                grabber.stop();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 抽帧
     *
     * @param videoFilePath 视频文件路径
     * @param outputFolder  存帧的文件夹路径
     * @param intervalMs    时间间隔，单位为毫秒，例如 5000 表示每隔 5 秒抽取一帧
     */
    public static void videoFrameExtractor(String videoFilePath, String outputFolder, long intervalMs) {
        int frameNumber = 0; // 初始帧编号
        long lastTimestamp = 0;

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath)) {
            grabber.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                if (frame.image != null) {
                    long currentTimestamp = grabber.getTimestamp();
                    if (currentTimestamp - lastTimestamp >= intervalMs * 1000L) {
                        BufferedImage image = converter.getBufferedImage(frame);
                        File output = new File(outputFolder + "/frame_" + frameNumber + ".png");
                        ImageIO.write(image, "png", output);
                        frameNumber++;
                        lastTimestamp = currentTimestamp;
                    }
                }
            }
            grabber.stop();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载水印图片
     *
     * @param imagePath 水印图片路径
     * @return 水印图片
     * @throws IOException
     */
    private static BufferedImage loadWatermarkImage(String imagePath) throws IOException {
        //return ImageIO.read(new URL(imagePath));
        return ImageIO.read(new File(imagePath));
    }
}
