package site.isleti.triwake.model;
import java.io.Serializable;
import java.util.UUID;

/***
 *IntrosBean
 *@author islet
 *@time 2019/5/9 0009 14:48
 ***/
public class IntrosBean implements Serializable {

    private String uuid;   //UUID
    private String date;   //日期
    private String content;//内容
    private String time;   //时间
    private long tag;      //用来排序


    public IntrosBean(String date,String content, String time) {
        this.date = date;
        this.time = time;
        this.content = content;
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

    public long getTag() {
        return tag;
    }

    public void setTag(long tag) {
        this.tag = tag;
    }

}
