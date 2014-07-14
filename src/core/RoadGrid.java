package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections.map.LinkedMap;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Node;

import roadmatch.SnapSegment;

import com.vividsolutions.jts.geom.Coordinate;

public class RoadGrid implements Serializable {
	// RoadNetwork RoadNetwork;
	Coordinate LeftUp = new Coordinate();
	Coordinate LeftDown = new Coordinate();// x,y lng,lat
	Coordinate RightDown = new Coordinate();
	Coordinate RightUp = new Coordinate();
	int width, length;
	int gridWidth, gridLength;
	int yGrids, xGrids;
	double AvgSpeed;
	// ItemVisitor Grids = new ArrayListVisitor();
	public Grid[][] grids;
	transient SnapSegment RoadNetwork;
	List<LinkedHashMap<Integer, Double>> SpatialIndex;
	public List<LinkedHashMap<Integer, Double>> TemporalIndex;

	private static RoadGrid rg = null;

	private RoadGrid() throws Exception {
		RoadNetwork=new SnapSegment("road_network/road_network.shp");//"C:\\Users\\dxy\\road_network\\road_network.shp");

		LeftUp = new Coordinate(116.215, 40.030);// (40.220, 116.095);
		LeftDown = new Coordinate(116.215, 39.780);// (39.715, 116.095);
		RightDown = new Coordinate(116.545, 39.780);// (39.715, 116.708);
		RightUp = new Coordinate(116.545, 40.030);// (40.220, 116.708);
		// 2-dimensional Euclidean distance，向上取整25000*33000 m*m
		setWidth((int) Math.ceil(100000 * LeftUp.distance(LeftDown)));
		setLength((int) Math.ceil(100000 * LeftDown.distance(RightDown)));
		// 每个格子的长宽 m
		setGridWidth(1000);
		setGridLength(1000);
		// 长和宽的格子个数 25*33
		setyGrids((int) (getWidth() / getGridWidth()));
		setxGrids((int) (getLength() / getGridLength()));
		setAvgSpeed(50);// M/s

		SpatialIndex = new ArrayList<LinkedHashMap<Integer, Double>>();
		TemporalIndex = new ArrayList<LinkedHashMap<Integer, Double>>();

		for (int i = 0; i < 825; i++) {
			SpatialIndex.add(new LinkedHashMap<Integer, Double>());
			TemporalIndex.add(new LinkedHashMap<Integer, Double>());
		}

		// 统计时间
		long begintime = System.nanoTime();
		//initGridMatrix("Output/grid.txt");
		long endtime = System.nanoTime();
		long costTime = (endtime - begintime) / 1000000;
		System.out.println("initGridMatrix:" + costTime + " ms");

	}

	// singleton get instance
	public static RoadGrid getRoadGrid() throws Exception {
		if (rg == null) {                                                                                       
			 FileInputStream fis = new FileInputStream("roadgrid");
			 ObjectInputStream ois = new ObjectInputStream(fis); 
			 RoadGrid rg = (RoadGrid) ois.readObject(); 
//			 rg.print(); 
			 ois.close();
			 return rg;
		}
		return rg;
	}

	public void initGridMatrix(String file) throws Exception {
		grids = new Grid[xGrids * yGrids][xGrids * yGrids];
		FileReader fr = new FileReader(new File(file));
		BufferedReader br = new BufferedReader(fr);
		String line;
		List<Grid> list = new LinkedList<Grid>();
		int src, tar, pre_src = -1;
		while ((line = br.readLine()) != null) {

			String[] str = line.split("\t");

			src = Integer.parseInt(str[0]);
			tar = Integer.parseInt(str[1]);
			// System.out.println(pre_src+"  "+src+"  "+tar);
			// jump no routes grid or to self
			if (!"null".equals(str[4]) && !"0".equals(str[4])) {
				double d = Double.parseDouble(str[4]);
				double t = Double.parseDouble(str[5]);
				grids[src][tar] = new Grid(t, d);
				grids[src][tar].setIndex(tar);
			}

			if (src == pre_src && grids[src][tar] != null) {
				list.add(grids[src][tar]);
				// System.out.println("add: "+grids[src][tar].toString());
			} else if (pre_src != -1 && grids[src][tar] != null) {// Math.abs(grids[src][tar].time)>-0.1){//add
																	// a new
																	// grid
			// System.out.println(pre_src+"  "+src+"  "+tar+"  contains?"+list.contains(grids[src][src])+"/"+list.size());
				// spatial index
				// sort 823 grids by distance
				Collections.sort(list, new Comparator<Grid>() {
					@Override
					public int compare(Grid o1, Grid o2) {
						double a = o1.getDistance() - o2.getDistance();
						if (a > 0)
							return 1;
						else if (a < 0)
							return -1;
						else
							return 0;
					}
				});

				LinkedHashMap<Integer, Double> map = SpatialIndex.get(pre_src);
				// add top 20 grids to the index
				for (Grid g : list) {
					if (map.size() < 20) {
						map.put(g.getIndex(), g.getDistance());
//						System.out.print(g.toString()+"  ");
					} else {
						break;
					}
				}

//				System.out
//						.println("src: " + pre_src + "//" + "size: "
//								+ list.size() + "//"
//								+ SpatialIndex.get(pre_src).size());
				// tempral index
				Collections.sort(list, new Comparator<Grid>() {
					@Override
					public int compare(Grid o1, Grid o2) {
						double a = o1.getTime() - o2.getTime();
						if (a > 0)
							return 1;
						else if (a < 0)
							return -1;
						else
							return 0;
					}
				});
				map = TemporalIndex.get(pre_src);
				// add top 20 grids to the index
				for (Grid g : list) {
					if (map.size() < 20) {
						map.put(g.getIndex(), g.getTime());
//						System.out.print(g.toString()+"  ");
					} else {
						break;
					}
				}
//				System.out.println("src: " + pre_src + "//" + "size: "
//						+ list.size() + "//"
//						+ TemporalIndex.get(pre_src).size());

				list = new LinkedList<Grid>();
				list.add(grids[src][tar]);
			}

			pre_src = src;
		}
	}

	public void print() {
		int a = 0;
		Iterator it1 = SpatialIndex.iterator();
		while (it1.hasNext()) {
			LinkedHashMap<Integer, Double> map = (LinkedHashMap<Integer, Double>) it1.next();
			System.out.println(a + ":" + map.size());
			a++;
		}
		System.out.println("****************Grids");
		for (int i = 0; i < grids.length; i++) {
			System.out.print(i + "  " + grids[i].length);
		}
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

	// 返回经纬度所在的格子
	public int getGrid(Coordinate location) {
		double x = (location.x - LeftDown.x) / LeftDown.distance(RightDown)
				* this.getxGrids();
		double y = (location.y - LeftDown.y) / LeftDown.distance(LeftUp)
				* this.getyGrids();
//		System.out.print(x + " " + Math.floor(x) + "    ");
//		System.out.println(y + " " + Math.floor(y));
		return (int) (this.getyGrids() * Math.floor(x) + Math.floor(y));
	}

	public Coordinate getCenter(int Grid) {
		return new Coordinate(
				LeftDown.x
						+ (Grid / getyGrids() * this.getGridWidth() + (Grid
								% getyGrids() != 0 ? getGridLength() / 2 : 0))
						* 0.00001,
				LeftDown.y
						+ (Grid % getyGrids() * this.getGridWidth() + getGridWidth() / 2)
						* 0.00001);
	}

	// 取每个格子的中心映射到最近的路为landmark
	public void saveLandmarks() {
		// TODO Auto-generated method stub

	}

	// 调API获得任意两点时间
	public double getTime1(Coordinate start, Coordinate end) throws Exception,
			IOException {
		Coordinate GridStart = getCenter(getGrid(start));
		Coordinate GridEnd = getCenter(getGrid(end));
		System.out.println(GridStart.toString() + "  " + GridEnd.toString());
		// String url =
		// "http://maps.googleapis.com/maps/api/directions/json?origin="+GridStart.x+","+GridStart.y
		// +"&destination="+GridEnd.x+","+GridEnd.y+"&sensor=false";
		JSONObject baiduJSON = readJsonFromUrl("http://api.map.baidu.com/direction/v1?mode=driving&origin="
				+ GridStart.y
				+ ","
				+ GridStart.x
				+ "&destination="
				+ GridEnd.y
				+ ","
				+ GridEnd.x
				+ "&origin_region=北京&destination_region=北京&output=json&ak=FF5d28ddc84d30ea86d9207d9839d1ea");
		// System.out.println(json.toString());
		JSONObject json = baiduJSON.getJSONObject("result").getJSONObject(
				"taxi");
		// System.out.println("time"+json2.get("duration"));
		// System.out.println("distance"+json2.get("distance"));
		this.setAvgSpeed(Double.parseDouble(json.get("distance").toString())
				/ Double.parseDouble(json.get("duration").toString()));
		return Double.parseDouble(json.get("duration").toString());
	}

	// 调API获得起点终点在格子中心的距离和时间
	public String getShortestDistanceTime1(Coordinate start, Coordinate end)
			throws Exception, IOException {

		// String url =
		// "http://maps.googleapis.com/maps/api/directions/json?origin="+GridStart.x+","+GridStart.y
		// +"&destination="+GridEnd.x+","+GridEnd.y+"&sensor=false";
		try {
			JSONObject baiduJSON = readJsonFromUrl("http://api.map.baidu.com/direction/v1?mode=driving&origin="
					+ start.y
					+ ","
					+ start.x
					+ "&destination="
					+ end.y
					+ ","
					+ end.x
					+ "&origin_region=北京&destination_region=北京&output=json&ak=FF5d28ddc84d30ea86d9207d9839d1ea");
			// System.out.println(json.toString());
			JSONObject json = baiduJSON.getJSONObject("result").getJSONObject(
					"taxi");
			// System.out.println("time"+json2.get("duration"));
			// System.out.println("distance"+json2.get("distance"));
			this.setAvgSpeed(Double
					.parseDouble(json.get("distance").toString())
					/ Double.parseDouble(json.get("duration").toString()));
			return json.get("distance").toString() + "\t"
					+ json.get("duration").toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JSONObject baiduJSON = readJsonFromUrl("http://api.map.baidu.com/direction/v1?mode=driving&origin="
					+ start.y
					+ ","
					+ start.x
					+ "&destination="
					+ end.y
					+ ","
					+ end.x
					+ "&origin_region=北京&destination_region=北京&output=json&ak=FF5d28ddc84d30ea86d9207d9839d1ea");
			System.out.println(baiduJSON.toString());
			return "null\tnull";
		}

	}

	// geotools解析路网计算任意两点时间
	public double getTime2(Coordinate start, Coordinate end) {
		Path route;// = RoadNetwork.getShortestPath(start, end);
		route = RoadNetwork.getNewShortestPath(this.RoadNetwork
				.getCloseNodeOnFeature(start,
						this.RoadNetwork.getNearestSegment(start)),
				this.RoadNetwork.getCloseNodeOnFeature(end,
						this.RoadNetwork.getNearestSegment(end)));
		// System.out.println(route.toString());
		List<Edge> edges = route.getEdges();
		double shortestDis = 0;
		for (Edge e : edges) {
			Node a = e.getNodeA();
			Node b = e.getNodeB();
			Coordinate a1 = (Coordinate) a.getObject();
			Coordinate b1 = (Coordinate) b.getObject();
			shortestDis += a1.distance(b1);
		}

		return shortestDis * 100000 / this.getAvgSpeed();
	}

	// geotools计算起点终点在格子中心最短路径和时间
	public String getShortestDistanceTime2(Coordinate start, Coordinate end) {
		Path route;
		try {
			route = RoadNetwork.getNewShortestPath(this.RoadNetwork
					.getCloseNodeOnFeature(start,
							this.RoadNetwork.getNearestSegment(start)),
					this.RoadNetwork.getCloseNodeOnFeature(end,
							this.RoadNetwork.getNearestSegment(end)));
		} catch (NullPointerException e) {
			route = null;
		}
		if (route != null) {
			List<Edge> edges = route.getEdges();
			double shortestDis = 0;
			for (Edge e : edges) {
				Node a = e.getNodeA();
				Node b = e.getNodeB();
				Coordinate a1 = (Coordinate) a.getObject();
				Coordinate b1 = (Coordinate) b.getObject();
				shortestDis += a1.distance(b1);
			}
			shortestDis = shortestDis * 100000;

			return this.getAvgSpeed()+"(avgspeed) "+doubleDigits(shortestDis) + "\t"
					+ doubleDigits(shortestDis / this.getAvgSpeed());
		} else {
			return "null\tnull";
		}
	}

	public String doubleDigits(double d) {
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(d);
	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
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
		 RoadGrid lt = new RoadGrid();

//		 FileOutputStream fos = new FileOutputStream("roadgrid");
//		 ObjectOutputStream oos = new ObjectOutputStream(fos);
//		 
//		 oos.writeObject(lt);
//		 oos.close();
//		 
//		 FileInputStream fis = new FileInputStream("roadgrid");
//		 ObjectInputStream ois = new ObjectInputStream(fis); 
//		 RoadGrid rg =	 (RoadGrid) ois.readObject(); 
//		 rg.print(); 
//		 ois.close();
		 
		// String str = "abc";
		// String target = "abd";
		// System.out.println("similarityRatio="+ lt.getSimilarityRatio(str,
		// target));

		// TODO Auto-generated method stub
		// 
		//
		// System.out.println(rg.getWidth()+"  "+rg.getLength()+"   "+rg.getxGrids()+" "+rg.getyGrids());
		// int l = rg.getxGrids();
		// int w = rg.getyGrids();
		 RoadGrid rg = new RoadGrid();
		 
		 Coordinate start= new Coordinate(116.249664,39.965543);
		 Coordinate end= new Coordinate(116.472824,39.809591);
		 
		 //System.out.println(start.toString()+"  "+end.toString());
		 String timeAPI,timeRN;
		 timeAPI = rg.getShortestDistanceTime1(rg.getCenter(3), rg.getCenter(5));
		 timeRN = rg.getShortestDistanceTime2(rg.getCenter(3), rg.getCenter(5));
		 
		 System.out.println("timeAPI:"+timeAPI+"   timeRN:"+timeRN);

		/*timeAPI:3473	942   timeRN:50.0(avgspeed) 3471.43	69.43
		 * FileWriter fw1 = new FileWriter(new
		 * File("C:\\Output\\gridDistance-65")); //BufferWriter bw = new
		 * BufferWriter(); //FileWriter fw2 = new FileWriter(new
		 * File("C:\\Users\\dxy\\Output\\gridDistance-BaiduAPI"));
		 * 
		 * for(int i=65;i<l;i++) for(int j =0;j<w;j++){ if(i!=j){
		 * fw1.write(i+"\t"+j+"\t"+rg.getShortestDistanceTime1(rg.getCenter(i),
		 * rg.getCenter(j))+"\t" +rg.getShortestDistanceTime2(rg.getCenter(i),
		 * rg.getCenter(j))+"\n"); fw1.flush();
		 * //System.out.println(i+"\t"+j+"\t"
		 * +rg.getShortestDistanceTime2(rg.getCenter(i), rg.getCenter(j)));
		 * }else{
		 * 
		 * fw1.write(i+"\t"+j+"\t"+0+"\t"+0+"\n"); fw1.flush();
		 * //System.out.println(i+"\t"+j+"\t"+0); } }
		 * 
		 * fw1.close();
		 */
		// landmark
		// saveLandmarks();

	}

}
