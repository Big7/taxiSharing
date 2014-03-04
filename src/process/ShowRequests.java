package process;

import java.util.List;

import core.ScheduleCenter;
import core.Rider;
import com.opensymphony.xwork2.ActionSupport;

public class ShowRequests extends ActionSupport {

	private List<Rider> requests;
	
	public List<Rider> getRequests() {
		return requests;
	}
	/**
	 * @return
	 */
	public String execute() {
		// TODO Auto-generated method stub
		ScheduleCenter.Center.getRequests();
		return SUCCESS;
	}
}