# ConsistentHashing
### Using hashCode() gives bad distribution
```text
ConsistentHashingRing{ring={1906075751=VirtualNode{parent=Node{id='A'}, virtualId='A-vn-0'}, 1906075752=VirtualNode{parent=Node{id='A'}, virtualId='A-vn-1'}, 1906075753=VirtualNode{parent=Node{id='A'}, virtualId='A-vn-2'}, 1934704902=VirtualNode{parent=Node{id='B'}, virtualId='B-vn-0'}, 1934704903=VirtualNode{parent=Node{id='B'}, virtualId='B-vn-1'}, 1934704904=VirtualNode{parent=Node{id='B'}, virtualId='B-vn-2'}, 1963334053=VirtualNode{parent=Node{id='C'}, virtualId='C-vn-0'}, 1963334054=VirtualNode{parent=Node{id='C'}, virtualId='C-vn-1'}, 1963334055=VirtualNode{parent=Node{id='C'}, virtualId='C-vn-2'}}}
```

```text
    A-vn-0 → 1906075750
    A-vn-1 → 1906075752
    A-vn-2 → 1906075753

    B-vn-0 → 1934704902
    B-vn-1 → 1934704903
    B-vn-2 → 1934704904
    
    C-vn-0 → 1963334053
    C-vn-1 → 1963334054
    C-vn-2 → 1963334055
```

### Using MurmurHash to avoid clustering that happens with  java hashCode()
```text
ConsistentHashingRing{ring={-1804354225=VirtualNode{parent=Node{id='B'}, virtualId='B-vn-2'}, -1709064735=VirtualNode{parent=Node{id='A'}, virtualId='A-vn-0'}, -1685830216=VirtualNode{parent=Node{id='B'}, virtualId='B-vn-0'}, -1661612644=VirtualNode{parent=Node{id='B'}, virtualId='B-vn-1'}, 1318732=VirtualNode{parent=Node{id='C'}, virtualId='C-vn-2'}, 1107826846=VirtualNode{parent=Node{id='C'}, virtualId='C-vn-1'}, 1302653999=VirtualNode{parent=Node{id='A'}, virtualId='A-vn-2'}, 1791686073=VirtualNode{parent=Node{id='C'}, virtualId='C-vn-0'}, 1852155904=VirtualNode{parent=Node{id='A'}, virtualId='A-vn-1'}}}

```

```text
B ─ A ─ B ─ B ─ C ─ C ─ A ─ C ─ A
```


```text
=== TEST 1: Key Distribution + Node Addition ===

BEFORE adding Node D:
Distribution: {A=3221, B=3439, C=3340}
Expected: ~3333 keys per node

AFTER adding Node D:
Distribution: {A=2528, B=2501, C=2356, D=2615}
Expected: ~2500 keys per node

Keys moved: 2615 / 10000
Percentage moved: 26.2%
Expected: ~25% (1/4 nodes)
Distribution test: PASSED ✓
Movement test: PASSED ✓

==================================================
=== TEST 2: Node Removal ===

BEFORE removing Node C:
Distribution: {A=3221, B=3439, C=3340}
Expected: ~3333 keys per node

AFTER removing Node C:
Distribution: {A=4943, B=5057}
Expected: ~5000 keys per node

C's keys before removal: 3340
Keys lost or still on C: 0
Removal test: PASSED ✓
```
