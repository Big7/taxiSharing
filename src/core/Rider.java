package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import TShare.RiderEntry;

import com.vividsolutions.jts.geom.Coordinate;

public class Rider {
	String RiderID;
	int State;//����
	Date MomentTime;
	Date MaxPickup;
	Date MaxDeliver;
	
	double OriginLng;//x
	double OriginLat;//y纬度39
	
	Coordinate Origin;
	
	double DestinationLng;
	double DestinationLat;
	Coordinate Destination;
	Taxi Driver;
	
	public Date getMaxPickup() {
		return MaxPickup;
	}

	public void setMaxPickup(long maxPickup) {
		MaxPickup.setTime( maxPickup);
	}

	public Date getMaxDeliver() {
		return MaxDeliver;
	}

	public void setMaxDeliver(long maxDeliver) {
		MaxDeliver.setTime(maxDeliver);
	}

	public Rider(String[] texts) throws ParseException {
		
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		MomentTime = df.parse(texts[0]);
		MaxPickup.setTime(MomentTime.getTime()+5*60*1000000);
		//MaxDeliver.setTime(MomentTime.getTime()+rg.grids[][])
		
		OriginLat =Double.parseDouble(texts[1]);//y
		OriginLng =Double.parseDouble(texts[2]);
		DestinationLat =Double.parseDouble(texts[3]);
		DestinationLng =Double.parseDouble(texts[4]);
		this.setOrigin(new Coordinate(OriginLng,OriginLat));
		this.setDestination(new Coordinate(DestinationLng,DestinationLat));
		State=1;//Ĭ��Ϊ1
		
	}

	public Rider() {
		// TODO Auto-generated constructor stub
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



	public Coordinate getOrigin() {
		return Origin;
	}

	public void setOrigin(Coordinate origin) {
		Origin = origin;
	}

	public Coordinate getDestination() {
		return Destination;
	}

	public void setDestination(Coordinate destination) {
		Destination = destination;
	}

	public double getOriginLat() {
		return OriginLat;
	}

	public void setOriginLat(double originLat) {
		OriginLat = originLat;
	}

	public double getOriginLng() {
		return OriginLng;
	}

	public void setOriginLng(double originLng) {
		OriginLng = originLng;
	}

	//�ж��Ƿ���⳵��˿ͷ���һ�£�180�ȷ�Χ֮�ڣ�
	public boolean range180(Taxi taxi) {
		double y = DestinationLng-OriginLng;
		double x = DestinationLat-OriginLat;
		double k1 = Math.atan2(y,x);
		//System.out.println(k1);
		y = taxi.longitude-OriginLng;
		x = taxi.latitude-OriginLat;
		double k2 = Math.atan2(y, x);
		//System.out.println(k2);
		double range=(k1-k2);
		if(-Math.PI/2<range && range<Math.PI/2){
			System.out.print(range);
			return true;
		}
		else return false;
	}
	
	@Override
	public String toString(){
		return "("+this.getOriginLat()+","+this.getOriginLng()+")"
	+"("+this.getDestinationLat()+","+this.getDestinationLng()+")";
	}
	public static void main(String[] args) throws ParseException {
		String[] texts1={"04:00:08","0","0","0","1"};
		Rider rider=new Rider(texts1);
		String[] texts2={"431156","4","2","20121130001937","1","1","10","184","1"};
		Taxi taxi=new Taxi(texts2);
		rider.range180(taxi);
	}

	public long arriveOrigin(RoadGrid rg, List<RiderEntry> subList) {
		long time=0;
		Coordinate pre = subList.get(0).location;
		for(RiderEntry re:subList){
			time+=rg.getTime2(pre, re.location);
			pre = re.location;
		}
		time+=rg.getTime2(pre, this.Origin);
		return time*1000000;//milliseconds
	}

	public long arriveDestination(RoadGrid rg, List<RiderEntry> subList) {
		long time=0;
		Coordinate pre = subList.get(0).location;
		for(RiderEntry re:subList){
			time+=rg.getTime2(pre, re.location);
			pre = re.location;
		}
		time+=rg.getTime2(pre, this.Destination);
		return time*1000000;//milliseconds
	}
	
}
