package tree.id;

import tree.intf.Matcher;
import tree.intf.MultipleID;

public class IDNumName implements MultipleID<IDNumName> {

	private String id;
	private String number;
	private String name;

	public IDNumName(String id, String number, String name) {
		super();
		this.id = id;
		this.number = number;
		this.name = name;
	}
	
	@Override
	public int compareTo(IDNumName t) {
		return this.id.compareTo(t.id);
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof IDNumName){
			return this.id.equals( ((IDNumName)o).id );
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.id.hashCode();
	}
	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static class IDMatcher implements Matcher<IDNumName, String>{

		@Override
		public boolean match(IDNumName o, String p) {
			if(o!=null && o.id!=null){
				return o.id.equals(p);
			}else{
				return p==null;
			}
		}
		
	}
	
	public static class NumberMatcher implements Matcher<IDNumName, String>{

		@Override
		public boolean match(IDNumName o, String p) {
			if(o!=null && o.number!=null){
				return o.number.equals(p);
			}else{
				return p==null;
			}
		}
		
	}
	
	public static class NameMatcher implements Matcher<IDNumName, String>{

		@Override
		public boolean match(IDNumName o, String p) {
			if(o!=null && o.name!=null){
				return o.name.equals(p);
			}else{
				return p==null;
			}
		}
		
	}
	
	@Override
	public String toString(){
		return this.id+" "+this.number+" "+this.name;
	}
}
