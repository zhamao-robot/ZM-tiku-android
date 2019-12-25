package xin.zhamao.zm_tiku.view;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xin.zhamao.zhamao.zm_tiku.R;

public class SelectBankFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("选择题库");
        View view = inflater.inflate(R.layout.fragment_select_bank, container, false);
        (view.findViewById(R.id.materialCardView1)).setOnClickListener(this);
        (view.findViewById(R.id.materialCardView2)).setOnClickListener(this);
        (view.findViewById(R.id.materialCardView3)).setOnClickListener(this);
        (view.findViewById(R.id.materialCardView4)).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        String qb_name = "";
        switch (v.getId()) {
            case R.id.materialCardView1:
                qb_name = "近代史题库";
                break;
            case R.id.materialCardView2:
                qb_name = "马克思题库";
                break;
            case R.id.materialCardView3:
                qb_name = "毛概题库";
                break;
            case R.id.materialCardView4:
                qb_name = "思修题库";
                break;
        }
        Intent intent = new Intent(getActivity(), SelectMode.class);
        intent.putExtra("qb_name", qb_name);
        if (android.os.Build.VERSION.SDK_INT < 26) {
            startActivity(intent);
        } else {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        }
    }
}
