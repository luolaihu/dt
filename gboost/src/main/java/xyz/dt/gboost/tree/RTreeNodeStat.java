package xyz.dt.gboost.tree;

import java.io.OutputStream;

/**
 * Created by luolaihu on 12/1/15.
 */
public class RTreeNodeStat {
    public float lossChg;
    public float sumHess;
    public float baseWeight;
    public int leafChildCnt;

    public void print(OutputStream fout, boolean isLeaf){

    }
}
