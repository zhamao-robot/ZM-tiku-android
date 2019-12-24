package dhu.cst.zhamao.zm_tiku.object;

public class BookmarkData {
    TikuSection tikuData;
    int childPosition;
    boolean isExpand;
    int isChild;
    public BookmarkData(TikuSection tikuData,int isChild){
        this.tikuData = tikuData;
        this.isChild = isChild;
        childPosition = 0;
        isExpand = false;
    }
}
