package xyz.dt.gboost.data;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created by luolaihu on 11/26/15.
 */
public abstract class SparseBatch {
    public int size;
    class Entry {
        public int index;
        public float fValue;
        public  Entry(){
        }
        public Entry(int index, float fValue){
            this.index = index;
            this.fValue = fValue;
        }
        public boolean cmpValue(Entry a, Entry b){
            return a.fValue < b.fValue;
        }
    }

    class Instance {
        public List<Entry> data;
        public Instance(List<Entry> data){
            this.data = data;
        }

        public Entry get(int index){
            Preconditions.checkArgument(data.size() > index, "out range of list");
            return data.get(index);
        }
    }
}
