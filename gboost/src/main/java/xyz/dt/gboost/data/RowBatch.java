package xyz.dt.gboost.data;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by luolaihu on 11/26/15.
 */
public class RowBatch extends SparseBatch {
    public List<Instance> instances;
    public Instance get(int index){
        Preconditions.checkArgument(instances.size() > index, "out range of list");
        return instances.get(index);
    }
}
