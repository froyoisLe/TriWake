package site.isleti.triwake.model;
/***
*DiaryBean
*@author islet
*@time 2019/5/9 0009 14:48
***/
public class DiaryBean {
    private String date;   //日期
    private String title;  //标题
    private String content;//内容
    private String tag;

    public DiaryBean(String date, String title, String content, String tag) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
