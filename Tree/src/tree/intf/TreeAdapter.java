
package tree.intf;

import java.util.List;

public interface TreeAdapter<K, ID, V> {
	
	public List<ID> getHierarchy(K k);
	
	public V getValue(K k);
}
