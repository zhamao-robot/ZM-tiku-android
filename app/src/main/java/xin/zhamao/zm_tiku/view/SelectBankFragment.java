package xin.zhamao.zm_tiku.view;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Objects;

import xin.zhamao.zhamao.zm_tiku.BuildConfig;
import xin.zhamao.zhamao.zm_tiku.R;
import xin.zhamao.zm_tiku.components.ConstraintGridLayout;
import xin.zhamao.zm_tiku.object.TikuMeta;
import xin.zhamao.zm_tiku.object.TikuMetaList;
import xin.zhamao.zm_tiku.utils.TikuManager;
import xin.zhamao.zm_tiku.utils.ZMUtil;

public class SelectBankFragment extends Fragment implements View.OnClickListener {
    private ConstraintLayout constraintLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 根据 tiku_list 生成题库的列表样式
        TikuMetaList list = TikuManager.getMetaList();
        ArrayList<MaterialCardView> cardList = new ArrayList<>();
        for (TikuMeta meta : list) {
            MaterialCardView card = new MaterialCardView(requireContext());
            card.setId(View.generateViewId());
            // 设置基础 layout 样式，长宽都是wrap_content
            card.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // 设置卡片的 marginTop 为 16dp
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(card.getLayoutParams());
            marginLayoutParams.setMargins(0, ZMUtil.dip2px(this.requireContext(), 16), 0, 0);
            card.setLayoutParams(marginLayoutParams);
            // 设置 minHeight 为 200dp
            card.setMinimumHeight(ZMUtil.dip2px(this.requireContext(), 200));

            // 生成一个 LinearLayout，用于放图片和文字
            LinearLayout linearLayout = new LinearLayout(requireContext());
            // 设置 LinearLayout 的 layout 样式，长宽是match_parent wrap_content
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // 设置 orientation 为 vertical
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            // 生成一个 ImageVie
            ImageView imageView = new ImageView(requireContext());
            // 设置 ImageView 的 layout 样式，长宽都是wrap_content
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // 通过resource 文件名动态设置图片资源
            imageView.setImageResource(requireContext().getResources().getIdentifier(meta.avatar, "drawable", requireContext().getPackageName()));
            // imageView.setImageResource(R.string.class.getField(meta.avatar).getInt(null));
            linearLayout.addView(imageView);
            // 生成一个 TextView
            TextView textView = new TextView(requireContext());
            // 设置宽高为 match 和 wrap
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // 设置 marginTop 和 marginBottom均为 14
            ViewGroup.MarginLayoutParams textMargin = new ViewGroup.MarginLayoutParams(textView.getLayoutParams());
            textMargin.setMargins(0, ZMUtil.dip2px(this.requireContext(), 14), 0, 0);
            textView.setLayoutParams(textMargin);
            // 设置 gravity 为 center
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            // 设置文字颜色
            textView.setTextColor(getResources().getColor(R.color.primaryTextColor));
            // 设置显示文本
            textView.setText("  " + meta.display_name);
            linearLayout.addView(textView);
            card.addView(linearLayout);
            TikuManager.metaMap.put(card.getId(), meta);
            cardList.add(card);
        }

        // 接下来绘制 ConstraintLayout
        constraintLayout = new ConstraintLayout(requireContext());
        // 设置 ConstraintLayout 的 layout 样式，长宽都是match_parent
        constraintLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // 设置 marginTop 为 56dp
        ViewGroup.MarginLayoutParams constraintMargin = new ViewGroup.MarginLayoutParams(constraintLayout.getLayoutParams());
        constraintMargin.setMargins(0, ZMUtil.dip2px(this.requireContext(), 56), 0, 0);
        constraintLayout.setLayoutParams(constraintMargin);

        // 然后就是依据 cardList 生成 ConstraintLayout 的子元素
        for (int i = 0; i < cardList.size(); i++) {
            MaterialCardView card = cardList.get(i);
            // 接下来根据位置 ID 设置 constraint 的 layout 参数
            int top, bottom, start, end;
            top = i < 2 ? -1 : i - 2;
            bottom = (i + 2) >= cardList.size() ? -1 : i + 2;
            start = i % 2 == 0 ? -1 : i - 1;
            end = i % 2 == 0 ? (i + 1 >= cardList.size() ? -1 : i + 1) : -1;

            top = top != -1 ? cardList.get(top).getId() : top;
            bottom = bottom != -1 ? cardList.get(bottom).getId() : bottom;
            start = start != -1 ? cardList.get(start).getId() : start;
            end = end != -1 ? cardList.get(end).getId() : end;
            int topEnd = i < 2 ? -1 : (i % 2 == 0 ? cardList.get(i - 1).getId() : -1);
            ConstraintLayout.LayoutParams cardLayoutParams = ConstraintGridLayout.getGridLayoutParam(new ConstraintLayout.LayoutParams(card.getLayoutParams()), top, bottom, start, end, topEnd);
            cardLayoutParams.topMargin = ZMUtil.dip2px(this.requireContext(), 16);
            card.setLayoutParams(cardLayoutParams);
            card.setOnClickListener(this);
            constraintLayout.addView(card);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("选择题库" + (BuildConfig.DEBUG ? " (调试模式)" : ""));
        View view = inflater.inflate(R.layout.fragment_select_bank, container, false);
        View p = view.findViewById(R.id.selectBankFrameLayout);
        if (p instanceof FrameLayout) {
            ((FrameLayout) p).addView(constraintLayout);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), SelectMode.class);
        intent.putExtra("qb_view_id", v.getId());
        if (android.os.Build.VERSION.SDK_INT < 26) {
            startActivity(intent);
        } else {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        }
    }
}
