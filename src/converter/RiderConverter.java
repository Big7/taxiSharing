package converter;

import java.util.Map;

import core.Rider;

import ognl.DefaultTypeConverter;

public class RiderConverter extends DefaultTypeConverter{
	@Override
	public Object convertValue(Map context, Object value, Class toType){
		if(toType==Rider.class){
			String[] params = (String[])value;
			Rider rider = new Rider();
			String[] values = params[0].split(",");
//			rider.setOrigin(values[0]);
//			rider.setOrigin(values[1]);
			return rider;
			
		}else if(toType==String.class){
			Rider rider = (Rider) value;
//			String Origin=rider.getOrigin();
//			String Destination=rider.getDestination();
//			return Origin+","+Destination;
		
		}
		return null;
	}

}
