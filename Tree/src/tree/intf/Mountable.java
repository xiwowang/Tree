
package tree.intf;

/**
 * @version Change History:
 * @version <1>    03/03/15 MQ  First Written for Sub-EN #15800: based on the updated WF. (Mizzle Qiu)
 */
public interface Mountable<T> extends Cloneable{

	public T clone();
	
	public T deepClone();
}
