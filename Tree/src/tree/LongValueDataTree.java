
package tree;

import java.util.List;

import tree.base.DataTree;
import tree.base.TreeNode;
import tree.base.TreeNode.Recursion;
import tree.value.SimpleLongValue;
import tree.value.SimpleLongValue.Adjustor;

/**
 * @version Change History:
 * @version <2>    03/16/15 MQ  Revisit for EN #17470: [CMM-Allocation]Allocation CategoryPlan part. (Mizzle Qiu)
 * @version <1>    03/03/15 MQ  First Written for Sub-EN #15800: based on the updated WF. (Mizzle Qiu)
 */
public class LongValueDataTree<ID> extends DataTree<ID, SimpleLongValue>{

	public LongValueDataTree(List<String> hierarchy) {
		super(hierarchy);
	}
	
	public LongValueDataTree(List<String> hierarchy, boolean isStatic) {
		super(hierarchy, isStatic);
	}
	
	public LongValueDataTree(List<String> hierarchy, boolean isStatic, TreeNode<ID, SimpleLongValue> node) {
		super(hierarchy, isStatic, node);
	}
	
	@Override
	public DataTree<ID, SimpleLongValue> newInstance(List<String> hierarchy, boolean isStatic){
		return new LongValueDataTree<ID>(hierarchy, isStatic);
	}
	
	public void breakDown(final SimpleLongValue des) throws Exception{
		
		int leafCount = 0;
		if(!this.isStatic){
			leafCount = this.root.sumUp();
		}
		
		final SimpleLongValue src = this.root.getValue().deepClone();
		
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
					// won't happen
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
