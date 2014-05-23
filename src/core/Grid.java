package core;

import java.io.Serializable;

public class Grid implements Serializable {

	int index;//end grid
	double distance;
	public double time=0.0;
	
	public Grid(double t,double d){
		this.setTime(t);
		this.setDistance(d);
	}
	public int getIndex(){
		return index;
	}
	public void setIndex(int index){
		this.index = index;
	}
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
	public String toString(){
		return index+":"+distance+","+time;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
