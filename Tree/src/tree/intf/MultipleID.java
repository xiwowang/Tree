package tree.intf;

public interface MultipleID<T> extends Comparable<T> {

	public int compareTo(T t);
	
	public boolean equals(Object o);
	
	public int hashCode();
}
