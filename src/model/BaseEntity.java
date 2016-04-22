package model;
import java.lang.Comparable;

import javax.validation.constraints.Size;

public abstract class BaseEntity implements Comparable<BaseEntity>{

	@Size(min=1, max=63)
	private long identity;
	
	
	private int version;
	private long creationTimeStamp;
	
	public BaseEntity(){

		
		version = 1;
		creationTimeStamp = System.currentTimeMillis();
		
	}
	
	@Override
	public int compareTo(BaseEntity other){
		//TODO implement functionality
		return 0;
	}


	
	/**
	 *********** Getter and Setter ************
	 */

	
	/**
	 * @return identity number of the entity
	 */
	public long getIdentity() {
		return identity;
	}



	/**
	 * @param identity identity number of the entity
	 */
	public void setIdentity(long identity) {
		this.identity = identity;
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



	/**
	 * @param creationTimeStamp creation timestamp of the entity
	 */
	public void setCreationTimeStamp(long creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}
	
	
	
	
	
}
