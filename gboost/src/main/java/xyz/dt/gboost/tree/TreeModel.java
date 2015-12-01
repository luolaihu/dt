package xyz.dt.gboost.tree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by luolaihu on 12/1/15.
 */
public class TreeModel<TSplitCond, TNodeStat > {
    public TSplitCond splitCond;
    public TNodeStat nodeStat;
    public Param param;

    public ArrayList<Node> nodes;
    public Queue<Integer> deletedNodes ;
    public ArrayList<TNodeStat> stats;
    public Map<Integer, ArrayList<Float>> leafVectorMap;

    public TreeModel(){
        param.numNodes = 1;
        param.numRoots = 1;
        param.numDeleted = 0;
        this.nodes = Lists.newArrayListWithCapacity(param.numNodes);
        this.deletedNodes = Queues.newConcurrentLinkedQueue();
        this.stats = Lists.newArrayListWithCapacity(param.numNodes);
        this.leafVectorMap = Maps.newConcurrentMap();
        leafVectorMap.put(1, new ArrayList<Float>(param.sizeLeafVector));
    }

    public int allocNode(){
        if (param.numDeleted != 0){
            int nd = deletedNodes.poll();
            --param.numDeleted;
            return nd;
        }
        int nd = param.numNodes++;
        Preconditions.checkArgument(param.numNodes < Integer.MAX_VALUE, "number of nodes in the tree exceed 2^31");
        nodes.add(new Node());
        stats.add(nodeStat); // 此处有问题
        leafVectorMap.put(param.numNodes, new ArrayList<Float>(param.sizeLeafVector));
        return nd;
    }

    public void deleteNode(int nid){
        Preconditions.checkArgument(nid >= param.numRoots, "can not delete root");
        deletedNodes.offer(nid);
        nodes.get(nid).markDelete();
        ++param.numDeleted;
    }

    public void changeToLeaf(int rid, float value){
        Preconditions.checkArgument(nodes.get(nodes.get(rid).cLeft).isLeaf(), "can not delete a non termial child");
        Preconditions.checkArgument(nodes.get(nodes.get(rid).cRight).isLeaf(), "can not delete a non termial child");
        deleteNode(nodes.get(rid).cLeft);
        deleteNode(nodes.get(rid).cRight);
        nodes.get(rid).setLeaf(value, -1);
    }

    public void collapseToLeaf(int rid, float value){
        if (nodes.get(rid).isLeaf()) return;
        if (!nodes.get(nodes.get(rid).cLeft).isLeaf()){
            collapseToLeaf(nodes.get(rid).cLeft, 0.0f);
        }
        if (!nodes.get(nodes.get(rid).cRight).isLeaf()){
            collapseToLeaf(nodes.get(rid).cRight, 0.0f);
        }
        changeToLeaf(rid, value);
    }

    public Node getNode(int nid){
        return nodes.get(nid);
    }

    public TNodeStat getNodeStat(int nid){
        return stats.get(nid);
    }

    public List<Float> leafVec(int nid){
        if (leafVectorMap.size() == 0) return null;
        return leafVectorMap.get(nid);
    }

    public void InitModel(){
        param.numNodes = param.numRoots;
        this.nodes = Lists.newArrayListWithCapacity(param.numNodes);
        this.deletedNodes = Queues.newConcurrentLinkedQueue();
        this.stats = Lists.newArrayListWithCapacity(param.numNodes);
        this.leafVectorMap = Maps.newConcurrentMap();

        //未完成
    }


    class Param {
        public int numRoots;
        public int numNodes;
        public int numDeleted;
        public int maxDepth;
        public int numFeature;
        public int sizeLeafVector;
        public int[] reserved = new int[31];

        public Param(){
            this.maxDepth = 0;
            this.sizeLeafVector = 0;
        }

        public void setParam(String name, String value){
            switch (name){
                case "numRoots":
                    numRoots = Ints.tryParse(value);
                    break;
                case "numFeature":
                    numFeature = Ints.tryParse(value);
                    break;
                case "sizeLeafVector":
                    sizeLeafVector = Ints.tryParse(value);
            }
        }
    }

    class Node {
        public int parent;
        public int cLeft;
        public int cRight;
        public int sIndex;
        class Info{
            public float leafValue;
            public TSplitCond splitCond;
        }
        public Info info;

        public Node(){
            this.sIndex = 0;
        }

        public int cLeft(){
            return this.cLeft;
        }

        public int cRight(){
            return this.cRight;
        }

        public boolean defaultLeft(){
            return (sIndex >>> 31) != 0;
        }

        public int cDefault(){
            return this.defaultLeft() ? this.cLeft : this.cRight;
        }

        public int splitIndex(){
            return sIndex & ((1 << 31) - 1);
        }

        public boolean isLeaf(){
            return cLeft == -1;
        }

        public float leafValue(){
            return  this.info.leafValue;
        }

        public TSplitCond splitCond(){
            return this.info.splitCond;
        }

        public int parent(){
            return (parent & (1 << 31) - 1);
        }

        public boolean isLeftChild(){
            return (parent & (1 << 31)) != 0;
        }

        public boolean isDeleted(){
            return sIndex == Integer.MAX_VALUE;
        }

        public boolean isRoot(){
            return parent == -1;
        }

        public void setRightChild(int nid){
            this.cRight = nid;
        }

        public void setSplit(int splitIndex,
                             TSplitCond splitCond,
                             boolean defaultLeft){
            if (defaultLeft) splitIndex |= (1 << 31);
            this.sIndex = splitIndex;
            this.info.splitCond = splitCond;
        }

        public void setLeaf(float value, int right){
            this.info.leafValue = value;
            this.cLeft = -1;
            this.cRight = right;
        }

        public void markDelete(){
            this.sIndex = Integer.MAX_VALUE;
        }

        private void setParent(int pIdx, boolean isLeftChild){
            if (isLeftChild) pIdx |= (1 << 31);
            this.parent = pIdx;
        }
    }
}

