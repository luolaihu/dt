package xyz.dt.gboost.tree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import xyz.dt.gboost.data.BoosterInfo;
import xyz.dt.gboost.data.GradientPair;

import java.util.List;

/**
 * Created by luolaihu on 11/27/15.
 */
public class CVGradStats extends GradStats {
    public int vSize;
    public List<GradStats> train = Lists.newArrayList();
    public List<GradStats> valid = Lists.newArrayList();

    public CVGradStats(TrainParam param, int vSize){
        this.vSize = vSize;
        Preconditions.checkArgument(param.sizeLeafVector == vSize, "CVGradStats: vsize must match size_leaf_vector");
        this.clear();
    }

    @Override
    public void checkInfo(BoosterInfo info) {
        Preconditions.checkArgument(info.foldIndex.size() != 0, "CVGradStats: require fold_index");
    }

    @Override
    public void clear(){
        super.clear();
        for (int i = 0; i < vSize; ++i){
            train.get(i).clear();
            valid.get(i).clear();
        }
    }

    @Override
    public void add(List<GradientPair> pairs,
                    BoosterInfo info,
                    int ridx){
        super.add(pairs.get(ridx).grad, pairs.get(ridx).hess);
        int step = info.foldIndex.size();
        for (int i = 0; i < vSize; ++i){
            GradientPair pair = pairs.get((i + 1) * step + ridx);
            if (info.foldIndex.get(ridx) == i){
                valid.get(i).add(pair.grad, pair.hess);
            } else {
                train.get(i).add(pair.grad, pair.hess);
            }
        }
    }

    @Override
    public double calcGain(TrainParam param){
        double ret = 0.0;
        for (int i = 0; i < vSize; ++i){
            ret += param.calcGain(train.get(i).sumGrad,
                                train.get(i).sumHess,
                                valid.get(i).sumGrad,
                                valid.get(i).sumHess);
        }
        return ret / vSize;
    }

    public void add(CVGradStats b){
        super.add(b);
        for (int i = 0; i < vSize; ++i){
            train.get(i).add(b.train.get(i));
            valid.get(i).add(b.valid.get(i));
        }
    }

    public void reduce(CVGradStats a, CVGradStats b) {
        a.add(b);
    }

    public void setSubstract(CVGradStats a, CVGradStats b){
        super.setSubstract(a, b);
        for (int i = 0; i < vSize; ++i){
            train.get(i).setSubstract(a.train.get(i), b.train.get(i));
            valid.get(i).setSubstract(a.valid.get(i), b.valid.get(i));
        }
    }

    @Override
    public void setLeafVec(TrainParam param, List<Float> vec) {
        for (int i = 0 ; i < vSize; ++i){
            vec.set(i, (float) (param.learningRate *
                                param.calcWeight(train.get(i).sumGrad, train.get(i).sumHess)));
        }
    }
}
