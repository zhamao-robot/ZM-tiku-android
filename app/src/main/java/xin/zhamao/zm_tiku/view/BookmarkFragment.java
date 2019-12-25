package xin.zhamao.zm_tiku.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xin.zhamao.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.object.BookmarkData;
import xin.zhamao.zm_tiku.object.BookmarksSheetAdapter;
import xin.zhamao.zm_tiku.object.QBSection;
import xin.zhamao.zm_tiku.object.TikuSection;
import xin.zhamao.zm_tiku.utils.QB;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {

    private View  view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("错题本: 近代史");
        RecyclerView bookmarks_sheet =  view.findViewById(R.id.bookmarks_sheet);
        bookmarks_sheet.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        bookmarks_sheet.setLayoutManager(new LinearLayoutManager(getContext()));
        BookmarksSheetAdapter adapter = new BookmarksSheetAdapter(getContext(), getWrongBank("近代史"));
        bookmarks_sheet.setAdapter(adapter);
        return view;
    }

    private List<BookmarkData> getWrongBank(String qb_name){
        QB qb = new QB(getContext());
        Map<String, TikuSection> tikuData =  qb.getTikuData(QB.getTikuName(qb_name));
        QBSection qbSection = new QBSection(qb, qb.getUserId(), QB.getTikuName(qb_name));
        List<Integer> wrong_list = qbSection.wrong;
        if(qbSection.wrong == null) return new ArrayList<>(0);
        List<BookmarkData> res = new ArrayList<>(wrong_list.size());
        for(int i = 0;i < wrong_list.size();i++){
            int id = wrong_list.get(i);
            TikuSection tikuSection = tikuData.get(String.valueOf(id));
            res.add(new BookmarkData(tikuSection,0));
        }
        return res;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.add(0,1,0,"近代史");
        menu.add(0,2,0,"马克思");
        menu.add(0,3,0,"毛概");
        menu.add(0,4,0,"思修");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("错题本: "+item.getTitle().toString());
        RecyclerView bookmarks_sheet =  view.findViewById(R.id.bookmarks_sheet);
        bookmarks_sheet.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        bookmarks_sheet.setLayoutManager(new LinearLayoutManager(getContext()));
        BookmarksSheetAdapter adapter = new BookmarksSheetAdapter(getContext(), getWrongBank(item.getTitle().toString()));
        bookmarks_sheet.setAdapter(adapter);
        return super.onOptionsItemSelected(item);
    }
}

