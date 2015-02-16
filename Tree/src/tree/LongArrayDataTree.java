package tree;

import java.util.List;

import tree.base.DataTree;
import tree.base.TreeNode;
import tree.base.TreeNode.Recursion;
import tree.value.LongArrayValue;
import tree.value.LongArrayValue.Adjustor;

public class LongArrayDataTree<ID> extends DataTree<ID, LongArrayValue>{
	
	public LongArrayDataTree(List<String> hierarchy) {
		super(hierarchy);
	}
	
	public LongArrayDataTree(List<String> hierarchy, boolean isStatic) {
		super(hierarchy, isStatic);
	}
	
	public void breakDown(final LongArrayValue des, final int[] indexes) throws Exception{
		
		int leafCount = 0;
		if(!this.isStatic){
			leafCount = this.root.sumUp();
		}
		
		if(!this.isStatic){
			this.root.sumUp();
		}
		
		final LongArrayValue src = this.root.getValue().deepClone();
		// 如果总数是0则均分,总数是0时才需要计算个数
		if(this.isStatic){
			for(int index : indexes){
				if(src.value[index] == 0){
					leafCount = this.root.countLeaf();
					break;
				}
			}
		}
		
		final int count = leafCount;
		final Adjustor adj = des.new Adjustor(indexes);
		
		this.root.recursion(new Recursion<ID, LongArrayValue>() {
			@Override
			public void doIfLeaf(TreeNode<ID, LongArrayValue> node) {
				try {
					node.getValue().breakDown(src, des, indexes, adj, count);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void doIfNotLeaf(TreeNode<ID, LongArrayValue> node) {
				for( TreeNode<ID, LongArrayValue> child : node.getChilds() ){
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
