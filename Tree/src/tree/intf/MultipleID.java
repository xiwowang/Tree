package tree.intf;

public interface MultipleID extends Comparable<MultipleID> {

	public int compareTo();
	
	public boolean equals();
	
	public int hashCode();
}
