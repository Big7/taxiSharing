package TShare;

import java.util.Date;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import core.Rider;
import core.RoadGrid;

public class RiderEntry {
	Rider r;
	public Coordinate location;
	Date maxtime;//max pickup or deliver
	public RiderEntry(Coordinate l,Rider r){
		this.location = l;
		this.r = r;
		if(location.equals(r.getOrigin())){
			maxtime = r.getMaxPickup();
		}else if(location.equals(r.getDestination())){
			maxtime = r.getMaxDeliver();
		}else{
			System.out.println("RiderEntry: neither equal origin nor destination");
		}
	}
	
	public RiderEntry( Coordinate l){
		this.location = l;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	

}
