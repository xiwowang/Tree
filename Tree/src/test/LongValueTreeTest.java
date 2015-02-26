package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import tree.IDNumName;
import tree.LongValueDataTree;
import tree.base.DataTree;
import tree.base.TreeNode;
import tree.value.SimpleLongValue;
import tree.intf.TreeAdapter;

public class LongValueTreeTest {
	
	class SortedTreeNode extends TreeNode<IDNumName, SimpleLongValue>{
		
		public SortedTreeNode(IDNumName id, SimpleLongValue v) {
			super(id, v);
		}

		@Override
		public TreeNode<IDNumName, SimpleLongValue> newInstance(IDNumName id, SimpleLongValue v) {
			return new SortedTreeNode(id, v);
		}
		
		@Override
		public Map<IDNumName, TreeNode<IDNumName, SimpleLongValue>> getChildMap(){
			return new TreeMap<IDNumName, TreeNode<IDNumName, SimpleLongValue>>();
		}
	}
	
	public static void main(String[] args) throws Exception{
		LongValueTreeTest t = new LongValueTreeTest();
		
		LongValueDataTree<IDNumName> sdt =
				new LongValueDataTree<IDNumName>(Arrays.asList("Division", "Gender", "Category", "Door"), 
						false, 
						t.new SortedTreeNode(null, null));
		
		sdt.build(LongValueTreeTest.getOverviewItems(), new TreeAdapter<OverviewItem, IDNumName, SimpleLongValue>() {

			@Override
			public List<IDNumName> getHierachy(OverviewItem k) {
				return Arrays.asList(new IDNumName(k.prodattrList.get(0), k.prodattrList.get(0), ""), 
						new IDNumName(k.prodattrList.get(1), k.prodattrList.get(1), ""),
						new IDNumName(k.prodattrList.get(2), k.prodattrList.get(2), ""),
						new IDNumName(k.storeNum, k.storeNum, ""));
			}

			@Override
			public SimpleLongValue getValue(OverviewItem k) {
				return new SimpleLongValue(k.bODUnits);
			}
			
		});
		
		System.out.println(sdt.toString());
	
		HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();
		Set<String> set = new HashSet<String>();
		set.add("S001");
		set.add("S002");
		set.add("S016");
		set.add("S015");
		set.add("S009");
		map.put("Door", set);
//
		LongValueDataTree<IDNumName> fsdt = (LongValueDataTree<IDNumName>) sdt.filter(map, new IDNumName.NumberMatcher());
		
		System.out.println(fsdt.toString());
		
		fsdt.breakDown(new SimpleLongValue(100L));
		
		System.out.println(fsdt.toString());
		System.out.println(sdt.toString());
		
//		
//		DataTree<String, SimpleLongValue> nstd = sdt.convert(Arrays.asList("Category", "Door"));
//
//		System.out.println(nstd.toString());
		
//		DataTree<IDNumName, SimpleLongValue> nstd = sdt.subTree(
//				Arrays.asList(new IDNumName("Tops","222","111"), 
//				new IDNumName("Men's", "222", "111"),
//				new IDNumName("PoloShirts", "222", "111")) );
//		
//		System.out.println(nstd.toString());
		
//		sdt.breakDown(new SimpleLongValue(100));
//		System.out.println(sdt.toString());
//		
//		List<OverviewItem> list =
//				sdt.getFlatData(new FlatDataAdapter<OverviewItem, String, SimpleLongValue>() {
//
//				@Override
//				public OverviewItem getData(List<String> hierarchys, List<String> ids, SimpleLongValue value) {
//					int index = hierarchys.indexOf("Door");
//					String door = ids.get(index);
//					ids.remove(index);
//					OverviewItem item =new OverviewItem();
//					item.storeNum = door;
//					item.prodattrList = ids;
//					item.bODUnits = value.value;
//					return item;
//				}
//			
//		});
//		
//		System.out.println( Arrays.toString(list.toArray()) );
	}
	
	
    public static List<OverviewItem> getOverviewItems(){
    	List<OverviewItem> list = new ArrayList<OverviewItem>();
    	list.add(new OverviewItem("S001", "Spectrum", "Tops", "Men's", "T-Shirts", 699L, 54L, 34L, 115L, 325L, 166L, 274L));
    	list.add(new OverviewItem("S007", "Hollywood", "Tops", "Men's", "T-Shirts", 1215L, 0L, 451L, 21L, 612L, 432L, 150L));
    	list.add(new OverviewItem("S009", "Tampa", "Tops", "Men's", "T-Shirts", 306L, 30L, 78L, 50L, 194L, 67L, 217L));
    	list.add(new OverviewItem("S010", "Orlando", "Tops", "Men's", "T-Shirts", 130L, 68L, 28L, 374L, 16L, 136L, 285L));
    	list.add(new OverviewItem("S011", "SanAntonio", "Tops", "Men's", "T-Shirts", 80L, 26L, 142L, 429L, 6L, 551L, 77L));
    	list.add(new OverviewItem("S012", "Boca", "Tops", "Men's", "T-Shirts", 610L, 57L, 296L, 143L, 369L, 416L, 300L));
    	list.add(new OverviewItem("S015", "Dadeland", "Tops", "Men's", "T-Shirts", 223L, 11L, 424L, 365L, 72L, 598L, 377L));
    	list.add(new OverviewItem("S016", "Houston", "Tops", "Men's", "T-Shirts", 840L, 54L, 285L, 80L, 480L, 463L, 205L));
    	list.add(new OverviewItem("S018", "StoneBriar", "Tops", "Men's", "T-Shirts", 380L, 55L, 18L, 219L, 103L, 51L, 481L));
    	list.add(new OverviewItem("S021", "BartonCreek", "Tops", "Men's", "T-Shirts", 215L, 28L, 66L, 448L, 87L, 451L, 297L));
    	list.add(new OverviewItem("S023", "Woodfield", "Tops", "Men's", "T-Shirts", 10L, 23L, 358L, 20L, 0L, 305L, 216L));
    	list.add(new OverviewItem("S001", "Spectrum", "Tops", "Men's", "PoloShirts", 384L, 30L, 120L, 349L, 455L, 308L, 321L));
    	list.add(new OverviewItem("S007", "Hollywood", "Tops", "Men's", "PoloShirts", 96L, 81L, 84L, 99L, 132L, 276L, 15L));
    	list.add(new OverviewItem("S009", "Tampa", "Tops", "Men's", "PoloShirts", 412L, 50L, 50L, 347L, 622L, 118L, 344L));
    	list.add(new OverviewItem("S010", "Orlando", "Tops", "Men's", "PoloShirts", 263L, 65L, 67L, 161L, 202L, 171L, 231L));
    	list.add(new OverviewItem("S011", "SanAntonio", "Tops", "Men's", "PoloShirts", 77L, 74L, 76L, 423L, 8L, 364L, 199L));
    	list.add(new OverviewItem("S012", "Boca", "Tops", "Men's", "PoloShirts", 371L, 14L, 273L, 18L, 355L, 122L, 341L));
    	list.add(new OverviewItem("S015", "Dadeland", "Tops", "Men's", "PoloShirts", 13L, 62L, 192L, 170L, 11L, 334L, 61L));
    	list.add(new OverviewItem("S016", "Houston", "Tops", "Men's", "PoloShirts", 302L, 96L, 211L, 296L, 108L, 534L, 310L));
    	list.add(new OverviewItem("S018", "StoneBriar", "Tops", "Men's", "PoloShirts", 263L, 83L, 70L, 41L, 217L, 1L, 290L));
    	list.add(new OverviewItem("S021", "BartonCreek", "Tops", "Men's", "PoloShirts", 236L, 29L, 42L, 219L, 72L, 229L, 239L));
    	list.add(new OverviewItem("S023", "Woodfield", "Tops", "Men's", "PoloShirts", 240L, 23L, 44L, 297L, 238L, 332L, 52L));
    	return list;
    }
    
    public static class OverviewItem{
    	public String storeNum;
    	public String storeDescription;
    	public List<String> prodattrList;
		public long bODUnits;
    	public long inTransitUnits;
    	public long allocationUnits;
    	public long transferInUnits;
    	public long transferOutUnits;
    	public long projectedSalesUnits;
    	public long projectedCapacityUnits;
    	
    	public OverviewItem(){
    		super();
    	}
    	
		public OverviewItem(String storeNum,
							String storeDescription,
							String department,
							String gender,
							String category,
							long bODUnits,
							long inTransitUnits,
							long allocationUnits,
							long transferInUnits,
							long transferOutUnits,
							long projectedSalesUnits,
							long projectedCapacityUnits) {
			super();
			this.storeNum = storeNum;
			this.storeDescription = storeDescription;
			this.prodattrList = Arrays.asList(department, gender, category);
			this.bODUnits = bODUnits;
			this.inTransitUnits = inTransitUnits;
			this.allocationUnits = allocationUnits;
			this.transferInUnits = transferInUnits;
			this.transferOutUnits = transferOutUnits;
			this.projectedSalesUnits = projectedSalesUnits;
			this.projectedCapacityUnits = projectedCapacityUnits;
		}
		
		@Override
		public String toString(){
			return this.storeNum +"  " + Arrays.toString( this.prodattrList.toArray() ) + "  " + this.bODUnits;
		}
    }
}
