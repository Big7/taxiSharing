package core;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Node;

import com.vividsolutions.jts.geom.Coordinate;

public class RoadGrid {
	RoadNetwork RoadNetwork;
	Coordinate LeftUp = new Coordinate();
	Coordinate LeftDown = new Coordinate();//x,y	lng,lat
	Coordinate RightDown = new Coordinate();
	Coordinate RightUp = new Coordinate();
	int width,length;
	int gridWidth,gridLength;
	int yGrids,xGrids;
	double AvgSpeed;
	//ItemVisitor Grids = new ArrayListVisitor();
	
	RoadGrid() throws Exception{
		RoadNetwork=new RoadNetwork("C:\\Users\\dxy\\road_network\\road_network.shp");
		
		LeftUp = new Coordinate(116.215,40.030);//(40.220, 116.095);
		LeftDown = new Coordinate(116.215,39.780);//(39.715, 116.095);
		RightDown = new Coordinate(116.545,39.780);//(39.715, 116.708);
		RightUp = new Coordinate(116.545,40.030);//(40.220, 116.708);
		//2-dimensional Euclidean distance，向上取整25000*33000 m*m
		setWidth((int) Math.ceil(100000*LeftUp.distance(LeftDown)));
		setLength((int) Math.ceil(100000*LeftDown.distance(RightDown)));
		//每个格子的长宽 m
		setGridWidth(500);
		setGridLength(500);
		//长和宽的格子个数
		setyGrids((int) (getWidth()/getGridWidth()));
		setxGrids((int)(getLength()/getGridLength()));
		setAvgSpeed(600);//M/s
	}
	
	

	public RoadNetwork getRoadNetwork() {
		return RoadNetwork;
	}



	public void setRoadNetwork(RoadNetwork roadNetwork) {
		RoadNetwork = roadNetwork;
	}



	public int getyGrids() {
		return yGrids;
	}



	public void setyGrids(int yGrids) {
		this.yGrids = yGrids;
	}



	public int getxGrids() {
		return xGrids;
	}



	public void setxGrids(int xGrids) {
		this.xGrids = xGrids;
	}



	public double getGridWidth() {
		return gridWidth;
	}



	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}



	public double getGridLength() {
		return gridLength;
	}



	public void setGridLength(int gridLength) {
		this.gridLength = gridLength;
	}



	public double getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	public double getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}

	public double getAvgSpeed() {
		return AvgSpeed;
	}



	public void setAvgSpeed(double avgSpeed) {
		AvgSpeed = avgSpeed;
	}



	//返回经纬度所在的格子
	public int getGrid(Coordinate location){
		double x = (location.x-LeftDown.x)/LeftDown.distance(RightDown) * this.getxGrids();
		double y = (location.y-LeftDown.y)/LeftDown.distance(LeftUp) * this.getyGrids();
		System.out.print(x+" "+Math.floor(x)+"    ");
		System.out.println(y+" "+Math.floor(y));
		return (int) (this.getyGrids()*Math.floor(x)+Math.floor(y));
	}
	
	public Coordinate getCenter(int Grid){
		return new Coordinate(LeftDown.x+( Grid/getyGrids() *this.getGridWidth() + (Grid % getyGrids()!=0? getGridLength()/2 : 0))*0.00001,
								LeftDown.y+(Grid % getyGrids()*this.getGridWidth()+getGridWidth()/2)*0.00001);
	}
	//取每个格子的中心映射到最近的路为landmark
	public void saveLandmarks() {
		// TODO Auto-generated method stub
		
	}
	
	//调API获得起点终点格子中心的taxi时间
	public double getTime1(Coordinate start, Coordinate end) throws Exception, IOException{
		Coordinate GridStart=getCenter(getGrid(start));
		Coordinate GridEnd=getCenter(getGrid(end));
		System.out.println(GridStart.toString()+"  "+GridEnd.toString());
//		String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+GridStart.x+","+GridStart.y
//				+"&destination="+GridEnd.x+","+GridEnd.y+"&sensor=false";
		JSONObject baiduJSON = readJsonFromUrl("http://api.map.baidu.com/direction/v1?mode=driving&origin="+GridStart.y+","+GridStart.x+"&destination="+GridEnd.y+","+GridEnd.x
				+"&origin_region=北京&destination_region=北京&output=json&ak=FF5d28ddc84d30ea86d9207d9839d1ea"); 
//	    System.out.println(json.toString());  
	    JSONObject json = baiduJSON.getJSONObject("result").getJSONObject("taxi");
//	    System.out.println("time"+json2.get("duration"));
//	    System.out.println("distance"+json2.get("distance"));
	    this.setAvgSpeed(Double.parseDouble(json.get("distance").toString())/Double.parseDouble(json.get("duration").toString()));
		return Double.parseDouble(json.get("duration").toString());
	}
	
	//geotools解析路网计算最短路径
	public double getTime2(Coordinate start, Coordinate end){
		Path route =this.getRoadNetwork().getShortestPath(start, end);;
		//System.out.println(route.toString());
		List<Edge> edges = route.getEdges();
		double shortestDis=0;
		for(Edge e:edges){
			Node a=e.getNodeA();
            Node b=e.getNodeB();
            Coordinate a1= (Coordinate)a.getObject();
            Coordinate b1= (Coordinate)b.getObject();
            shortestDis += a1.distance(b1);
		}
		
		return shortestDis*100000/this.getAvgSpeed(); 
	}
	
	private  String readAll(Reader rd) throws IOException {  
	    StringBuilder sb = new StringBuilder();  
	    int cp;  
	    while ((cp = rd.read()) != -1) {  
	      sb.append((char) cp);  
	    }  
	    return sb.toString();  
	  }  
	
	public  JSONObject readJsonFromUrl(String url) throws IOException, JSONException {  
	    InputStream is = new URL(url).openStream();  
	    try {  
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));  
	      String jsonText = readAll(rd);  
	      JSONObject json = new JSONObject(jsonText);  
	      return json;  
	    } finally {  
	      is.close(); 
	    }  
	  }  
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		RoadGrid rg = new RoadGrid();
		System.out.println(rg.getWidth()+"  "+rg.getLength()+"   "+rg.getxGrids()+" "+rg.getyGrids());
		
		Coordinate start= new Coordinate(116.249664,39.965543);
		Coordinate end= new Coordinate(116.472824,39.809591);
//		Coordinate start= new Coordinate(116.397452,39.90888);
//		Coordinate end= new Coordinate(116.406313,39.924285);
		System.out.println(start.toString()+"  "+end.toString());
		double timeAPI,timeRN;
		timeAPI = rg.getTime1(start, end);
		timeRN = rg.getTime2(start, end);
		
		System.out.println("timeAPI:"+timeAPI+"   timeRN:"+timeRN);
		
		
		//landmark
		//saveLandmarks();
		
		
	}




}
