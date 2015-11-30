package xyz.dt.gboost.data;

/**
 * Created by luolaihu on 11/24/15.
 *
 */
public class GradientPair {
    public float grad;
    public float hess;
    public GradientPair(float grad, float hess){
        this.grad = grad;
        this.hess = hess;
    }
}
