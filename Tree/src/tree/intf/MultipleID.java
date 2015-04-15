
package tree.intf;

public interface MultipleID<T> extends Comparable<T> {

	@Override
	public int compareTo(T t);
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
}
