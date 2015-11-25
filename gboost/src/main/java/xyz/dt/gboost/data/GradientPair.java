package xyz.dt.gboost.data;

/**
 * Created by luolaihu on 11/24/15.
 *
 */
public class GradientPair {
    public final float grad;
    public final float hess;
    public GradientPair(float grad, float hess){
        this.grad = grad;
        this.hess = hess;
    }
}
