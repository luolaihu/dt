package xyz.dt.gboost.tree;

/**
 * Created by luolaihu on 11/27/15.
 */
public class SplitEntry {
    public float lossChg;
    public int sIndex;
    public float sValue;

    public SplitEntry(){
        this.lossChg = 0.0f;
        this.sIndex = 0;
        this.sValue = 0.0f;
    }

    public boolean needReplace(float newLossChg, int sIndex){
        if (this.splitIndex() <= sIndex){
            return newLossChg > this.lossChg;
        } else {
            return !(newLossChg > this.lossChg);
        }
    }

    public boolean update(SplitEntry e){
        if (this.needReplace(e.lossChg, e.splitIndex())){
            this.lossChg = e.lossChg;
            this.sIndex = e.sIndex;
            this.sValue = e.sValue;
            return true;
        } else {
            return false;
        }
    }

    public boolean update(float newLossChg, int sIndex,
                          float newSValue, boolean defaultLeft){
        if (this.needReplace(newLossChg, sIndex)){
            this.lossChg = newLossChg;
            if (defaultLeft) sIndex |= (1 << 31);
            this.sIndex = sIndex;
            this.sValue = newSValue;
            return true;
        } else {
            return false;
        }
    }

    public void reduce(SplitEntry dst, SplitEntry src){
        dst.update(src);
    }

    public int splitIndex(){
        return sIndex & ((1 << 31) - 1);
    }

    public boolean defaultLeft(){
        return (sIndex >>> 31) != 0;
    }
}
