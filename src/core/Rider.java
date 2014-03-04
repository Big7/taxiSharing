package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rider {
	String RiderID;
	int State;//人数
	Date MomentTime;
	
	double OriginLng;
	double OriginLat;//纬度x
	String Origin;
	
	double DestinationLng;
	double DestinationLat;
	String Destination;
	
	public Rider(String[] texts) throws ParseException {
		
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		MomentTime = df.parse(texts[0]);
		OriginLng =Double.parseDouble(texts[1]);
		OriginLat =Double.parseDouble(texts[2]);
		DestinationLng =Double.parseDouble(texts[3]);
		DestinationLat =Double.parseDouble(texts[4]);
		State=1;//默认为1
	}

	public String getRiderID(){
		return RiderID;
	}
	
	public void setRiderID(String riderID) {
		RiderID = riderID;
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

	public void setMomentTime(Date momentTime) {
		MomentTime = momentTime;
	}

	
	public double getDestinationLng() {
		return DestinationLng;
	}

	public void setDestinationLng(double destinationLng) {
		DestinationLng = destinationLng;
	}

	public double getDestinationLat() {
		return DestinationLat;
	}

	public void setDestinationLat(double destinationLat) {
		DestinationLat = destinationLat;
	}

	public String getOrigin() {
		return Origin;
	}

	public void setOrigin(String origin) {
		Origin = origin;
	}

	public String getDestination() {
		return Destination;
	}

	public void setDestination(String destination) {
		Destination = destination;
	}

	public boolean range90(Taxi taxi) {
		double y = DestinationLat-OriginLat;
		double x = DestinationLng-OriginLng;
		double k1 = Math.atan2(y,x);
		y = taxi.longitude-OriginLng;
		x = taxi.latitude-OriginLng;
		double k2 = Math.atan2(y, x);
		double range=(k1-k2);
		if(range<Math.PI/2){
			System.out.print(range);
			return true;
		}
		else return false;
	}
	public static void main(String[] args) throws ParseException {
		String[] texts1={"04:00:08","0","0","1","1"};
		Rider rider=new Rider(texts1);
		String[] texts2={"431156","4","2","20121130001937","1","0","10","184","1"};
		Taxi taxi=new Taxi(texts2);
		rider.range90(taxi);
	}
	
}
