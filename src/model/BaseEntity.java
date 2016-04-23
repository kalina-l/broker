package model;
import java.lang.Comparable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.constraints.Size;

/**
 * Abstract BaseEntity super class for all persitance entities. 
 * @author Master Programming Group 6
 *
 */
public abstract class BaseEntity implements Comparable<BaseEntity>{

	private static final AtomicInteger count = new AtomicInteger(0);
	
	@Size(min=1, max=63)
	private long identity;
	private int version;
	private long creationTimeStamp;
	
	public BaseEntity(){

		this.identity = count.incrementAndGet();
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
