package dhu.cst.zhamao.zm_tiku.view;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import dhu.cst.zhamao.zm_tiku.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("错题本");
        String[] data = {"a","b","c"};
        RecyclerView bookmarks_sheet =  view.findViewById(R.id.bookmarks_sheet);
        bookmarks_sheet.setLayoutManager(new LinearLayoutManager(getContext()));
        BookmarksSheetAdapter adapter = new BookmarksSheetAdapter(getContext(), data, new BookmarksSheetAdapter.BookmarksSheetClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(),""+position,Toast.LENGTH_SHORT).show();
            }
        });
        bookmarks_sheet.setAdapter(adapter);
        return view;
    }
}
class BookmarksSheetAdapter extends RecyclerView.Adapter<BookmarksSheetAdapter.ViewHolder> {

    private String[] mData;
    private LayoutInflater mInflater;
    private BookmarksSheetClickListener mListener;

    BookmarksSheetAdapter(Context context, String[] data,BookmarksSheetClickListener mListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mListener = mListener;
    }

    @Override
    @NonNull
    public BookmarksSheetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bookmarks_sheet_item, parent, false);
        return new BookmarksSheetAdapter.ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarksSheetAdapter.ViewHolder holder, int position) {
        holder.myTextView.setText(mData[position]);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        private BookmarksSheetClickListener mListener;

        ViewHolder(View itemView,BookmarksSheetClickListener listener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.info_text);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public interface BookmarksSheetClickListener {
        void onClick(View view, int position);
    }
}
