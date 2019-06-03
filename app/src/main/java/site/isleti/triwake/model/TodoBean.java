package site.isleti.triwake.model;

import android.provider.Settings;

import java.util.UUID;

import site.isleti.triwake.util.DateUtil;

/***
 *TodoBean
 *@author islet
 *@time 2019/5/9 0009 14:48
 ***/
public class TodoBean {

    private String uuid;   //UUID
    private String date;   //日期
    private String content;//内容
    private String time;   //时间
    private long tag;      // this.tag = System.currentTimeMillis(); 排序时根据tag降序
    private int state;   //标识状态  初始为0 每点击一次加一  偶数表示undone  奇数表示done
    public int getState() {
        return state; // 0 undone 1 done
    }
    public void setState(int state) {
        this.state = state;
    }


    public long getTag() {
        return tag;
    }

    public void setTag(long tag) {
        this.tag = tag;
    }

    public TodoBean(String date,String content, String time,int state,long tag) {
        this.date = date;
        this.content = content;
        this.state = state;
        this.time = time;
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
