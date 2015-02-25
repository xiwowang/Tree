package tree.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

// 值是可运算的(继承Collectable接口)，树本身也是可运算的，可以循环嵌套
public class DataTree<ID, V extends Collectable<V>> 
	implements Collectable<DataTree<ID, V>>{
	
	protected TreeNode<ID, V> root;
	protected List<String> hierarchy;
	
	// 数据是否会发生变化，如果针对不会变化的数据，会在每层保存聚合值
	protected boolean isStatic;
	
	public DataTree(List<String> hierarchy){
		this(hierarchy, false);
	}
	
	public DataTree(List<String> hierarchy, boolean isStatic){
		this(hierarchy, isStatic, new TreeNode<ID, V>(null, null));
	}
	
	public DataTree(List<String> hierarchy, boolean isStatic, TreeNode<ID, V> node){
		this.hierarchy = new ArrayList<String>(hierarchy);
		this.root = node;
		this.isStatic = isStatic;
	}

	public List<String> getHierarchy() {
		return Collections.unmodifiableList(this.hierarchy);
	}
	
	public void setHierarchy(List<String> hierarchy) {
		this.hierarchy = hierarchy;
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

	// 根据数据构建
	public <K> void build(List<K> dataList, TreeAdapter<K, ID, V> adapter) throws Exception{
		
		for(K data : dataList){
			List<ID> hierarchyIds = adapter.getHierachy(data);
			V v= adapter.getValue(data);
			TreeNode<ID, V> curNode = this.addHierachy(hierarchyIds);
			curNode.mergeValue(v);
		}
		if(this.isStatic){
			this.root.sumUp();
		}
	}

	// 添加数据，如果已存在值相加
	public void add(List<ID> hierarchyIds, V v) throws Exception{
		
		TreeNode<ID, V> curNode = this.addHierachy(hierarchyIds);
		curNode.mergeValue(v);
		
		if(this.isStatic){
			while(curNode.parent!=null){
				curNode = curNode.parent;
				curNode.mergeValue(v);
			}
		}
	}
	
	// 如果有则覆盖，没有则添加
	public void update(List<ID> hierarchyIds, V v) throws Exception{
		
		TreeNode<ID, V> curNode = this.addHierachy(hierarchyIds);
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
	
	// 查找,查找条件为ID
	public TreeNode<ID, V> find(List<ID> hierarchyIds) {
		int level = this.hierarchy.size();
		if(hierarchyIds==null ||this.hierarchy.size()>level){
			return null;
		}
		
		return this.root.find(hierarchyIds);
	}
	
	// 查找,查找条件非ID,根据提供的比较器进行ID的匹配
	public <K> TreeNode<ID, V> find(List<K> hierarchyKeys, Matcher<ID, K> m) {
		int level = this.hierarchy.size();
		if(hierarchyKeys==null ||this.hierarchy.size()>level){
			return null;
		}
		
		return this.root.find(hierarchyKeys, m);
	}
	
	// 查找出的节点封装成子树
	public DataTree<ID, V> subTree(List<ID> hierarchyIds) {
		int level = this.hierarchy.size();
		if(hierarchyIds==null ||this.hierarchy.size()>level){
			return null;
		}
		
		DataTree<ID, V> tree = new DataTree<ID, V>(this.hierarchy.subList(hierarchyIds.size(), level), this.isStatic);
		tree.root = this.root.find(hierarchyIds);
		return tree;
	}
	
	// 查找出的节点封装成子树,查找条件非ID,根据提供的比较器进行ID的匹配
	public <K> DataTree<ID, V> subTree(List<K> hierarchyKeys, Matcher<ID, K> m) {
		int level = this.hierarchy.size();
		if(hierarchyKeys==null ||this.hierarchy.size()>level){
			return null;
		}
		
		DataTree<ID, V> tree = new DataTree<ID, V>(this.hierarchy.subList(hierarchyKeys.size(), level), this.isStatic);
		tree.root = this.root.find(hierarchyKeys, m);
		return tree;
	}
	
	// 删除
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
	
	// 删除并将删除的节点封装成子树
	public DataTree<ID, V> removeSubTree(List<ID> hierarchyIds) throws Exception {
		int level = this.hierarchy.size();
		
		DataTree<ID, V> tree = new DataTree<ID, V>(this.hierarchy.subList(hierarchyIds.size(), level), this.isStatic);
		tree.root = this.remove(hierarchyIds);
		return tree;
	}
	
	// 筛选, 得到符合每层ID条件的子树
	public DataTree<ID, V> filter(Map<String, Set<ID>> hierarchyCriteria) throws Exception{
		DataTree<ID, V> tree = new DataTree<ID, V>(this.hierarchy, this.isStatic);
		TreeMap<Integer, Set<ID>> levelCriteriaMap = new TreeMap<Integer, Set<ID>>();
		for(Entry<String, Set<ID>> entry : hierarchyCriteria.entrySet()){
			int level = this.hierarchy.indexOf(entry.getKey());
			if(level < 0){
				throw new Exception("hierarchy" + entry.getKey() +  "doesn't exist in the tree");
			}
			levelCriteriaMap.put(level, entry.getValue());
		}
		tree.root = this.root.filter(levelCriteriaMap);
		
		if(tree.isStatic){
			tree.root.sumUp();
		}
		return tree;
	}
	
	// 筛选, 得到符合每层ID条件的子树,筛选条件不是ID的key,根据提供的比较器进行ID的匹配
	public <K> DataTree<ID, V> filter(Map<String, Set<K>> hierarchyCriteria, Matcher<ID, K> m) throws Exception{
		DataTree<ID, V> tree = new DataTree<ID, V>(this.hierarchy, this.isStatic);
		TreeMap<Integer, Set<K>> levelCriteriaMap = new TreeMap<Integer, Set<K>>();
		for(Entry<String, Set<K>> entry : hierarchyCriteria.entrySet()){
			int level = this.hierarchy.indexOf(entry.getKey());
			if(level < 0){
				throw new Exception("hierarchy" + entry.getKey() +  "doesn't exist in the tree");
			}
			levelCriteriaMap.put(level, entry.getValue());
		}
		tree.root = this.root.filter(levelCriteriaMap, m);
		
		if(tree.isStatic){
			tree.root.sumUp();
		}
		return tree;
	}
	
	// 将树型转变成对象的List
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
	
	// 修改树的层级，可以删除层级， 如[CLASS, GENDER, STYLE, COLOR] -> [STYLE, COLOR, GENDER]
	public DataTree<ID, V> convert(List<String> newHierarchy)  throws Exception{
		final List<Integer> levels = new ArrayList<Integer>();
		for(String id : newHierarchy){
			int level = this.hierarchy.indexOf(id);
    		if(level < 0){
    			throw new Exception("hierarchy" + id +  "doesn't exist in the tree");
    		}
    		levels.add(level);
    	}
		
		final DataTree<ID, V> tree = new DataTree<ID, V>(newHierarchy, this.isStatic);
		final List<ID> ids = new ArrayList<ID>();
		
		this.root.recursion(new Recursion<ID, V>(){

			@Override
			public void doIfLeaf(TreeNode<ID, V> node) {
				List<ID> curIds = new ArrayList<ID>();
				for(int i : levels){
					curIds.add(ids.get(i));
				}
				try {
					TreeNode<ID, V> curNode = tree.addHierachy(curIds);
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
	
	// 增加层次结构但是不设置值
	private TreeNode<ID, V> addHierachy(List<ID> hierarchyIds) throws Exception{
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
		
		this.root.merge(sdt.root);
	}


	@Override
	public void minus(DataTree<ID, V> sdt) throws Exception {
		if(!this.hierarchy.equals(sdt.hierarchy)){
			throw new Exception("can't minus tree of different hierarchy");
		}
		
		this.root.minus(sdt.root);
	}


	@Override
	public DataTree<ID, V> getEmptyInstance() {
		DataTree<ID, V> sdt = new DataTree<ID, V>(this.hierarchy, this.isStatic);
		return sdt;
	}

	@Override
	public DataTree<ID, V> clone() {
		DataTree<ID, V> sdt = new DataTree<ID, V>(this.hierarchy, this.isStatic);
		sdt.root = this.root.clone();
		return sdt;
	}

	@Override
	public DataTree<ID, V> deepClone() {
		DataTree<ID, V> sdt = new DataTree<ID, V>(this.hierarchy, this.isStatic);
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
