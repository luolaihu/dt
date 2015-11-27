package xyz.dt.gboost.tree;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

/**
 * Created by luolaihu on 11/27/15.
 */
public class TrainParam {
    public float learningRate;
    public float minSplitLoss;
    public int maxDepth;
    public float minChildWeight;
    public float regLambda;
    public float regAlpha;
    public int defaultDirection;
    public float maxDeltaStep;
    public float subSample;
    public float colSampleByLevel;
    public float colSampleByTree;
    public float optDenseCol;
    public float sketchEps;
    public float sketchRatio;
    public int sizeLeafVector;
    public int parallelOption;
    public int cacheOpt;
    public int nThread;

    public  TrainParam(){
        this.learningRate = 0.3f;
        this.minSplitLoss = 0.0f;
        this.minChildWeight = 1.0f;
        this.maxDeltaStep = 0.0f;
        this.maxDepth = 6;
        this.regLambda = 1.0f;
        this.regAlpha = 0.0f;
        this.defaultDirection = 0;
        this.subSample = 1.0f;
        this.colSampleByTree = 1.0f;
        this.colSampleByLevel = 1.0f;
        this.optDenseCol = 1.0f;
        this.nThread = 0;
        this.sizeLeafVector = 0;
        this.parallelOption = 0;
        this.sketchEps = 0.1f;
        this.sketchRatio = 2.0f;
        this.cacheOpt = 1;
    }

    public void setParam(String name, String value){
        switch (name) {
            case "gamma":
                this.minSplitLoss = Floats.tryParse(value);
                break;
            case "eta":
                this.learningRate = Floats.tryParse(value);
                break;
            case "lambda":
                this.regLambda = Floats.tryParse(value);
                break;
            case "alpha":
                this.regAlpha = Floats.tryParse(value);
                break;
            case "learingRate":
                this.learningRate = Floats.tryParse(value);
                break;
            case "minChildWeight":
                this.minChildWeight = Floats.tryParse(value);
                break;
            case "maxDeltaStep":
                this.maxDeltaStep = Floats.tryParse(value);
                break;
            case "regLambda":
                this.regLambda = Floats.tryParse(value);
                break;
            case "regAlpha":
                this.regAlpha = Floats.tryParse(value);
                break;
            case "subSample":
                this.subSample = Floats.tryParse(value);
                break;
            case "colSampleByLevel":
                this.colSampleByLevel = Floats.tryParse(value);
                break;
            case "colSampleByTree":
                this.colSampleByTree = Floats.tryParse(value);
                break;
            case "sketchEps":
                this.sketchEps = Floats.tryParse(value);
                break;
            case "sketchRatio":
                this.sketchRatio = Floats.tryParse(value);
                break;
            case "optDenseCol":
                this.optDenseCol = Floats.tryParse(value);
                break;
            case "sizeLeafVector":
                this.sizeLeafVector = Ints.tryParse(value);
                break;
            case "cacheOpt":
                this.cacheOpt = Ints.tryParse(value);
                break;
            case "maxDepth":
                this.maxDepth = Ints.tryParse(value);
                break;
            case "nThread":
                this.nThread = Ints.tryParse(value);
                break;
            case "parallelOption":
                this.parallelOption = Ints.tryParse(value);
                break;
            case "defaultDirection":
                switch (value){
                    case "learn":
                        this.defaultDirection = 0;
                        break;
                    case "left":
                        this.defaultDirection = 1;
                        break;
                    case "right":
                        this.defaultDirection = 2;
                        break;
                }
                break;
        }
    }

    /**
     * l1 cost function
     * @param w
     * @param lambda
     * @return
     */
    public double thresholdL1(double w, double lambda){
        if (w > +lambda){
            return w - lambda;
        }
        if (w < -lambda){
            return w + lambda;
        }
        return 0.0;
    }

    /**
     * leaf node weight with l1 l2 reg net
     * @param sumGrad
     * @param sumHess
     * @return
     */
    public double calcWeight(double sumGrad, double sumHess){
        if (sumHess < minChildWeight) return 0.0;
        double dw = 0.0;
        if (regAlpha == 0.0f){
            dw = - sumGrad / ( sumHess + regLambda);
        } else {
            dw = - thresholdL1(sumGrad, regAlpha) / (sumHess + regLambda);
        }
        if (maxDeltaStep != 0.0f){
            if (dw > maxDeltaStep) dw = maxDeltaStep;
            if (dw < -maxDeltaStep) dw = -maxDeltaStep;
        }
        return dw;
    }

    public double calcGain(double sumGrad, double sumHess){
        if (sumHess < minChildWeight) return 0.0;
        if (maxDeltaStep == 0.0f){
            if (regAlpha == 0.0f){
                return Math.sqrt(sumGrad) / ( sumHess + regLambda);
            } else {
                return Math.sqrt(thresholdL1(sumGrad, regAlpha)) / ( sumHess + regLambda);
            }
        } else {
            double w = calcWeight(sumGrad, sumHess);
            double ret = sumGrad * w + 0.5 * (sumHess + regLambda) * Math.sqrt(w);
            if (regAlpha == 0.0f){
                return -2.0 * ret;
            } else {
                return -2.0 * (ret + regAlpha * Math.abs(w));
            }
        }
    }

    public double calcGain(double sumGrad, double sumHess,
                            double testGrad, double testHess){
        double w = calcWeight(sumGrad, sumHess);
        double ret = testGrad * w + 0.5 * (testHess + regLambda) * Math.sqrt(w);
        if (regAlpha == 0.0f){
            return -2.0 * ret;
        } else {
            return -2.0 * (ret + regAlpha * Math.abs(w));
        }
    }

    public boolean needForwardSearch(float colDensity, boolean indicator){
       return this.defaultDirection == 2 ||
               (defaultDirection == 0 && (colDensity < optDenseCol) && !indicator);
    }

    public boolean needBackwardSearch(float colDensity, boolean indicator){
        return this.defaultDirection != 2;
    }

    public boolean needPrune(double lossChg, int depth){
        return lossChg < this.minSplitLoss;
    }

    public boolean cannotSplit(double sumHess, int depth){
        return  sumHess < minChildWeight * 2.0;
    }

    public int maxSketchSize(){
        int ret = (int) (sketchRatio / sketchEps);
        Preconditions.checkArgument(ret > 0 , "sketchRatio / sketchEps must be bigger than 1");
        return ret;
    }
}
