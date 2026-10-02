package parul.practice.ConsistentHashing;

import java.util.HashMap;
import java.util.Map;

public class VirtualNode {
    private String virtualId;
    private Node parent;
    private Map<String, CacheObject> objects;

    public VirtualNode(Node parent, String id) {
        this.parent = parent;
        objects = new HashMap<>();
        virtualId = id;
    }

    public String getVirtualId() {
        return virtualId;
    }

    public Node getParent() {
        return parent;
    }

    public Map<String, CacheObject> getObjects() {
        return objects;
    }

    public CacheObject getObject(String key){
        return objects.get(key);
    }

    public void addObject(String key, CacheObject o){
        objects.put(key, o);
    }

    public void removeObject(String key){
        objects.remove(key);
    }

    @Override
    public String toString() {
        return "VirtualNode{" +
                "parent=" + parent +
                ", virtualId='" + virtualId + '\'' +
                '}';
    }
}
