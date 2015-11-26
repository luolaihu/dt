package xyz.dt.gboost.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by luolaihu on 11/24/15.
 */
public interface IFeatureMatrix {
    public Iterator<RowBatch> rowIterator();
    public Iterator<ColBatch> colIterator();
    public Iterator<ColBatch> colIterator(List<Integer> fset);
    public void initColAccess(ArrayList<Boolean> fset, float subSample, int maxRowPerBatch);
    public boolean havaColAccess();
    public int getNumCol();
    public int getColSize();
    public float getColDensity();
    public List<Integer> getBufferedRowSet();
}
