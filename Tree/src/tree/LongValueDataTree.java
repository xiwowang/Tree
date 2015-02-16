package tree;

import java.util.List;

import tree.base.DataTree;
import tree.base.TreeNode;
import tree.base.TreeNode.Recursion;

import tree.value.SimpleLongValue;
import tree.value.SimpleLongValue.Adjustor;

public class LongValueDataTree<ID> extends DataTree<ID, SimpleLongValue>{

	public LongValueDataTree(List<String> hierarchy) {
		super(hierarchy);
	}
	
	public LongValueDataTree(List<String> hierarchy, boolean isStatic) {
		super(hierarchy, isStatic);
	}
	
	public void breakDown(final SimpleLongValue des) throws Exception{
		
		int leafCount = 0;
		if(!this.isStatic){
			leafCount = this.root.sumUp();
		}
		
		final SimpleLongValue src = this.root.getValue().deepClone();
		
		// 如果总数是0则均分,总数是0时才需要计算个数
		if(src.value==0){
			if(this.isStatic){
				leafCount = this.root.countLeaf();
			}
		}
		
		final int count = leafCount;
		final Adjustor adj = des.new Adjustor();
		
		this.root.recursion(new Recursion<ID, SimpleLongValue>() {
			@Override
			public void doIfLeaf(TreeNode<ID, SimpleLongValue> node) {
				try {
					node.getValue().breakDown(src, des, adj, count);
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
