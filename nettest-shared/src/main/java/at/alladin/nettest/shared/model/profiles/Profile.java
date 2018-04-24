package at.alladin.nettest.shared.model.profiles;

import java.util.Map;

import com.google.gson.annotations.Expose;
import at.alladin.nettest.shared.CouchDbEntity;
import at.alladin.nettest.shared.annotation.NotEmpty;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class Profile extends CouchDbEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@NotEmpty
	@Expose
	private String name;

	/**
	 * 
	 */
	@Expose
	private boolean active;
	
	/**
	 * 
	 */
	@NotEmpty
	@Expose
	private Map<Object, Object> selector;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Object, Object> getSelector() {
		return selector;
	}

	public void setSelector(Map<Object, Object> selector) {
		this.selector = selector;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Profile [name=" + name + ", active=" + active + ", selector=" + selector + "]";
	}
}
