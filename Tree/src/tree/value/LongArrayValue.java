
package tree.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import tree.intf.Collectable;

/**
 * @version Change History:
 * @version <1>    03/03/15 MQ  First Written for Sub-EN #15800: based on the updated WF. (Mizzle Qiu)
 */
public class LongArrayValue implements Collectable<LongArrayValue>{
	
	public long[] value;

	public LongArrayValue(long[] value) {
		this.value = value;
	}
	
	public LongArrayValue(int size) {
		this.value = new long[size];
	}

	@Override
	public LongArrayValue clone() {
		return new LongArrayValue(this.value);
	}

	@Override
	public LongArrayValue deepClone() {
		int size = this.value.length;
		long[] newValue = new long[size];
		System.arraycopy(this.value, 0, newValue, 0, size);
		return new LongArrayValue(newValue);
	}

	// May need to override merge and minus to merge the average/percentage etc. data.
	@Override
	public void merge(LongArrayValue v) throws Exception {
		if(v != null && v.value != null){
			if(this.value.length != v.value.length){
				throw new Exception("Can't merge array when length not the same");
			}
			int size = this.value.length;
			for(int i=0; i<size; i++){
				this.value[i] += v.value[i];
			}
		}
	}

	@Override
	public void minus(LongArrayValue v) throws Exception {
		if(v != null && v.value != null){
			if(this.value.length != v.value.length){
				throw new Exception("Can't minus array when length not the same");
			}
			int size = this.value.length;
			for(int i=0; i<size; i++){
				this.value[i] -= v.value[i];
			}
		}
		
	}

	@Override
	public LongArrayValue getEmptyInstance() {
		return new LongArrayValue(this.value.length);
	}

	@Override
	public String toString() {
		return Arrays.toString(this.value);
	}
	
	public void breakDown(LongArrayValue src, LongArrayValue des, int[] indexes, Adjustor adj, int count) throws Exception {
		for(int i=0; i<indexes.length; i++){
			int index = indexes[i];
			if(src.value[index]!=0){
				double val = this.value[index] * des.value[index] / (double) src.value[index];
				this.value[index] = Math.round(val);
				adj.register(val-this.value[index], i, this);
			}else{
				double val = des.value[index] / (double) count;
				this.value[index] = Math.round(val);
				adj.register(val-this.value[index], i, this);
			}
		}
	}
	
	public class Adjustor{
		private int[] indexes;
		private List<TreeMap<Double, List<LongArrayValue>>> adjMaps = new ArrayList<TreeMap<Double, List<LongArrayValue>>>();
		private long[] curSum;
		
		public Adjustor(int[] indexes){
			super();
			this.indexes = indexes;
			this.curSum = new long[indexes.length];
			for(int i=0; i<indexes.length; i++){
				this.adjMaps.add(new TreeMap<Double, List<LongArrayValue>>());
				this.curSum[i] = 0;
			}
		}
		
		public void register(double d, int i, LongArrayValue ar){
			int index = this.indexes[i];
			if(this.adjMaps.get(i).containsKey(d)){
				this.adjMaps.get(i).get(d).add(ar);
			}else{
				ArrayList<LongArrayValue> l = new ArrayList<LongArrayValue>();
				l.add(ar);
				this.adjMaps.get(i).put(d, l);
			}
			this.curSum[i] += ar.value[index];
		}
		
		public void adjust(LongArrayValue des){
			for(int i=0; i<this.indexes.length; i++){
				int index = this.indexes[i];
				long diff = des.value[index] - this.curSum[i];
				if(diff > 0){
					double lastkey = 1;
					while(diff>0){
						lastkey = this.adjMaps.get(i).lowerKey(lastkey);
						List<LongArrayValue> list = this.adjMaps.get(i).get(lastkey);
						for(LongArrayValue slv : list){
							while(diff>0){
								slv.value[index]+=1;
								diff--;
							}
						}
					}
				}else{
					double firstkey = -1;
					while(diff<0){
						firstkey = this.adjMaps.get(i).higherKey(firstkey);
						List<LongArrayValue> list = this.adjMaps.get(i).get(firstkey);
						for(LongArrayValue slv : list){
							while(diff<0){
								slv.value[index]-=1;
								diff++;
							}
						}
					}
				}
			}
		}
	}
}
