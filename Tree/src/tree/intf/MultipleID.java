
package tree.intf;

/**
 * @version Change History:
 * @version <1>    03/03/15 MQ  First Written for Sub-EN #15800: based on the updated WF. (Mizzle Qiu)
 */
public interface MultipleID<T> extends Comparable<T> {

	@Override
	public int compareTo(T t);
	
	@Override
	public boolean equals(Object o);
	
	@Override
	public int hashCode();
}
