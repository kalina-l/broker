package model;
import java.lang.Comparable;

/**
 * Abstract BaseEntity super class for all persitance entities. 
 * @author Master Programming Group 6
 *
 */
public abstract class BaseEntity implements Comparable<BaseEntity>{
	
	private long identity;
	private int version;
	private long creationTimeStamp;
	
	public BaseEntity(){

		this.identity = 0;
		this.version = 1;
		this.creationTimeStamp = System.currentTimeMillis();
		
	}
	
	@Override
	public int compareTo(BaseEntity other){
		return Long.compare(this.getIdentity(), other.getIdentity());	
	}


	/**
	 * @return identity number of the entity
	 */
	public long getIdentity() {
		return identity;
	}

	/**
	 * @return version number of the entity
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version version number of the entity
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return creation timestamp of the entity
	 */
	public long getCreationTimeStamp() {
		return creationTimeStamp;
	}
	
}
