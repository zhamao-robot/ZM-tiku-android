package dhu.cst.zhamao.zm_tiku.view;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dhu.cst.zhamao.zm_tiku.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_bookmark, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("错题本");
        return view;
    }

}
