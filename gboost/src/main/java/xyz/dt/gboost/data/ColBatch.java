package xyz.dt.gboost.data;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by luolaihu on 11/26/15.
 */
public class ColBatch extends SparseBatch{
    public @Setter
    @Getter
    List<Instance> instances;
    public Instance get(int index){
        Preconditions.checkArgument(instances.size() > index, "out range of list");
        return instances.get(index);
    }
}
