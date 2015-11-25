package xyz.dt.gboost.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by luolaihu on 11/25/15.
 */
public class BoosterInfo {
    public @Setter @Getter long numRow;
    public @Setter @Getter long numCol;
    public List<Integer> rootIndex = Lists.newArrayList();
    public List<Integer> foldIndex = Lists.newArrayList();

    public  BoosterInfo(){
        this.numRow = 0;
        this.numCol = 0;
    }

    public int getRoot(int index){
        Preconditions.checkArgument(rootIndex.size() > index, "index greater than root index!");
        return rootIndex.get(index);
    }
}
