package xin.zhamao.zm_tiku.components;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ConstraintGridLayout {

    public static ConstraintLayout.LayoutParams getGridLayoutParam(ConstraintLayout.LayoutParams layoutParams, int top, int bottom, int start, int end, int topEnd) {
        // 干脆还是用判定表得了
        int judge = 0;
        judge = (top != -1 ? 1 : 0) << 3;
        judge = judge | (bottom != -1 ? 1 : 0) << 2;
        judge = judge | (start != -1 ? 1 : 0) << 1;
        judge = judge | (end != -1 ? 1 : 0);

        switch (judge) {
            case 0: // 如果只有一个题库，那么四个值都是 -1，我就直接居中显示，四个都是 parent
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                break;
            case 1: // 只有 end 有东西，表明就两个元素，且当前元素是第0个
            case 5: // bottom 和 end 有东西，此情况类似 1
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.endToStart = end;
                break;
            case 2: // 只有 start 有东西，表明就两个元素，且当前元素是第1个
            case 6: // bottom 和 start 有东西，此情况类似 2
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.startToEnd = start;
                break;
            case 8: // 只有 top 有东西，表明当前这个元素是第一列，但不是第一行，且题库数是奇数个，这个是最后一个
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.topToBottom = top;
                layoutParams.endToStart = topEnd;
                break;
            case 9: // top 和 end 有东西
            case 13:// top、bottom 和 end 有东西，此情况类似 9
                layoutParams.endToStart = end;
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.topToBottom = top;
                break;
            case 10:// top 和 start 有东西
            case 14:// top、bottom 和 start 有东西，此情况类似 10
                layoutParams.startToEnd = start;
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.topToBottom = top;
                break;
            case 3: // 这种情况不可能发生
            case 4: // 这种情况不可能发生
            case 7: // 这种情况不可能发生
            case 11:// 这种情况不可能发生
            case 12:// 这种情况不可能发生
            case 15:// 这种情况不可能发生
                break;
        }
        return layoutParams;
    }
}
