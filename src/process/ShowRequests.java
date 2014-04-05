package process;

import java.util.List;

import core.ScheduleCenter;
import core.Rider;
import core.Taxi;

import com.opensymphony.xwork2.ActionSupport;

public class ShowRequests extends ActionSupport {

	private List<Rider> requests;
	private List<Taxi> taxis;
	
	public void setRequests(List<Rider> requests) {
		this.requests = requests;
	}
	public List<Rider> getRequests() {
		return requests;
	}
	/**
	 * @return
	 */
	@Override
	public String execute() {
		// TODO Auto-generated method stub
		requests = ScheduleCenter.Center.getRequests();
		taxis = ScheduleCenter.Center.getRunTaxi();
		
//		for(Rider rider:requests){
//			System.out.println("Rider: "+rider.toString());
//		}
		System.out.println(requests.size()+"//////"+taxis.size());
		return SUCCESS;
	}
}