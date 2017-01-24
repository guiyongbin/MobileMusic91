package com.itcast.mobileplayer91.lyrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ding on 2016/10/14.
 */
public class LyricsParser {

    // 从文件解析出歌词数据列表
    public static ArrayList<Lyric> parserFromFile(File lyricsFile){
        ArrayList<Lyric> lyrics = new ArrayList<>();
        // 数据可用性验证
        if (lyricsFile == null || !lyricsFile.exists()) {
            lyrics.add(new Lyric(0, "无法解析歌词文件"));
            return lyrics;
        }

        // 按行读取歌词并解析
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(lyricsFile),"GBK"));
            String line ;
            while ((line= br.readLine())!=null){
                // [01:22.04][02:35.04]寂寞的夜和谁说话
                ArrayList<Lyric> lineLyrics = parserLine(line);
                lyrics.addAll(lineLyrics);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(lyrics);

        return lyrics;
    }

    // 解析一行歌词 [01:22.04][02:35.04]寂寞的夜和谁说话
    private static ArrayList<Lyric> parserLine(String line) {
        ArrayList<Lyric> lineLyrics = new ArrayList<>();

        String[] splitArr = line.split("]");
        // [01:22.04 [02:35.04 寂寞的夜和谁说话
        String content = splitArr[splitArr.length-1];

        // [01:22.04 [02:35.04
        for (int i = 0; i < splitArr.length - 1; i++) {
            int startPoint = parserStartPoint(splitArr[i]);
            lineLyrics.add(new Lyric(startPoint,content));
        }

        return lineLyrics;
    }

    // 解析歌词起始时间 [02:35.04
    private static int parserStartPoint(String time) {

        String[] splitArr = time.split(":");
        // [02 35.04
        String minStr = splitArr[0].substring(1);

        // 35.04
        String[] split = splitArr[1].split("\\.");

        // 35 04
        String secStr = split[0];
        String msecStr = split[1];

        // 将字符串转换为数值
        int min = Integer.parseInt(minStr);
        int sec = Integer.parseInt(secStr);
        int msec = Integer.parseInt(msecStr);

        // 合成时间戳
        int startPoint = min * 60 * 1000
                + sec * 1000
                + msec * 10;
        return startPoint;
    }
}
