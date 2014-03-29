package process;

import java.io.IOException;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import net.sf.json.JSONObject;

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