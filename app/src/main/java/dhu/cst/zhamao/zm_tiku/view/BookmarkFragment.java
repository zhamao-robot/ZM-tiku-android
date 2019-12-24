package dhu.cst.zhamao.zm_tiku.view;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import dhu.cst.zhamao.zm_tiku.R;
import dhu.cst.zhamao.zm_tiku.object.BookmarkData;
import dhu.cst.zhamao.zm_tiku.object.BookmarksSheetAdapter;
import dhu.cst.zhamao.zm_tiku.object.QBSection;
import dhu.cst.zhamao.zm_tiku.object.TikuSection;
import dhu.cst.zhamao.zm_tiku.utils.QB;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("错题本");
        RecyclerView bookmarks_sheet =  view.findViewById(R.id.bookmarks_sheet);
        bookmarks_sheet.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        bookmarks_sheet.setLayoutManager(new LinearLayoutManager(getContext()));
        BookmarksSheetAdapter adapter = new BookmarksSheetAdapter(getContext(), getWrongBank());
        bookmarks_sheet.setAdapter(adapter);
        return view;
    }

    private List<BookmarkData> getWrongBank(){
        QB qb = new QB(getContext());
        Map<String, TikuSection> tikuData =  qb.getTikuData(QB.getTikuName("近代史题库"));
        QBSection qbSection = new QBSection(qb, qb.getUserId(), QB.getTikuName("近代史题库"));
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

}

