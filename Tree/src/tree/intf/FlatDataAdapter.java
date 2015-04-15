
package tree.intf;

import java.util.List;

public interface FlatDataAdapter<K, ID, V> {
	public K getData(List<String> hierarchys, List<ID> ids, V value);
}
