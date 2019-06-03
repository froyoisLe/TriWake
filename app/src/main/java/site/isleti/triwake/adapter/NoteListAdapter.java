package site.isleti.triwake.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import org.greenrobot.eventbus.EventBus;
import java.util.HashMap;
import java.util.LinkedList;
import site.isleti.triwake.R;
import site.isleti.triwake.db.NoteDBHelper;
import site.isleti.triwake.event.StartUpdateNoteEvent;
import site.isleti.triwake.model.NoteBean;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class NoteListAdapter extends BaseAdapter{

    private static final String TAG = "NoteListAdapter";

    private LinkedList<NoteBean> mNoteBeans;
    private LayoutInflater  mLayoutInflater;
    private Context mContext;
    private int mEditPosition = -1;
    private NoteDBHelper mHelper;

    /*** 初始化构造器：传入上下文和DataList，获得数据库信息 ***@author：IsLeti***/
    public NoteListAdapter(Context context, LinkedList<NoteBean> noteBeans){
        this.mContext = context;
        this.mNoteBeans = noteBeans;
        this.mLayoutInflater = LayoutInflater.from(context);
        mHelper = new NoteDBHelper(mContext,"Notes.db",null,1);
    }

    public void setNotes(LinkedList<NoteBean> notes){
        this.mNoteBeans = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mNoteBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mNoteBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NoteViewHolder noteViewHolder;

        //*** HashMap<> 获取View 防止布局复用产生混乱***@author：IsLeti***/
        HashMap<Integer,View> ntmp = new HashMap<>();
        if (ntmp.get(position) == null){
            convertView = mLayoutInflater.inflate(R.layout.notes_item_view,null);
            noteViewHolder = new NoteViewHolder(convertView, (NoteBean) getItem(position));
            convertView.setTag(noteViewHolder);
            ntmp.put(position,convertView);
        }
        else {
            convertView = ntmp.get(position);
            noteViewHolder = (NoteViewHolder) convertView.getTag();
        }

        //*** 按钮预先隐藏 ***@author：IsLeti***/
        noteViewHolder.mIvEdit.setVisibility(View.INVISIBLE);
        noteViewHolder.mIvDelete.setVisibility(View.INVISIBLE);
        if(mEditPosition == position){
            noteViewHolder.mIvEdit.setVisibility(View.VISIBLE);
            noteViewHolder.mIvDelete.setVisibility(View.VISIBLE);
        }
        else {
            noteViewHolder.mIvEdit.setVisibility(View.GONE);
            noteViewHolder.mIvDelete.setVisibility(View.GONE);
        }

        //设置卡片主题背景
         int i = position % 3;
        if (i == 0){
            noteViewHolder.mRelativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bgr_note_theme));
            noteViewHolder.mNoteDate.setTextColor(mContext.getResources().getColor(R.color.note_primary));
            noteViewHolder.mNoteTime.setTextColor(mContext.getResources().getColor(R.color.note_primary));
            noteViewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.note_darker));
        }
        else if (i == 1){
            noteViewHolder.mRelativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bgr_diary_theme));
            noteViewHolder.mNoteDate.setTextColor(mContext.getResources().getColor(R.color.diary_primary));
            noteViewHolder.mNoteTime.setTextColor(mContext.getResources().getColor(R.color.diary_primary));
            noteViewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.diary_darker));
        }
        else if (i == 2){
            noteViewHolder.mRelativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bgr_account_theme));
            noteViewHolder.mNoteDate.setTextColor(mContext.getResources().getColor(R.color.account_primary));
            noteViewHolder.mNoteTime.setTextColor(mContext.getResources().getColor(R.color.account_primary));
            noteViewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.account_darker));
        }

        //点击item区域显示删除和编辑的图标
        noteViewHolder.mLayout.setOnClickListener(v -> {
            if(noteViewHolder.mIvEdit.getVisibility() == View.VISIBLE){
                noteViewHolder.mIvEdit.setVisibility(View.GONE);
            }
            else {
                noteViewHolder.mIvEdit.setVisibility(View.VISIBLE);
            }
            if(noteViewHolder.mIvDelete.getVisibility() == View.VISIBLE){
                noteViewHolder.mIvDelete.setVisibility(View.GONE);
            }
            else {
                noteViewHolder.mIvDelete.setVisibility(View.VISIBLE);
            }
            if(mEditPosition != position){
                mEditPosition = position;
            }
            mEditPosition = position;
        });

        //跳转进行编辑 EventBus实现
        noteViewHolder.mIvEdit.setOnClickListener(v -> {
            EventBus.getDefault().post(new StartUpdateNoteEvent(position));
        });

        //delete
        noteViewHolder.mIvDelete.setOnClickListener(v -> {
            NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(mContext);
            dialogBuilder
                    .withTitle("删除笔记")
                    .withTitleColor("#FFFFFF")
                    .withDividerColor(mContext.getResources().getColor(R.color.note_primary))
                    .withMessage("   您确认要删除吗?")
                    .withMessageColor("#FFFFFF")
                    .withDialogColor(mContext.getResources().getColor(R.color.note_dark))
                    .withIcon(mContext.getResources().getDrawable(R.drawable.ic_filter_vintage_teal_50_36dp))
                    .isCancelableOnTouchOutside(true)
                    .withDuration(500)
                    .withEffect(Slidetop)
                    .withButton1Text("手滑")
                    .withButton2Text("是的")
                    .setButton2Click(v1 -> {
                        String uuid = mNoteBeans.get(position).getUuid();
                        mHelper.removeNotes(uuid);
                        mNoteBeans.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(v1.getContext(), "已删除", Toast.LENGTH_SHORT).show();
                        dialogBuilder.cancel();
                    })
                    .setButton1Click(v12 -> {
                        Toast.makeText(v12.getContext(), "取消了", Toast.LENGTH_SHORT).show();
                        dialogBuilder.cancel();
                    })
                    .show();
        });

        return convertView;
    }

    public static class NoteViewHolder {
        private  TextView mNoteDate;
        private  TextView mNoteTime;
        private  ExpandableTextView mNoteContent;
        private TextView mTextView;
        private RelativeLayout mLayout;
        private ImageView mIvEdit;
        private ImageView mIvDelete;
        private RelativeLayout mRelativeLayout;

        NoteViewHolder(View itemView,NoteBean NotesBean){
            mNoteDate = itemView.findViewById(R.id.note_date);
            mNoteTime = itemView.findViewById(R.id.note_time);
            mNoteContent = itemView.findViewById(R.id.expand_text_view);
            mTextView = itemView.findViewById(R.id.expandable_text);
            mNoteDate.setText(NotesBean.getDate());
            mNoteTime.setText(NotesBean.getTime());
            mNoteContent.setText("  " + NotesBean.getContent());
            mLayout = itemView.findViewById(R.id.note_layout);
            mIvEdit = itemView.findViewById(R.id.note_iv_edit);
            mIvDelete = itemView.findViewById(R.id.note_iv_delete);
            mRelativeLayout = itemView.findViewById(R.id.note_item);
        }
    }
}

