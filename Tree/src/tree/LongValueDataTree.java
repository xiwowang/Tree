package tree;

import java.util.List;

import tree.base.DataTree;
import tree.base.SimpleLongValue;
import tree.base.TreeNode;
import tree.base.SimpleLongValue.Adjustor;
import tree.base.TreeNode.Recursion;

public class LongValueDataTree<ID> extends DataTree<ID, SimpleLongValue>{

	public LongValueDataTree(List<String> hierarchy) {
		super(hierarchy);
	}
	
	public LongValueDataTree(List<String> hierarchy, boolean isStatic) {
		super(hierarchy, isStatic);
	}
	
	public void breakDown(final SimpleLongValue des) throws Exception{
		if(!this.isStatic){
			this.root.sumUp();
		}
		
		final SimpleLongValue src = this.root.getValue();
		final Adjustor adj = des.new Adjustor();
		
		this.root.recursion(new Recursion<ID, SimpleLongValue>() {
			@Override
			public void doIfLeaf(TreeNode<ID, SimpleLongValue> node) {
				try {
					node.getValue().breakDown(src, des, adj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void doIfNotLeaf(TreeNode<ID, SimpleLongValue> node) {
				for( TreeNode<ID, SimpleLongValue> child : node.getChilds() ){
					child.recursion(this);
				}
			}
		});
		
		adj.adjust(des);
		
		if(this.isStatic){
			this.root.sumUp();
		}
	}

}
