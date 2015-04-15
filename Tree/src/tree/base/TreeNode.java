package tree.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import tree.intf.Collectable;
import tree.intf.Matcher;

public class TreeNode <ID, V extends Collectable<V>>  implements Collectable<TreeNode<ID, V>>{

	protected ID id;
	private V value;
	protected TreeNode<ID, V> parent;
	protected Map<ID, TreeNode<ID, V>> childs;
	
	public TreeNode(ID id, V v) {
		this.id = id;
		this.value = v;
	}

	public void recursion(Recursion<ID, V> r){
		if(this.childs==null || this.childs.isEmpty()){
			r.doIfLeaf(this);
			return;
		}
		
		r.doIfNotLeaf(this);
	}
	
	public interface Recursion<ID, V extends Collectable<V>>{
		public void doIfLeaf(TreeNode<ID, V> node);
		
		public void doIfNotLeaf(TreeNode<ID, V> node);
	}
	
	public void setId(ID id){
		if(this.parent==null){
			this.id = id;
		}else{
			TreeNode<ID, V> parentNode = this.parent;
			parentNode.removeChild(this.id);
			this.setId(id);
			try {
				parentNode.addChild(this);
			} catch (Exception e) {
				// never happen
			}
		}
	}
	
	public void addChild(ID id, V v){
		TreeNode<ID, V> child = this.newInstance(id, v);
		try {
			this.addChild(child);
		} catch (Exception e) {
			// never happen
		}
	}
	
	public void addChild(TreeNode<ID, V> node) throws Exception{
		if(node.parent!=null && node.parent!=this){
			throw new Exception("Node has parent when add to another parent!");
		}
		node.parent = this;
		if(this.childs == null){
			this.childs = this.getChildMap();
		}
		this.childs.put(node.id, node);
	}
	
	public boolean hasChild(ID id){
		if(this.childs==null){
			return false;
		}
		return this.childs.containsKey(id);
	}
	
	public TreeNode<ID, V> getChild(ID id){
		if(this.childs==null){
			return null;
		}
		return this.childs.get(id);
	}
	
	public List<TreeNode<ID, V>> getChilds(){
		if(this.childs==null){
			return null;
		}
		return new ArrayList<TreeNode<ID, V>>(this.childs.values());
	}
	
	public TreeNode<ID, V> getParent() {
		return this.parent;
	}

	public void setParent(TreeNode<ID, V> parent) {
		this.parent = parent;
	}

	public ID getId() {
		return this.id;
	}
	
	public List<ID> getHierachy(){
		ArrayList<ID> hierachys = new ArrayList<ID>();
		TreeNode<ID, V> curNode = this;
		while(curNode.parent!=null){
			hierachys.add(curNode.id);
			curNode = curNode.parent;
		}
		Collections.reverse(hierachys);
		return hierachys;
	}
	
	public TreeNode<ID, V> removeChild(ID id){
		TreeNode<ID, V> node = this.childs.remove(id);
		node.setParent(null);
		return node;
	}

	public void setValue(V v){
		this.value = v;
	}
	
	public V getValue(){
		return this.value;
	}

	public int sumUp() throws Exception {
		if(this.childs==null || this.childs.isEmpty()){
			return 1;
		}
		
		int count = 0;
		this.value = null;
		for(TreeNode<ID, V> node : this.childs.values()){
			count += node.sumUp();
			this.mergeValue( node.value );
		}
		return count;
	}
	
	public int countLeaf() {
		if(this.childs==null || this.childs.isEmpty()){
			return 1;
		}
		
		int count = 0;
		for(TreeNode<ID, V> node : this.childs.values()){
			count += node.countLeaf();
		}
		return count;
	}
	
	public TreeNode<ID, V> find(List<ID> ids){
		return this.find(ids, 0);
	}
	
	private TreeNode<ID, V> find(List<ID> ids, int level){
		if(level == ids.size()){
			return this;
		}
		
		if( !this.hasChild(ids.get(level)) ){
			return null;
		}else{
			return this.getChild(ids.get(level)).find(ids, level+1);
		}
	}
	
	public <K> TreeNode<ID, V> find(List<K> keys, Matcher<ID, K> m){
		return this.find(keys, 0, m);
	}
	
	private <K> TreeNode<ID, V> find(List<K> keys, int level, Matcher<ID, K> m){
		if(level == keys.size()){
			return this;
		}
		
		for(Entry<ID, TreeNode<ID, V>> entry : this.childs.entrySet()){
			if( m.match(entry.getKey(), keys.get(level)) ){
				return entry.getValue().find(keys, level+1, m);
			}
		}
		return null;
	}
	
	public TreeNode<ID, V> filter(TreeMap<Integer, Set<ID>> criteriaMap){
		Integer key = null;
		try{
			key = criteriaMap.firstKey();
		}catch(Exception e){
			// do nothing
		}
		TreeNode<ID, V> node = this.filter(criteriaMap, key, 0);
		return node;
	}
	
	private TreeNode<ID, V> filter(TreeMap<Integer, Set<ID>> criteriaMap, Integer curKey, int curLevel){
		if(curKey == null || this.childs==null || this.childs.isEmpty()){
			return this.clone();
		}
		
		TreeNode<ID, V> curNode = this.newInstance(this.id, this.value);
		
		Set<ID> keySet = this.childs.keySet();
		if(curKey == curLevel){
			Set<ID> valueSet = criteriaMap.get(curKey);
			for(ID id : keySet){
				if( valueSet.contains(id) ){
					TreeNode<ID, V> child = this.getChild(id);
					Integer nextKey = null;
					try{
						nextKey = criteriaMap.higherKey(curKey);
						TreeNode<ID, V> filteredChild = child.filter(criteriaMap, nextKey, curLevel+1);
						if(nextKey==null || (filteredChild!=null && (filteredChild.childs!=null && !filteredChild.childs.isEmpty()))){
							curNode.addChild( filteredChild );
						}
					}catch(Exception e){
						// never happen
					}
				}
			}
		}else{
			for(ID id : keySet){
				TreeNode<ID, V> child = this.getChild(id);
				try{
					TreeNode<ID, V> filteredChild = child.filter(criteriaMap, curKey, curLevel+1);
					if(filteredChild!=null && (filteredChild.childs!=null && !filteredChild.childs.isEmpty())){
						curNode.addChild( filteredChild );
					}
				}catch(Exception e){
					// never happen
				}
			}
		}
		return curNode;
	}
	
	public <K> TreeNode<ID, V> filter(TreeMap<Integer, Set<K>> criteriaMap, Matcher<ID, K> m){
		Integer key = null;
		try{
			key = criteriaMap.firstKey();
		}catch(Exception e){
			// do nothing
		}
		TreeNode<ID, V> node = this.filter(criteriaMap, key, 0, m);
		return node;
	}
	
	private <K> TreeNode<ID, V> filter(TreeMap<Integer, Set<K>> criteriaMap, Integer curKey, int curLevel, Matcher<ID, K> m){
		if(curKey == null || this.childs==null || this.childs.isEmpty()){
			return this.clone();
		}
		
		TreeNode<ID, V> curNode = this.newInstance(this.id, this.value);
		Set<ID> keySet = this.childs.keySet();
		if(curKey == curLevel){
			Set<K> valueSet = criteriaMap.get(curKey);
			for(ID id : keySet){
				for(K k : valueSet){
					if( m.match(id, k) ){
						TreeNode<ID, V> child = this.getChild(id);
						Integer nextKey = null;
						try{
							nextKey = criteriaMap.higherKey(curKey);
							TreeNode<ID, V> filteredChild = child.filter(criteriaMap, nextKey, curLevel+1, m);
							if(nextKey==null || (filteredChild!=null && (filteredChild.childs!=null && !filteredChild.childs.isEmpty()))){
								curNode.addChild( filteredChild );
							}
						}catch(Exception e){
							// never happen
						}
					}
				}
			}
		}else{
			for(ID id : keySet){
				TreeNode<ID, V> child = this.getChild(id);
				try{
					TreeNode<ID, V> filteredChild = child.filter(criteriaMap, curKey, curLevel+1, m);
					if(filteredChild!=null && (filteredChild.childs!=null && !filteredChild.childs.isEmpty())){
						curNode.addChild( filteredChild );
					}
				}catch(Exception e){
					// never happen
				}
			}
		}
		return curNode;
	}
	
	public TreeNode<ID, V> reduceLevel(List<Integer> levels){
		TreeNode<ID, V> node = this.newInstance(this.id, this.value);
		this.reduceLevel(node, levels, 0, levels.get(levels.size()-1));
		return node;
	}
	
	private TreeNode<ID, V> reduceLevel(TreeNode<ID, V> parent, List<Integer> levels, int curLevel, int endLevel){
		if(endLevel == curLevel){
			for(TreeNode<ID, V> child : this.childs.values()){
				TreeNode<ID, V> node = this.newInstance(child.id, child.value);
				try {
					if(parent.hasChild(node.id)){
						parent.getChild(node.id).merge(node);
					}else{
						child.parent = null;
						parent.addChild(node);
					}
				} catch (Exception e) {
					// never happen
				}
			}
			return parent;
		}
		
		if(levels.contains(curLevel)){
			for(TreeNode<ID, V> child : this.childs.values()){
				TreeNode<ID, V> node = this.newInstance(child.id, child.value);
				child.reduceLevel(node, levels, curLevel+1, endLevel);
				try {
					if(parent.hasChild(node.id)){
						parent.getChild(node.id).merge(node);
					}else{
						child.parent = null;
						parent.addChild(node);
					}
				} catch (Exception e) {
					// never happen
				}
			}
		}else{
			for(TreeNode<ID, V> child : this.childs.values()){
				child.reduceLevel(parent, levels, curLevel+1, endLevel);
			}
		}
		return parent;
	}
	
	@Override
	public void merge(TreeNode<ID, V> node) throws Exception{
		if(this.childs==null || this.childs.isEmpty()){
			this.mergeValue(node.value);
			return;
		}
		
		Set<ID> set = new HashSet<ID>(node.childs.keySet());
		set.removeAll(this.childs.keySet());
		
		for(Entry<ID, TreeNode<ID, V>> entry : this.childs.entrySet()){
			ID key = entry.getKey();
			if(node.hasChild(key)){
				entry.getValue().merge(node.getChild(key));
			}
		}
		
		for (Iterator<ID> iterator = set.iterator(); iterator.hasNext();) {
			ID id = iterator.next();
			this.addChild(node.getChild(id).clone());
		}
	}
	
	@Override
	public void minus(TreeNode<ID, V> node) throws Exception{
		if(this.childs==null || this.childs.isEmpty()){
			this.value.minus(node.value);
			return;
		}
		
		for(Entry<ID, TreeNode<ID, V>> entry : this.childs.entrySet()){
			ID key = entry.getKey();
			if(node.hasChild(key)){
				entry.getValue().minus(node.getChild(key));
			}
		}
	}
	
	@Override
	public TreeNode<ID, V> clone(){
		TreeNode<ID, V> clone = this.newInstance(this.id, this.value);
		if(this.childs!=null){
			clone.childs = this.getChildMap();
			clone.childs.putAll(this.childs);
		}
		return clone;
	}
	
	@Override
	public TreeNode<ID, V> deepClone(){
		if(this.childs==null || this.childs.isEmpty()){
			return this.newInstance(this.id, this.value.deepClone());
		}
		
		TreeNode<ID, V> clone;
		if(this.value!=null){
			clone = this.newInstance(this.id, this.value.deepClone());
		}else{
			clone = this.newInstance(this.id, null);
		}
		for(TreeNode<ID, V> child : this.childs.values()){
			try {
				clone.addChild( child.deepClone() );
			} catch (Exception e) {
				// never happen
			}
		}
		return clone;
	}

	@Override
	public TreeNode<ID, V> getEmptyInstance() {
		return this.newInstance(null, null);
	}
	
    @Override
	public String toString(){
    	StringBuilder uStrBuilder = new StringBuilder();
        return this.toString(uStrBuilder, "|-");
    }
    
    public String toString(StringBuilder uStrBuilder, String indent){
    	
    	if(this.childs!=null && !this.childs.isEmpty()){
    		uStrBuilder.append(indent).append(this.id).append("=").append(this.value).append("\n");
    		for(TreeNode<ID,V> child : this.childs.values()){
    			child.toString(uStrBuilder, "  " + indent);
    		}
    	}else{
    		uStrBuilder.append(indent).append(this.id).append("=").append(this.value).append("\n");
    	}
    	
        return uStrBuilder.toString();
    }
    
	
	public void  mergeValue(V v) throws Exception{
		if(this.value!=null){
			this.value.merge(v);
		}else if(v!=null){
			this.value=v.deepClone();
		}
	}
	
	public void minusValue(V v) throws Exception{
		if(this.value!=null){
			this.value.minus(v);
		}else if(v!=null){
			this.value.getEmptyInstance().minus(v.clone());
		}
	}
	
	// for Override
	protected TreeNode<ID, V> newInstance(ID id, V v){
		return new TreeNode<ID, V>(id, v);
	}
	
	// for Override
	public Map<ID, TreeNode<ID, V>> getChildMap(){
		return new HashMap<ID, TreeNode<ID, V>>();
	}
}
