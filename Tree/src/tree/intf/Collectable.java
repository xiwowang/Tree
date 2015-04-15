
package tree.intf;

/**
 * @version Change History:
 * @version <1>    03/03/15 MQ  First Written for Sub-EN #15800: based on the updated WF. (Mizzle Qiu)
 */
public interface Collectable<V> {
	
	public V clone();
	
	public V deepClone();
	
	public void merge(V v) throws Exception;
	
	public void minus(V v) throws Exception;
	
	public V getEmptyInstance();
}
