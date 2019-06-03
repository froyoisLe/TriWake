package site.isleti.triwake.model;

import java.io.Serializable;
import java.util.UUID;

import site.isleti.triwake.util.DateUtil;

/***
 *RecordBean
 *@author islet
 *@time 2019/5/9 0009 14:51
 ***/
public class RecordBean implements Serializable {

    public static String TAG = "RecordBean";

    /*** 账目类别-支出/收入 ***@author：IsLeti***/
    public enum RecordType {
        RECORD_TYPE_EXPENSE,
        RECORD_TYPE_INCOME
    }

    private double amount;  //金额
    private RecordType type;//账目类别
    private String category;//消费类型
    private String remark;  //备注
    private String date;    //2019-05-09
    private long timeStamp; //时间
    private String uuid;    //UUID

    public RecordBean() {
        uuid = UUID.randomUUID().toString();
        timeStamp = System.currentTimeMillis();
        date = DateUtil.getFormattedDate();
    }


    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /***Type:
     * 支出->1
     * 收入->2
     * ***@author：IsLeti***/
    public int getType() {
        if (this.type == RecordType.RECORD_TYPE_EXPENSE) {
            return 1;
        } else {
            return 2;
        }
    }
    public void setType(int type) {
        if (type == 1) {
            this.type = RecordType.RECORD_TYPE_EXPENSE;
        } else {
            this.type = RecordType.RECORD_TYPE_INCOME;
        }
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
