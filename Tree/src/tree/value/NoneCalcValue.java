
package tree.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tree.intf.Collectable;

public class NoneCalcValue<K> implements Collectable<NoneCalcValue<K>>{

	public List<K> list;

	public NoneCalcValue(List<K> list){
		this.list = new ArrayList<K>();
		this.list.addAll(list);
	}
	
	@Override
	public NoneCalcValue<K> clone(){
		return new NoneCalcValue<K>(this.list);
	}
	
	@Override
	public NoneCalcValue<K> deepClone() {
		return new NoneCalcValue<K>(this.list);
	}

	@Override
	public void merge(NoneCalcValue<K> v) throws Exception {
		if(v==null || v.list==null){
			return;
		}
		if(this.list==null){
			this.list = new ArrayList<K>();
		}
		this.list.addAll ( v.list );
	}

	@Override
	public void minus(NoneCalcValue<K> v) throws Exception {
		if(this.list==null || v==null || v.list==null){
			return;
		}
		this.list.retainAll ( v.list );
	}

	@Override
	public NoneCalcValue<K> getEmptyInstance() {
		return new NoneCalcValue<K>(new ArrayList<K>());
	}
	
	@Override
	public String toString(){
		return this.list==null?null:Arrays.toString(this.list.toArray());
	}
}
