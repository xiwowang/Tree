package tree.intf;


public interface Collectable<V> {
	
	public V clone();
	
	public V deepClone();
	
	public void merge(V v) throws Exception;
	
	public void minus(V v) throws Exception;
	
	public V getEmptyInstance();
}
