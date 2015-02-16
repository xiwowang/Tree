package tree.base;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import tree.intf.Collectable;

public class SimpleLongValue implements Collectable<SimpleLongValue>{

	public long value;
	
	public SimpleLongValue(long value) {
		super();
		this.value = value;
	}
	
	@Override
	public SimpleLongValue clone() {
		return new SimpleLongValue(this.value);
	}

	@Override
	public SimpleLongValue deepClone() {
		return new SimpleLongValue(this.value);
	}

	@Override
	public void merge(SimpleLongValue v) throws Exception {
		if(v != null){
			this.value += v.value;
		}
	}

	@Override
	public void minus(SimpleLongValue v) throws Exception {
		if(v != null){
			this.value -= v.value;
		}
		
	}

	@Override
	public SimpleLongValue getEmptyInstance() {
		return new SimpleLongValue(0);
	}

	@Override
	public String toString(){
		return String.valueOf(this.value);
	}

	public void breakDown(SimpleLongValue src, SimpleLongValue des, Adjustor adj) throws Exception {
		double val = this.value * des.value / (double) src.value;
		this.value = Math.round(val);
		adj.register(val-this.value, this);
	}
	
	public class Adjustor{
		private TreeMap<Double, List<SimpleLongValue>> adjMap = new TreeMap<Double, List<SimpleLongValue>>();
		private long curSum = 0;
		
		public Adjustor(){
			super();
		}
		
		public void register(double d, SimpleLongValue s){
			if(this.adjMap.containsKey(d)){
				this.adjMap.get(d).add(s);
			}else{
				ArrayList<SimpleLongValue> l = new ArrayList<SimpleLongValue>();
				l.add(s);
				this.adjMap.put(d, l);
			}
			this.curSum += s.value;
		}
		
		public void adjust(SimpleLongValue des){
			long diff = des.value - this.curSum;
			if(diff > 0){
				double lastkey = 1;
				while(diff>0){
					lastkey = this.adjMap.lowerKey(lastkey);
					List<SimpleLongValue> list = this.adjMap.get(lastkey);
					for(SimpleLongValue slv : list){
						while(diff>0){
							slv.value+=1;
							diff--;
						}
					}
				}
			}else{
				double firstkey = -1;
				while(diff<0){
					firstkey = this.adjMap.lowerKey(firstkey);
					List<SimpleLongValue> list = this.adjMap.get(firstkey);
					for(SimpleLongValue slv : list){
						while(diff<0){
							slv.value-=1;
							diff++;
						}
					}
				}
			}
		}
	}
	
	
}
