package xyz.dt.gboost.tree;

import xyz.dt.gboost.data.BoosterInfo;
import xyz.dt.gboost.data.GradientPair;

import java.util.List;

/**
 * Created by luolaihu on 11/27/15.
 */
public abstract class GradStats {
    public double sumGrad;
    public double sumHess;
    public static int kSimpleStats = 1;

    public  GradStats(TrainParam trainParam){
        clear();
    }

    public  GradStats(){
    }

    public void clear() {
        sumGrad = 0.0f;
        sumHess = 0.0f;
    }

    public abstract void checkInfo(BoosterInfo info);

    public void add(GradientPair pair){
        add(pair.grad, pair.hess);
    }

    public void add(List<GradientPair> pairs,
                    BoosterInfo info,
                    int ridx){
        GradientPair pair = pairs.get(ridx);
        add(pair.grad, pair.hess);
    }

    public void add(double grad, double hess) {
        sumGrad += grad;
        sumHess += hess;
    }

    public double calcWeight(TrainParam param){
        return param.calcWeight(sumGrad, sumHess);
    }

    public double calcGain(TrainParam param){
        return param.calcGain(sumGrad, sumHess);
    }

    public void add(GradStats b){
        add(b.sumGrad, b.sumHess);
    }

    public void reduce(GradStats a, GradStats b){
        a.add(b);
    }

    public void setSubstract(GradStats a, GradStats b){
        sumGrad = a.sumGrad - b.sumGrad;
        sumHess = a.sumHess - b.sumHess;
    }

    public boolean empty(){
        return sumHess == 0.0;
    }

    public abstract void setLeafVec(TrainParam param, List<Float> vec);
}
