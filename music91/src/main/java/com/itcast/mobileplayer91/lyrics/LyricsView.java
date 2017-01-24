package com.itcast.mobileplayer91.lyrics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.itcast.mobileplayer91.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ding on 2016/10/14.
 */
public class LyricsView extends TextView {

    private Paint mPaint;
    private int HIGHLIGHTT_COLOR;
    private int NORMAL_COLOR;
    private int HIGHLIGHT_SIZE;
    private int NORMMAL_SIZE;
    private int mViewW;
    private int mViewH;
    private ArrayList<Lyric> lyricArrayList;
    private int centerIndex;
    private int LINE_HEIGHT;
    private int mDuration;
    private int mPosition;

    public LyricsView(Context context) {
        super(context);
        initView();
    }

    public LyricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LyricsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

        HIGHLIGHT_SIZE = getResources().getDimensionPixelSize(R.dimen.hightlight_size);
        NORMMAL_SIZE = getResources().getDimensionPixelSize(R.dimen.normal_size);
        LINE_HEIGHT = getResources().getDimensionPixelSize(R.dimen.line_height);

        HIGHLIGHTT_COLOR = Color.GREEN;
        NORMAL_COLOR = Color.WHITE;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(HIGHLIGHTT_COLOR);
        mPaint.setTextSize(HIGHLIGHT_SIZE);

        // 伪造歌词数据
//        lyricArrayList = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            lyricArrayList.add(new Lyric(i * 2000,"当前歌词的行数为："+i));
//        }
//        centerIndex = 16;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewW = w;
        mViewH = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyricArrayList == null || lyricArrayList.size() == 0) {
            drawSingleLineText(canvas);
        } else {
            drawMuliteLineText(canvas);
        }
    }

    private void drawMuliteLineText(Canvas canvas) {

        // 获取据中行的歌词
        Lyric lyric = lyricArrayList.get(centerIndex);

//        移动的距离 = 已消耗时间百分比 * 行高
//        已消耗时间百分比 = 已消耗时间 / 可用时间
//        已消耗时间 = 当前播放进度 - 行起始时间
//        可用时间 = 下一行起始时间 - 行起始时间
        // 下一行起始时间

        int nextStartPoint ;
        if (centerIndex != lyricArrayList.size()-1){
            // 非最后一行
            Lyric nextLyric = lyricArrayList.get(centerIndex + 1);
            nextStartPoint = nextLyric.getStartPoint();
        }else{
            // 最后一行
            nextStartPoint = mDuration;
        }
        // 可用时间
        int lineTime = nextStartPoint - lyric.getStartPoint();
        // 已消耗时间
        int pastTime = mPosition - lyric.getStartPoint();
        // 已消耗时间百分比
        float pastPercent = pastTime / (float)lineTime;
        // 移动的距离
        float offsetY = pastPercent * LINE_HEIGHT;

        // 计算居中行的Y位置
        // 获取文本宽高
        Rect bounds = new Rect();
        mPaint.getTextBounds(lyric.getContent(),0,lyric.getContent().length(),bounds);

        // 计算居中行的Y坐标
        float centerY = mViewH / 2 + bounds.height() / 2 - offsetY;

        for (int i = 0; i < lyricArrayList.size(); i++) {

            if (i!=centerIndex){
                // 不是居中行
                mPaint.setColor(NORMAL_COLOR);
                mPaint.setTextSize(NORMMAL_SIZE);
            }else{
                // 居中行
                mPaint.setColor(HIGHLIGHTT_COLOR);
                mPaint.setTextSize(HIGHLIGHT_SIZE);
            }

            Lyric drawLyric = lyricArrayList.get(i);
            // centerY + lineH * (drawIndex - centerIndex)
            float drawY = centerY + LINE_HEIGHT * (i - centerIndex);
            drawHorizontalText(canvas, drawLyric.getContent(), drawY);
        }
    }

    private void drawHorizontalText(Canvas canvas, String text, float drawY) {
        // 获取文本宽高
        Rect bounds = new Rect();
        mPaint.getTextBounds(text,0,text.length(),bounds);

        // 计算绘制文本的坐标
        float drawX = mViewW / 2 - bounds.width() / 2;
        canvas.drawText(text, drawX, drawY, mPaint);
    }

    private void drawSingleLineText(Canvas canvas) {
        String text = "正在加载歌词...";
//        X = view宽度的一半 - 文本宽度的一半
//        Y = view高度的一半 + 文本高度的一半

        // 获取文本宽高
        Rect bounds = new Rect();
        mPaint.getTextBounds(text,0,text.length(),bounds);

        // 计算绘制文本的坐标
        float drawX = mViewW / 2 - bounds.width() / 2;
        float drawY = mViewH / 2 + bounds.height() / 2;
        canvas.drawText(text, drawX, drawY, mPaint);
    }

    // 根据当前歌曲的播放进度，更新居中行的位置
    public void computeCenterIndex(int position,int duration){

        mPosition = position;
        mDuration = duration;

        // 比较所有行的起始时间，如果比较行的时间比播放时间小，并且下一行的时间比播放时间大
        for (int i = 0; i < lyricArrayList.size(); i++) {
            Lyric lyric = lyricArrayList.get(i);
            int nextStartPoint;

            if (i!=lyricArrayList.size()-1){
                // 不是最后一行歌词
                Lyric nextLyric = lyricArrayList.get(i + 1);
                nextStartPoint = nextLyric.getStartPoint();
            }else{
                nextStartPoint = duration;
            }

            if (lyric.getStartPoint() <= position && nextStartPoint > position){
                centerIndex = i;
                break;
            }
        }

        invalidate();
    }

    // 更新歌词文件
    public void setLyricsFile(File lyricsFile){
        lyricArrayList = LyricsParser.parserFromFile(lyricsFile);
        centerIndex = 0;
    }
}
