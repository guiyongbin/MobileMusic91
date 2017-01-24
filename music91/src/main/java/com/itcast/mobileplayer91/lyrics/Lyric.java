package com.itcast.mobileplayer91.lyrics;

/**
 * Created by Ding on 2016/10/14.
 */
public class Lyric implements Comparable<Lyric>{

    private int startPoint;
    private String content;

    public Lyric(int startPoint, String content) {
        this.startPoint = startPoint;
        this.content = content;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "startPoint=" + startPoint +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public int compareTo(Lyric another) {
        return startPoint - another.startPoint;
    }
}
