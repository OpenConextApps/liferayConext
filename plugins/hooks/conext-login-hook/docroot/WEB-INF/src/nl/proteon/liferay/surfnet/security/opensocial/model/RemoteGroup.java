package nl.proteon.liferay.surfnet.security.opensocial.model;




/**
 * XML Bean class. A remote group representation.
 */

public class RemoteGroup{

	private String id;
	private String title;
	private String description;





	public RemoteGroup() {
	}





	public RemoteGroup(String id, String name, String description) {
		super();
		this.id = id;
		this.title = name;
		this.description = description;
	}





	public String getId(){
		return id;
	}





	public void setId(String id){
		this.id = id;
	}





	public String getTitle(){
		return title;
	}





	public void setTitle(String title){
		this.title = title;
	}





	public String getDescription(){
		return description;
	}





	public void setDescription(String description){
		this.description = description;
	}





	@Override
	public String toString(){
		return "RemoteGroup [id=" + id + ", title=" + title + ", description="
				+ description + "]";
	}
}
