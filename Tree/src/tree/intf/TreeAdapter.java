
package tree.intf;

/**
 * @version Change History:
 * @version <1>    03/03/15 MQ  First Written for Sub-EN #15800: based on the updated WF. (Mizzle Qiu)
 */
import java.util.List;

public interface TreeAdapter<K, ID, V> {
	
	public List<ID> getHierarchy(K k);
	
	public V getValue(K k);
}
