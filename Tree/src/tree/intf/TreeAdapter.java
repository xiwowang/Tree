package tree.intf;

import java.util.List;

public interface TreeAdapter<K, ID, V> {
	
	public List<ID> getHierachy(K k);
	
	public V getValue(K k);
}
