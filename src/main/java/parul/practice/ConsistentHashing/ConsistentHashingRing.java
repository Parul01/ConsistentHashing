package parul.practice.ConsistentHashing;

import com.google.common.hash.Hashing;

import java.util.*;


public class ConsistentHashingRing {
    private TreeMap<Integer, VirtualNode> ring;

    public ConsistentHashingRing(){
        ring = new TreeMap<>();
    }

    public void addObject(String key, CacheObject o){
//        int hashPos = key.hashCode();
//        Map.Entry<Integer, VirtualNode> entry = ring.ceilingEntry(hashPos);
//        if(entry == null){
//            entry = ring.firstEntry();
//        }
//        VirtualNode vn = entry.getValue();
        VirtualNode vn = getVN(key);
        vn.addObject(key, o);
    }
    public void removeObject(String key){

//        int hashPos = hash(key);
//
//        Map.Entry<Integer, VirtualNode> entry = ring.ceilingEntry(hashPos);
//        if(entry == null){
//            entry = ring.firstEntry();
//        }
//        VirtualNode vn = entry.getValue();
        VirtualNode vn = getVN(key);
        vn.removeObject(key);

    }
    public CacheObject getObject(String key){
        VirtualNode vn = getVN(key);
        return vn.getObject(key);
    }

    public VirtualNode getVN(String key){
        int hashPos = hash(key);
        Map.Entry<Integer, VirtualNode> entry = ring.ceilingEntry(hashPos);
        if(entry == null){
            entry = ring.firstEntry();
        }
        VirtualNode vn = entry.getValue();
        return vn;
    }

    public void deleteNode(Node node){
        // rehash required
        List<VirtualNode> virtualNodeList = node.getVirtualNodeList();
        // step 1 - get all objects of from all virtual nodes
        // step 2 - fpr each virtual node , get the next virtual node in ring - shift objects to next virtual node
        // step 3 - we will do this in 3 phase (first add all objects in the next node in ring phase 2- remove to_delete vn  from ring phase 3 - delete all objects (optional)
        for(VirtualNode to_delete: virtualNodeList){
            int hashPos = hash(to_delete.getVirtualId());
            // hashPos+1 could overflow if hashPos is Integer.MAX_VALUE. Use ring.higherEntry(hashPos) instead:
            Map.Entry<Integer, VirtualNode> entry = ring.higherEntry(hashPos);
            if(entry == null){
                entry = ring.firstEntry();
            }
            VirtualNode to_shift = entry.getValue();
            Map<String, CacheObject> objects = to_delete.getObjects();
            // shift objects from vn to to_shift
            for(Map.Entry<String, CacheObject> entry1: objects.entrySet()){
                to_shift.addObject(entry1.getKey(), entry1.getValue());
            }
            // delete from ring
            ring.remove(hash(to_delete.getVirtualId())); // should we pass key or object?
            Iterator<Map.Entry<String, CacheObject>> it = objects.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, CacheObject> entry1 = it.next();
                it.remove();
            }
        }
    }

    public void addNode(Node node){
        // rehash required
        List<VirtualNode> virtualNodeList = node.getVirtualNodeList();
        // lower_bound virtual node -- upper_bound virtual node
        // add in the hash ring

        for(VirtualNode vn : virtualNodeList){


            //1    4    6
            // all objects for 6, get hascode and then move to virtual node vn
            int hashPos = hash(vn.getVirtualId());
            if(ring.size() == 0){
                ring.put(hashPos, vn);
                continue;
            }
            Map.Entry<Integer, VirtualNode> entry =
                    ring.ceilingEntry(hashPos);
            if(entry == null){
                entry = ring.firstEntry();
            }
            VirtualNode existing = entry.getValue();


            Map<String, CacheObject> objects = existing.getObjects();

            ring.put(hashPos, vn);
            // Exception in thread "main" java.util.ConcurrentModificationException
//            for(Map.Entry<String, CacheObject> entry1: objects.entrySet()){

            Iterator<Map.Entry<String, CacheObject>> it = objects.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, CacheObject> entry1 = it.next();
                VirtualNode virtualNode = getVN(entry1.getKey());
                if(virtualNode.equals(existing)) continue;
                vn.addObject(entry1.getKey(), entry1.getValue());
                it.remove();
            }

        }
    }

    /*

    private void reshash(String key, CacheObject obj, VirtualNode existing, VirtualNode curr){
        VirtualNode vn = getVN(key);
        if(vn.equals(existing)) return;
        // one issue is few some time old node will be non existent - ths is was simpler logic - fixed
//        you can also add object to new vn and only then remove from old
//        removeObject(key);
        addObject(key, obj);
        existing.removeObject(key);
    }
    */

    public Node getNode(String key) {
        return getVN(key).getParent();
    }

    @Override
    public String toString() {
        return "ConsistentHashingRing{" +
                "ring=" + ring +
                '}';
    }

    private int hash(String key) {
        return Hashing.murmur3_32_fixed()
                .hashUnencodedChars(key)
                .asInt() & 0x7fffffff;  // ← make positive
    }

}
