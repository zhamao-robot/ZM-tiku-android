package dhu.cst.zhamao.zm_tiku.object;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dhu.cst.zhamao.zm_tiku.R;

public class BookmarksSheetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BookmarkData> mData;
    private LayoutInflater mInflater;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private Context context;

    public BookmarksSheetAdapter(Context context, List<BookmarkData> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case 0:
                view = mInflater.inflate(R.layout.bookmarks_sheet_item, parent, false);
                return new BookmarksSheetAdapter.ParentViewHolder(view);
            case 1:
                view = mInflater.inflate(R.layout.bookmarks_sheet_item_child, parent, false);
                return new BookmarksSheetAdapter.ChildViewHolder(view);
            default:
                view = mInflater.inflate(R.layout.bookmarks_sheet_item_child, parent, false);
                return new BookmarksSheetAdapter.ParentViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 0:
                ParentViewHolder parentViewHolder = (ParentViewHolder) holder;
                parentViewHolder.bindView(mData.get(position), position, itemClickListener);
                break;
            case 1:
                ChildViewHolder childViewHolder = (ChildViewHolder) holder;
                childViewHolder.bindView(mData.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).isChild;
    }

    private ItemClickListener itemClickListener = new ItemClickListener() {
        @Override
        public void onExpandChildren(BookmarkData bean) {
            int position = bean.childPosition;
            BookmarkData children = new BookmarkData(bean.tikuData,1);
            mData.add(position, children);
            notifyDataSetChanged();
        }

        @Override
        public void onHideChildren(BookmarkData bean) {
            int position = bean.childPosition;
            mData.remove(position);
            notifyDataSetChanged();
        }
    };

    public class ParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        private BookmarkData data;
        private ItemClickListener clickListener;

        ParentViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.info_text);
            itemView.setOnClickListener(this);
        }

        void bindView(BookmarkData data, int position, ItemClickListener clickListener){
            this.data = data;
            this.clickListener = clickListener;
            data.childPosition = position + 1;
            myTextView.setText(data.tikuData.question);
        }

        @Override
        public void onClick(View view) {
            if(!data.isExpand){
                data.isExpand = true;
                data.childPosition = getAdapterPosition()+1;
                clickListener.onExpandChildren(data);
            }else{
                data.isExpand = false;
                clickListener.onHideChildren(data);
            }
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        Map<String,TextView> textViews;

        ChildViewHolder(View itemView) {
            super(itemView);
            textViews = new HashMap<>(5);
            textViews.put("A",(TextView)itemView.findViewById(R.id.textView1));
            textViews.put("B",(TextView)itemView.findViewById(R.id.textView2));
            textViews.put("C",(TextView)itemView.findViewById(R.id.textView3));
            textViews.put("D",(TextView)itemView.findViewById(R.id.textView4));
            textViews.put("E",(TextView)itemView.findViewById(R.id.textView5));
        }

        void bindView(BookmarkData data){
            Map<String, String> answer = data.tikuData.answer;
            for(Map.Entry<String,TextView> entry : textViews.entrySet()){
                textViews.get(entry.getKey()).setVisibility(View.GONE);
                textViews.get(entry.getKey()).setTextColor(context.getResources().getColor(R.color.primary_text));
            }
            for (Map.Entry<String,String> entry : answer.entrySet()){
                textViews.get(entry.getKey()).setText(entry.getKey()+". "+entry.getValue());
                textViews.get(entry.getKey()).setVisibility(View.VISIBLE);
            }
            String key = data.tikuData.key;
            String[] key_list = key.split(" ");
            for (String s : key_list) {
                if (s.equals("")) continue;
                textViews.get(s).setTextColor(context.getResources().getColor(R.color.green));
            }
        }
    }

    public interface ItemClickListener {

        void onExpandChildren(BookmarkData bean);

        void onHideChildren(BookmarkData bean);
    }
}
