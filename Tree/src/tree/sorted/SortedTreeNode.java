
package tree.sorted;

import java.util.Map;
import java.util.TreeMap;

import tree.base.TreeNode;
import tree.intf.Collectable;

public class SortedTreeNode<ID, V extends Collectable<V>> extends TreeNode<ID, V>{
	
	public SortedTreeNode(ID id, V v) {
		super(id, v);
	}

	@Override
	public TreeNode<ID, V> newInstance(ID id, V v) {
		return new SortedTreeNode(id, v);
	}
	
	@Override
	public Map<ID, TreeNode<ID, V>> getChildMap(){
		return new TreeMap<ID, TreeNode<ID, V>>();
	}
}