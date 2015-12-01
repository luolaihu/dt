package xyz.dt.gboost.tree;

import com.google.common.base.Preconditions;

import java.util.ArrayList;

/**
 * Created by luolaihu on 12/1/15.
 */
public class RegTree extends TreeModel<Float, RTreeNodeStat> {

    @Override
    public int allocNode(){
        if (param.numDeleted != 0){
            int nd = deletedNodes.poll();
            --param.numDeleted;
            return nd;
        }
        int nd = param.numNodes++;
        Preconditions.checkArgument(param.numNodes < Integer.MAX_VALUE, "number of nodes in the tree exceed 2^31");
        nodes.add(new Node());
        stats.add(new RTreeNodeStat()); // 此处有问题
        leafVector.addAll(new ArrayList<Float>(param.sizeLeafVector));
        return nd;
    }
}
