
package tree.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import tree.base.TreeNode.Recursion;
import tree.intf.Collectable;
import tree.intf.FlatDataAdapter;
import tree.intf.Matcher;
import tree.intf.TreeAdapter;

public class DataTree<ID, V extends Collectable<V>> 
	implements Collectable<DataTree<ID, V>>{
	
	protected TreeNode<ID, V> root;
	protected List<String> hierarchy;
	
	// Whether the data is static.
	// For static data, every level's value is calculated and hold in the node. after change, every level's value will
	// auto synchronize.
	protected boolean isStatic;
	
	public DataTree(List<String> hierarchy){
		this(hierarchy, false);
	}
	
	public DataTree(List<String> hierarchy, boolean isStatic){
		this(hierarchy, isStatic, new TreeNode<ID, V>(null, null));
	}
	
	// Use your own node in the tree, the node should inherit TreeNode. 
	public DataTree(List<String> hierarchy, boolean isStatic, TreeNode<ID, V> node){
		this.hierarchy = new ArrayList<String>(hierarchy);
		this.root = node;
		this.isStatic = isStatic;
	}

	// Make sure the return list is unmodifiable.
	public List<String> getHierarchy() {
		return Collections.unmodifiableList(this.hierarchy);
	}
	
	public TreeNode<ID, V> getRoot() {
		return this.root;
	}

	public boolean isStatic() {
		return this.isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	// Recalculate every level's value.
	public void recalc() throws Exception{
		this.root.sumUp();
	}
	
	// Build the tree by the dataList.
	public <K> void build(List<K> dataList, TreeAdapter<K, ID, V> adapter) throws Exception{
		
		for(K data : dataList){
			List<ID> hierarchyIds = adapter.getHierarchy(data);
			V v= adapter.getValue(data);
			TreeNode<ID, V> curNode = this.addHierarchy(hierarchyIds);
			curNode.mergeValue(v);
		}
		if(this.isStatic){
			this.root.sumUp();
		}
	}
	
	// Build the tree by iterator
	public <K> void build(Iterator<K> itor,  TreeAdapter<K, ID, V> adapter) throws Exception{
		while(itor.hasNext()){
			K data = itor.next();
			List<ID> hierarchyIds = adapter.getHierarchy(data);
			V v= adapter.getValue(data);
			TreeNode<ID, V> curNode = this.addHierarchy(hierarchyIds);
			curNode.mergeValue(v);
		}
		if(this.isStatic){
			this.root.sumUp();
		}
	}

	// Add data to the tree. if the same hierarchy already exists, merge the value.
	public TreeNode<ID, V> add(List<ID> hierarchyIds, V v) throws Exception{
		
		TreeNode<ID, V> node = this.addHierarchy(hierarchyIds);
		node.mergeValue(v);
		
		TreeNode<ID, V> curNode = node;
		if(this.isStatic){
			while(curNode.parent!=null){
				curNode = curNode.parent;
				curNode.mergeValue(v);
			}
		}
		return node;
	}
	
	// Add data to the tree. if the same hierarchy already exists, cover the value.
	public TreeNode<ID, V> update(List<ID> hierarchyIds, V v) throws Exception{
		
		TreeNode<ID, V> node = this.addHierarchy(hierarchyIds);
		TreeNode<ID, V> curNode = node;
		if(this.isStatic){
			curNode.minusValue(v);
			V differ = curNode.getValue();
			curNode.setValue(v);
			while(curNode.parent!=null){
				curNode = curNode.parent;
				curNode.minusValue(differ);
			}
		}else{
			node.setValue(v);
		}
		return node;
	}
	
	// This one only update the node, can't add node if the node is not exists.
	public <K> TreeNode<ID, V> update(List<K> hierarchyIds, V v, Matcher<ID, K> m) throws Exception{
		
		TreeNode<ID, V> curNode = this.find(hierarchyIds, m);
		if(curNode!=null){
			if(this.isStatic){
				curNode.minusValue(v);
				V differ = curNode.getValue();
				curNode.setValue(v);
				while(curNode.parent!=null){
					curNode = curNode.parent;
					curNode.minusValue(differ);
				}
			}else{
				curNode.setValue(v);
			}
		}
		return curNode;
	}
	
	// Find the node by hierarchy, the node is a reference of the node in the tree.
	// Find by ID.
	public TreeNode<ID, V> find(List<ID> hierarchyIds) {
		int level = this.hierarchy.size();
		if(hierarchyIds==null ||this.hierarchy.size()>level){
			return null;
		}
		
		return this.root.find(hierarchyIds);
	}
	
	// Find by attributes of ID.
	public <K> TreeNode<ID, V> find(List<K> hierarchyKeys, Matcher<ID, K> m) {
		int level = this.hierarchy.size();
		if(hierarchyKeys==null ||this.hierarchy.size()>level){
			return null;
		}
		
		return this.root.find(hierarchyKeys, m);
	}
	
	// get subTree by hierarchy, the subTree's root is a reference of the node in the tree. you may need to deepClone
	// the subTree to get a tree which has no relationship with the original tree.
	// Find by ID.
	public DataTree<ID, V> subTree(List<ID> hierarchyIds) {
		int level = this.hierarchy.size();
		if(hierarchyIds==null ||this.hierarchy.size()>level){
			return null;
		}
		
		DataTree<ID, V> tree = this.newInstance(this.hierarchy.subList(hierarchyIds.size(), level), this.isStatic);
		if(this.root == null){
			tree.root = null;
		}else{
			tree.root = this.root.find(hierarchyIds);
		}
		return tree;
	}
	
	// Find by attributes of ID.
	public <K> DataTree<ID, V> subTree(List<K> hierarchyKeys, Matcher<ID, K> m) {
		int level = this.hierarchy.size();
		if(hierarchyKeys==null ||this.hierarchy.size()>level){
			return null;
		}
		
		DataTree<ID, V> tree = this.newInstance(this.hierarchy.subList(hierarchyKeys.size(), level), this.isStatic);
		if(this.root == null){
			tree.root = null;
		}else{
			tree.root = this.root.find(hierarchyKeys, m);
		}
		return tree;
	}
	
	// Delete the node of tree by hierarchy.
	public TreeNode<ID, V> remove(List<ID> hierarchyIds) throws Exception{
		TreeNode<ID, V> node = this.find(hierarchyIds);
		
		if(node!=null){
			if(this.isStatic){
				TreeNode<ID, V> curNode = node;
				V v = node.getValue();
				while(curNode.parent!=null){
					curNode = curNode.parent;
					curNode.minusValue(v);
				}
			}
			node.parent.removeChild(node.id);
		}
		
		return node;
	}
	
	// Delete the node of tree by hierarchy and get the deleted subTree .
	public DataTree<ID, V> removeSubTree(List<ID> hierarchyIds) throws Exception {
		int level = this.hierarchy.size();
		
		DataTree<ID, V> tree = this.newInstance(this.hierarchy.subList(hierarchyIds.size(), level), this.isStatic);
		tree.root = this.remove(hierarchyIds);
		return tree;
	}

	// Filter the tree by hierarchyCriteria. The result tree is not static even when the original is static.
	// The nodes of the level lower than the last criteria's level are references of the original tree and their
	// parent node are the original parent. If you don't want to change the original tree when use the result
	// you have to deepCopy the result.
	// Filter by ID.
	public DataTree<ID, V> filter(Map<String, Set<ID>> hierarchyCriteria) throws Exception{
		DataTree<ID, V> tree = this.newInstance(this.hierarchy, this.isStatic);
		TreeMap<Integer, Set<ID>> levelCriteriaMap = new TreeMap<Integer, Set<ID>>();
		for(Entry<String, Set<ID>> entry : hierarchyCriteria.entrySet()){
			int level = this.hierarchy.indexOf(entry.getKey());
			if(level < 0){
				throw new Exception("hierarchy " + entry.getKey() +  " doesn't exist in the tree");
			}
			levelCriteriaMap.put(level, entry.getValue());
		}
		tree.root = this.root.filter(levelCriteriaMap);
		if(tree.root.childs==null || tree.root.childs.isEmpty()){
			return null;
		}
		
		tree.setStatic(false);
		return tree;
	}
	
	// Filter by attributes of ID.
	public <K> DataTree<ID, V> filter(Map<String, Set<K>> hierarchyCriteria, Matcher<ID, K> m) throws Exception{
		DataTree<ID, V> tree = this.newInstance(this.hierarchy, this.isStatic);
		TreeMap<Integer, Set<K>> levelCriteriaMap = new TreeMap<Integer, Set<K>>();
		for(Entry<String, Set<K>> entry : hierarchyCriteria.entrySet()){
			int level = this.hierarchy.indexOf(entry.getKey());
			if(level < 0){
				throw new Exception("hierarchy " + entry.getKey() +  " doesn't exist in the tree");
			}
			levelCriteriaMap.put(level, entry.getValue());
		}
		tree.root = this.root.filter(levelCriteriaMap, m);
		if(tree.root.childs==null || tree.root.childs.isEmpty()){
			return null;
		}
		
		tree.setStatic(false);
		if(tree.isStatic){
			tree.root.sumUp();
		}
		return tree;
	}
	
	// Return List data from the tree.
	public <K> List<K> getFlatData(final FlatDataAdapter<K, ID, V> adp){
		final List<K> list = new ArrayList<K>();
		final List<String> hierarchy = new ArrayList<String>(this.hierarchy);
		final List<ID> ids = new ArrayList<ID>();
		
		this.root.recursion(new Recursion<ID, V>(){

			@Override
			public void doIfLeaf(TreeNode<ID, V> node) {
				List<ID> curIds = new ArrayList<ID>(ids);
				list.add( adp.getData(hierarchy, curIds, node.getValue()) );
			}

			@Override
			public void doIfNotLeaf(TreeNode<ID, V> node) {
				for(TreeNode<ID, V> child : node.getChilds()){
					ids.add(child.getId());
					child.recursion(this);
					ids.remove(ids.size()-1);
				}
			}
		});
		
		return new ArrayList<K>(list);
	}
	
	// Change the hierarchy order or delete hierarchy level of the tree.
	public DataTree<ID, V> convert(List<String> newHierarchy)  throws Exception{
		final List<Integer> levels = new ArrayList<Integer>();
		for(String id : newHierarchy){
			int level = this.hierarchy.indexOf(id);
    		if(level < 0){
    			throw new Exception("hierarchy " + id +  " doesn't exist in the tree");
    		}
    		levels.add(level);
    	}
		
		final DataTree<ID, V> tree = this.newInstance(newHierarchy, this.isStatic);
		final List<ID> ids = new ArrayList<ID>();
		
		this.root.recursion(new Recursion<ID, V>(){

			@Override
			public void doIfLeaf(TreeNode<ID, V> node) {
				List<ID> curIds = new ArrayList<ID>();
				for(int i : levels){
					curIds.add(ids.get(i));
				}
				try {
					TreeNode<ID, V> curNode = tree.addHierarchy(curIds);
					curNode.mergeValue(node.getValue());
				} catch (Exception e) {
					// never happen
				}
			}

			@Override
			public void doIfNotLeaf(TreeNode<ID, V> node) {
				for(TreeNode<ID, V> child : node.getChilds()){
					ids.add(child.getId());
					child.recursion(this);
					ids.remove(ids.size()-1);
				}
			}
		});
		
		if(this.isStatic){
			tree.root.sumUp();
		}
		return tree.clone();
	}
	
	// add hierarchy, the value is set to null.
	private TreeNode<ID, V> addHierarchy(List<ID> hierarchyIds) throws Exception{
		int level = this.hierarchy.size();
		if(hierarchyIds==null ||this.hierarchy.size()!=level){
			throw new Exception("hierarchyIds doesn't fit the tree");
		}
		
		TreeNode<ID, V> curNode = this.root;
		for(int i=0; i<level; i++){
			ID id = hierarchyIds.get(i);
			if(!curNode.hasChild(id)){
				try{
					curNode.addChild(id, null);
				}catch(Exception e){
					// never happen
				}
			}
			curNode = curNode.getChild(id);
		}
		return curNode;
	}


	@Override
	public void merge(DataTree<ID, V> sdt) throws Exception {
		if(!this.hierarchy.equals(sdt.hierarchy)){
			throw new Exception("can't merge tree of different hierarchy");
		}
		
		if(sdt.root == null){
			return;
		}
		
		if(this.root == null || this.root.childs == null){
			this.root = sdt.root.deepClone();
		}else{
			this.root.merge(sdt.root);
		}
	}


	@Override
	public void minus(DataTree<ID, V> sdt) throws Exception {
		if(!this.hierarchy.equals(sdt.hierarchy)){
			throw new Exception("can't minus tree of different hierarchy");
		}
		
		this.root.minus(sdt.root);
	}

	public DataTree<ID, V> newInstance(List<String> hierarchy, boolean isStatic){
		return new DataTree<ID, V>(hierarchy, isStatic);
	}

	@Override
	public DataTree<ID, V> getEmptyInstance() {
		DataTree<ID, V> sdt = this.newInstance(this.hierarchy, this.isStatic);
		return sdt;
	}

	@Override
	public DataTree<ID, V> clone() {
		DataTree<ID, V> sdt = this.newInstance(this.hierarchy, this.isStatic);
		sdt.root = this.root.clone();
		return sdt;
	}

	@Override
	public DataTree<ID, V> deepClone() {
		DataTree<ID, V> sdt = this.newInstance(this.hierarchy, this.isStatic);
		sdt.root = this.root.deepClone();
		return sdt;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Hierarchy: " + Arrays.toString(this.hierarchy.toArray())).append("\n");
		sb.append("Static: " + this.isStatic).append("\n");
		sb.append("Data: " + "\n" + this.root.toString());
		return sb.toString();
	}
}
