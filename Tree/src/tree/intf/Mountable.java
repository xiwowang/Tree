package tree.intf;

public interface Mountable<T> extends Cloneable{

	public T clone();
	
	public T deepClone();
}
