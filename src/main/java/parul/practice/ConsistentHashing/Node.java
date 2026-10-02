package parul.practice.ConsistentHashing;

import java.util.ArrayList;
import java.util.List;

public class Node {
    String id;
    List<VirtualNode> virtualNodeList;

    public Node(String id){
        this.id = id;
        virtualNodeList = new ArrayList<>();
        // add virtual nodes;

        for(int i =0;i<150;i++){
            virtualNodeList.add(new VirtualNode(this,id+"-vn-"+i));
        }
    }

    public List<VirtualNode> getVirtualNodeList() {
        return virtualNodeList;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                '}';
    }
}
