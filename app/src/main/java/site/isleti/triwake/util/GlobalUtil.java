package site.isleti.triwake.util;

import android.annotation.SuppressLint;
import android.content.Context;
import java.util.LinkedList;
import site.isleti.triwake.R;
import site.isleti.triwake.db.RecordDBHelper;
import site.isleti.triwake.model.RecordCategoryResBean;
import site.isleti.triwake.ui.account.AccountFragment;

public class GlobalUtil {

    private static final String TAG = "GlobalUtil";

    @SuppressLint("StaticFieldLeak")
    private static GlobalUtil instance;
    public RecordDBHelper mRecordDBHelper;
    private Context mContext;
    public AccountFragment mAccountFragment;

    public LinkedList<RecordCategoryResBean> costRes = new LinkedList<>();
    public LinkedList<RecordCategoryResBean> earnRes = new LinkedList<>();

    private static int[] costIconRes = {
            R.drawable.icon_general_white,
            R.drawable.icon_food_white,
            R.drawable.icon_drinking_white,
            R.drawable.icon_groceries_white,
            R.drawable.icon_shopping_white,
            R.drawable.icon_personal_white,
            R.drawable.icon_entertain_white,
            R.drawable.icon_movie_white,
            R.drawable.icon_social_white,
            R.drawable.icon_transport_white,
            R.drawable.icon_appstore_white,
            R.drawable.icon_mobile_white,
            R.drawable.icon_computer_white,
            R.drawable.icon_gift_white,
            R.drawable.icon_house_white,
            R.drawable.icon_travel_white,
            R.drawable.icon_ticket_white,
            R.drawable.icon_book_white,
            R.drawable.icon_medical_white,
            R.drawable.icon_transfer_white
    };

    private static int[] costIconResBlack = {
            R.drawable.icon_general,
            R.drawable.icon_food,
            R.drawable.icon_drinking,
            R.drawable.icon_groceries,
            R.drawable.icon_shopping,
            R.drawable.icon_presonal,
            R.drawable.icon_entertain,
            R.drawable.icon_movie,
            R.drawable.icon_social,
            R.drawable.icon_transport,
            R.drawable.icon_appstore,
            R.drawable.icon_mobile,
            R.drawable.icon_computer,
            R.drawable.icon_gift,
            R.drawable.icon_house,
            R.drawable.icon_travel,
            R.drawable.icon_ticket,
            R.drawable.icon_book,
            R.drawable.icon_medical,
            R.drawable.icon_transfer
    };

    private static String[] costTitle = {
            "日常",
            "餐饮",
            "酒水",
            "日用",
            "购物",
            "个人",
            "娱乐",
            "电影",
            "社交",
            "交通",
            "应用",
            "手机",
            "电脑",
            "礼物",
            "家务",
            "旅行",
            "票务",
            "书籍",
            "医疗",
            "转账"};

    private static int[] earnIconRes = {
            R.drawable.icon_general_white,
            R.drawable.icon_reimburse_white,
            R.drawable.icon_salary_white,
            R.drawable.icon_redpocket_white,
            R.drawable.icon_parttime_white,
            R.drawable.icon_bonus_white,
            R.drawable.icon_investment_white};

    private static int[] earnIconResBlack = {
            R.drawable.icon_general,
            R.drawable.icon_reimburse,
            R.drawable.icon_salary,
            R.drawable.icon_redpocket,
            R.drawable.icon_parttime,
            R.drawable.icon_bonus,
            R.drawable.icon_investment};

    private static String[] earnTitle = {
            "日常",
            "报销",
            "工资",
            "红包",
            "兼职",
            "奖金",
            "投资"};

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
        mRecordDBHelper = new RecordDBHelper(context,RecordDBHelper.DB_NAME, null, 1);

        for (int i = 0; i < costTitle.length; i++) {
            RecordCategoryResBean res = new RecordCategoryResBean();
            res.title = costTitle[i];
            res.resBlack = costIconResBlack[i];
            res.resWhite = costIconRes[i];
            costRes.add(res);
        }

        for (int i = 0; i < earnTitle.length; i++) {
            RecordCategoryResBean res = new RecordCategoryResBean();
            res.title = earnTitle[i];
            res.resBlack = earnIconResBlack[i];
            res.resWhite = earnIconRes[i];
            earnRes.add(res);
        }

    }

    public static GlobalUtil getInstance() {
        if (instance == null) {
            instance = new GlobalUtil();
        }
        return instance;
    }

    public int getResourceIcon(String category) {

        for (RecordCategoryResBean res : costRes) {
            if (res.title.equals(category)) {
                return res.resWhite;
            }
        }
        for (RecordCategoryResBean res : earnRes) {
            if (res.title.equals(category)) {
                return res.resWhite;
            }
        }
        return costRes.get(0).resWhite;
    }
}
