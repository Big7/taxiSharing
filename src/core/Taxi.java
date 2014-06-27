package core;

import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat; 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import TShare.RiderEntry;

import com.vividsolutions.jts.geom.Coordinate;


public class Taxi {
	String TaxiID;
	int  Trigger;
	int  State;
	Date MomentTime;
	double longitude;
	double latitude;
	int GPSpeed;
	int GPSangle;
	int GPState;
	public List<Rider> Passengers = new ArrayList<Rider>();//onboard
	public List<RiderEntry> Schedule = new ArrayList<RiderEntry>();
	public String getTaxiID() {
		return TaxiID;
	}

	public void setTaxiID(String taxiID) {
		TaxiID = taxiID;
	}

public Coordinate getLocation(){
	return new Coordinate(longitude,latitude);
}



	public int getTrigger() {
		return Trigger;
	}





	public void setTrigger(int trigger) {
		Trigger = trigger;
	}





	public int getState() {
		return State;
	}





	public void setState(int state) {
		State = state;
	}





	public Date getMomentTime() {
		return MomentTime;
	}





	public void setMomentTime(Date time) {
		MomentTime = time;
	}





	public double getLongitude() {
		return longitude;
	}





	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}





	public double getLatitude() {
		return latitude;
	}





	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}





	public int getGPSpeed() {
		return GPSpeed;
	}





	public void setGPSpeed(int gPSpeed) {
		GPSpeed = gPSpeed;
	}





	public int getGPSangle() {
		return GPSangle;
	}





	public void setGPSangle(int gPSangle) {
		GPSangle = gPSangle;
	}





	public int getGPState() {
		return GPState;
	}





	public void setGPState(int gPState) {
		GPState = gPState;
	}



	
    /**
	 * ���ַ��������taxi gps point
	 * @param texts һ���ַ�
     * @throws ParseException 
	 */
	public Taxi(String[] texts) throws ParseException{
		super();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		
	    TaxiID=texts[0];
	    Trigger = Integer.parseInt(texts[1]);
	    State = Integer.parseInt(texts[2]);
	    MomentTime = df.parse(texts[3]);
	    longitude = Double.parseDouble(texts[4]);
	    latitude = Double.parseDouble(texts[5]);
	    GPSpeed = Integer.parseInt(texts[6]);
	    GPSangle = Integer.parseInt(texts[7]);
	    GPState = Integer.parseInt(texts[8]);
	    
	}





	@Override
	public String toString() {
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss"); 
		String time=df.format(MomentTime);
		return TaxiID+","+Trigger+","+State+","+time+","+longitude+","+latitude+","+GPSpeed+","+GPSangle+","+GPState;
	}
	

}
