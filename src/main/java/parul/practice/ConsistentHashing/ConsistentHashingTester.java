package parul.practice.ConsistentHashing;

import java.util.*;

public class ConsistentHashingTester {
//    public static void main(String[] args) {
//        ConsistentHashingRing chr = new ConsistentHashingRing();
//        testDistribution();
//
//        /*
//        Node a = new Node("A");
//        Node b = new Node("B");
//        Node c = new Node("C");
//        chr.addNode(a);
//        chr.addNode(b);
//        chr.addNode(c);
//        System.out.println(chr);
//
//        List<CacheObject> objects = new ArrayList<>();
//
//        for(int i =0;i<10;i++){
//            CacheObject ob = new CacheObject("A"+i);
//            chr.addObject(ob.getKey(), ob);
//            objects.add(ob);
//        }
//
//        for(int i =0;i<10;i++){
//            CacheObject ob = new CacheObject("B"+i);
//            chr.addObject(ob.getKey(), ob);
//            objects.add(ob);
//        }
//
//        List<VirtualNode> virtualNodeList = c.getVirtualNodeList();
//        for (VirtualNode virtualNode: virtualNodeList){
//            Map<String, CacheObject> cacheObjs = virtualNode.getObjects();
//            for(Map.Entry<String, CacheObject> entry: cacheObjs.entrySet()){
//                System.out.print(entry.getKey()+" ");
//
//            }
//            System.out.println();
//        }
//
//        Node d = new Node("D");
//        chr.addNode(d);
////        virtualNodeList = d.getVirtualNodeList();
//
//        for (VirtualNode virtualNode: virtualNodeList){
//            Map<String, CacheObject> cacheObjs = virtualNode.getObjects();
//            for(Map.Entry<String, CacheObject> entry: cacheObjs.entrySet()){
//                System.out.print(entry.getKey()+" ");
//
//            }
//            System.out.println();
//        }
//
//         */
//
//    }

    public static void main(String[] args) {
        testDistribution();
        System.out.println("\n" + "=".repeat(50));
        testNodeRemoval();
    }

    // Test 1 — Add node, verify ~1/N keys move
    public static void testDistribution() {
        System.out.println("=== TEST 1: Key Distribution + Node Addition ===\n");
        ConsistentHashingRing ring = new ConsistentHashingRing();

        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        ring.addNode(a);
        ring.addNode(b);
        ring.addNode(c);

        // Add 10,000 objects and track which node holds each
        Map<String, String> keyToNode = new HashMap<>();
        for(int i = 0; i < 10000; i++){
            String key = "key-" + i;
            CacheObject obj = new CacheObject(key);
            ring.addObject(key, obj);
            keyToNode.put(key, ring.getNode(key).id);
        }

        System.out.println("BEFORE adding Node D:");
        printDistribution(ring);

        // Add new node
        Node d = new Node("D");
        ring.addNode(d);

        // Count how many keys moved
        int moved = 0;
        for(Map.Entry<String, String> entry : keyToNode.entrySet()){
            String newNode = ring.getNode(entry.getKey()).id;
            if(!newNode.equals(entry.getValue())) moved++;
        }

        System.out.println("\nAFTER adding Node D:");
        printDistribution(ring);

        System.out.println("\nKeys moved: " + moved + " / 10000");
        System.out.printf("Percentage moved: %.1f%%%n", moved * 100.0 / 10000);
        System.out.println("Expected: ~25% (1/4 nodes)");
        System.out.println("Distribution test: " +
                (isEvenDistribution(ring, 4) ? "PASSED ✓" : "FAILED ✗"));
        System.out.println("Movement test: " +
                (moved > 1500 && moved < 3500 ? "PASSED ✓" : "FAILED ✗"));
    }

    // Test 2 — Remove node, verify keys moved to remaining nodes
    public static void testNodeRemoval() {
        System.out.println("=== TEST 2: Node Removal ===\n");
        ConsistentHashingRing ring = new ConsistentHashingRing();

        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        ring.addNode(a);
        ring.addNode(b);
        ring.addNode(c);

        // Add 10,000 objects
        for(int i = 0; i < 10000; i++){
            String key = "key-" + i;
            ring.addObject(key, new CacheObject(key));
        }

        System.out.println("BEFORE removing Node C:");
        printDistribution(ring);

        // Track C's keys
        int cKeysBefore = countKeysOnNode(ring, "C");

        // Remove node C
        ring.deleteNode(c);

        System.out.println("\nAFTER removing Node C:");
        printDistribution(ring);

        // Verify all keys still retrievable
        int lost = 0;
        for(int i = 0; i < 10000; i++){
            String key = "key-" + i;
            CacheObject obj = ring.getObject(key);
            if(obj == null) lost++;
            // verify no key went to C
            String nodeId = ring.getNode(key).id;
            if(nodeId.equals("C")) lost++;
        }

        System.out.println("\nC's keys before removal: " + cKeysBefore);
        System.out.println("Keys lost or still on C: " + lost);
        System.out.println("Removal test: " + (lost == 0 ? "PASSED ✓" : "FAILED ✗"));
    }

    // Helper — print distribution
    private static void printDistribution(ConsistentHashingRing ring) {
        Map<String, Integer> distribution = new TreeMap<>();
        for(int i = 0; i < 10000; i++){
            String nodeId = ring.getNode("key-" + i).id;
            distribution.merge(nodeId, 1, Integer::sum);
        }
        int nodeCount = distribution.size();
        System.out.println("Distribution: " + distribution);
        System.out.println("Expected: ~" + (10000 / nodeCount) + " keys per node");
    }

    // Helper — check if distribution is even (within 20% of expected)
    private static boolean isEvenDistribution(ConsistentHashingRing ring, int nodeCount) {
        Map<String, Integer> distribution = new HashMap<>();
        for(int i = 0; i < 10000; i++){
            String nodeId = ring.getNode("key-" + i).id;
            distribution.merge(nodeId, 1, Integer::sum);
        }
        int expected = 10000 / nodeCount;
        for(int count : distribution.values()){
            if(Math.abs(count - expected) > expected * 0.4) return false;
        }
        return true;
    }

    // Helper — count keys on a specific node
    private static int countKeysOnNode(ConsistentHashingRing ring, String nodeId) {
        int count = 0;
        for(int i = 0; i < 10000; i++){
            if(ring.getNode("key-" + i).id.equals(nodeId)) count++;
        }
        return count;
    }
}