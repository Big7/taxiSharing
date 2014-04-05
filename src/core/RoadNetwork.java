package core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;

import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.graph.build.line.BasicLineGraphGenerator;
import org.geotools.graph.path.DijkstraShortestPathFinder;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Node;
import org.geotools.graph.traverse.standard.DijkstraIterator;
import org.geotools.graph.traverse.standard.DijkstraIterator.EdgeWeighter;
import org.geotools.util.NullProgressListener;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

//import edu.ecnu.contest.taxi.test.MapMatchingMethod.TrPoint;

/** 
 * 閼惧嘲褰囬悙鐟邦嚠鎼存梻娈慠oad Segment
 * @description 
 * @author leyi  2013楠烇拷0閺堬拷0閺冿拷
 * @version 0.0.1
 * @modify 
 * @Copyright 閸楀簼绗㈢敮鍫ｅ瘱婢堆冾劅閺佺増宓佺粔鎴濐劅娑撳骸浼愮粙瀣埡缁屽爼娅岄悧鍫熸綀閹碉拷婀�
 */
public class RoadNetwork {
  //Logger logger=Logger.getLogger(SnapSegment.class.getName());
	String roadPathString;
	final SpatialIndex index = new STRtree();
    final BasicLineGraphGenerator graphGen = new BasicLineGraphGenerator();
    final EdgeWeighter weighter = new DijkstraIterator.EdgeWeighter() {
       public double getWeight(Edge e) {
           try{
               
               Node a=e.getNodeA();
               Node b=e.getNodeB();
               Coordinate a1= (Coordinate)a.getObject();
               Coordinate b1= (Coordinate)b.getObject();
        
               double tmp =a1.distance(b1);
               return tmp;
        
           }catch(Exception kk)
           {
               System.out.print("kk");
               return 1;
           }
          
       }
    };
     /**
     * 閺堬拷銇囬幖婊呭偍閼煎啫娲�
     */
    double MAX_SEARCH_DISTANCE;
     
    /**
     * Maximum time to spend running the snapping process (milliseconds)
     */
    final long DURATION = 500;
    
    public RoadNetwork(String path) throws IOException{
        roadPathString=path;                
        buildIndex();
    }
    
    /**
     * 瀵よ櫣鐝涚捄顖涱唽閺屻儴顕楃槐銏犵穿
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    public void buildIndex() throws IOException {
        
         FileDataStore store = FileDataStoreFinder.getDataStore(new File(roadPathString));
         FeatureSource source = store.getFeatureSource();
         
         FeatureCollection features = source.getFeatures();
        
        
            //logger.info("Slurping in features ...");
            features.accepts(new FeatureVisitor() {

                public void visit(Feature feature) {
                    SimpleFeature simpleFeature = (SimpleFeature) feature;
                    //System.out.println((MultiLineString) simpleFeature.getDefaultGeometry());
                    
                    MultiLineString sptmp=(MultiLineString) simpleFeature.getDefaultGeometry();
                    
                    
                    Geometry geom = (MultiLineString) simpleFeature.getDefaultGeometry();
                    // Just in case: check for  null or empty geometry
                    if (geom != null) {
                        
                        Coordinate[] tmp_co=geom.getCoordinates();
                        for(int j=0;j<tmp_co.length-1;j++)
                        {
                            LineSegment aa=new LineSegment();
                            aa.setCoordinates(tmp_co[j], tmp_co[j+1]);
                            //System.out.println(tmp_co[j]+"   "+tmp_co[j+1]);
                            
                            
                            
                            graphGen.add(aa);                           
                            //graphGen.
                        }                
                        Envelope env = geom.getEnvelopeInternal();
                        if (!env.isNull()) {
                            index.insert(env, simpleFeature);
                        }
                    }
                }
            }, new NullProgressListener()); 
            
            store.dispose();
            
            
            
            
            
            ReferencedEnvelope bounds = features.getBounds();
            MAX_SEARCH_DISTANCE=bounds.getSpan(0) / 300.0;
    }
    
    /**
     * Get the nearest segment ID
     * @param pt
     * @return if null, it is too far from road segment
     */
    @SuppressWarnings("unchecked")
    public String getNearestSegmentID(Coordinate pt){
        String segmentID=null;
        
         // Get point and create search envelope
        Envelope search = new Envelope(pt);
        search.expandBy(MAX_SEARCH_DISTANCE);

        /*
         * Query the spatial index for objects within the search envelope.
         * Note that this just compares the point envelope to the line envelopes
         * so it is possible that the point is actually more distant than
         * MAX_SEARCH_DISTANCE from a line.
         */
        List<SimpleFeature> lines = index.query(search);

        // Initialize the minimum distance found to our maximum acceptable
        // distance plus a little bit
        double minDist = MAX_SEARCH_DISTANCE + 1.0e-6;
        Coordinate minDistPoint = null;
        SimpleFeature nearest=null;

        for (SimpleFeature line : lines) {
            Geometry geom = (MultiLineString) line.getDefaultGeometry();
            LocationIndexedLine locationline=new LocationIndexedLine(geom);
            LinearLocation here = locationline.project(pt);
            Coordinate point = locationline.extractPoint(here);
            double dist = point.distance(pt);
            if (dist < minDist) {
                minDist = dist;
                minDistPoint = point;
                nearest=line;
            }
        }


        if (minDistPoint == null) {
            // No line close enough to snap the point to
            //System.out.println(pt + "- X");

        } else {
            segmentID=(String) nearest.getAttribute("ID");
        }
        
        return segmentID;
    }
    
    /**
     * Get nearest segment feature. If need ID, can just use GetNearestSegmentID
     * @param pt
     * @return if no nearest segment, return null
     */
    @SuppressWarnings("unchecked")
    public SimpleFeature getNearestSegment(Coordinate pt){
        SimpleFeature segment=null;
        
         // Get point and create search envelope
       Envelope search = new Envelope(pt);
       search.expandBy(MAX_SEARCH_DISTANCE);

       /*
        * Query the spatial index for objects within the search envelope.
        * Note that this just compares the point envelope to the line envelopes
        * so it is possible that the point is actually more distant than
        * MAX_SEARCH_DISTANCE from a line.
        */
       List<SimpleFeature> lines = index.query(search);

       // Initialize the minimum distance found to our maximum acceptable
       // distance plus a little bit
       double minDist = MAX_SEARCH_DISTANCE + 1.0e-6;
       Coordinate minDistPoint = null;
       SimpleFeature nearest=null;

       for (SimpleFeature line : lines) {
        Geometry geom = (MultiLineString) line.getDefaultGeometry();
        
        
        LocationIndexedLine locationline=new LocationIndexedLine(geom);
           LinearLocation here = locationline.project(pt);
           Coordinate point = locationline.extractPoint(here);
           double dist = point.distance(pt);
           if (dist < minDist) {
               minDist = dist;
               minDistPoint = point;
               nearest=line;
           }
       }


       if (minDistPoint == null) {
           // No line close enough to snap the point to
           System.out.println(pt + "- X");

       } else {
        segment=nearest;
       }
        
        return segment;
    }
    
    
    public List<SimpleFeature> getNearestSegments(Coordinate pt)
    {
        List<SimpleFeature> res=new ArrayList<SimpleFeature>();
        Envelope search = new Envelope(pt);
        search.expandBy(MAX_SEARCH_DISTANCE);
        List<SimpleFeature> lines = index.query(search);
        double minDist = MAX_SEARCH_DISTANCE + 1.0e-6;
        Coordinate minDistPoint = null;
        SimpleFeature nearest=null;
        
        int i=0;
        for(i=0;i<3;i++)
        {
            if(lines.size()==0)
            {
                break;
            }
            else
            {
                minDist=99999999;
                for (SimpleFeature line : lines) {
                    Geometry geom = (MultiLineString) line.getDefaultGeometry();
                    LocationIndexedLine locationline=new LocationIndexedLine(geom);
                    LinearLocation here = locationline.project(pt);
                    Coordinate point = locationline.extractPoint(here);
                    double dist = point.distance(pt);
                    if (dist < minDist) {
                        minDist = dist;
                        minDistPoint = point;
                        nearest=line;
                    }
                }
                if (minDistPoint == null)
                {
                    return null;
                }
                res.add(nearest);
                lines.remove(nearest);
                
            }
        }       
        return res;
    }
    
    
    public Path getShortestPath(Coordinate start,Coordinate end)
    {
        start=this.getCloseNodeOnFeature(start, this.getNearestSegment(start));
        end = this.getCloseNodeOnFeature(end, this.getNearestSegment(end));
        Node dd=  graphGen.getNode(start);
        Node dd2=  graphGen.getNode(end);
        
        Path res=null;
        try{
        DijkstraShortestPathFinder pf = new DijkstraShortestPathFinder( graphGen.getGraph(), graphGen.getNode(start), weighter );
        pf.calculate();
        Path path = pf.getPath(dd2);
        res=path;
        }catch(Exception e)
        {
            res=null;
        }
        return res;
        
    }
    
    
    public void testme()
    {
        try{
        Coordinate c=new Coordinate();
        c.x=115.87710200000004;
        c.y= 40.039235000000076;
        
        Coordinate d=new Coordinate();
        d.x=115.87701000000004;
        d.y=40.03907000000004;
        Edge ee=graphGen.getEdge(c, d);
        Node a=ee.getNodeA();
        Coordinate pp=(Coordinate)a.getObject();
        System.out.print("asd");
        }catch(Exception e)
        {
            System.out.print("error");
        }
    }
    
    
    public Coordinate getCloseNodeOnFeature(Coordinate pt,SimpleFeature feature)
    {
        Coordinate res=null;
        double distance=999999;
        Geometry geom = (MultiLineString) feature.getDefaultGeometry();
        
        for(Coordinate tmp:geom.getCoordinates())
        {
            if(tmp.distance(pt)<distance)
            {
                distance=tmp.distance(pt);
                res=tmp;
            }
        }
        
        return res;
    }
    
    public Double getPathLength(Path path)
    {
        double res=0;
        List<Edge> edges= path.getEdges();
        
        for(Edge ede:edges)
        {
            try{
                   Node a=ede.getNodeA();
                   Node b=ede.getNodeB();
                   Coordinate a1= (Coordinate)a.getObject();
                   Coordinate b1= (Coordinate)b.getObject();
            
                   res=res+a1.distance(b1);                
                   
               }catch(Exception kk)
               {
                   System.out.print("kk");
                   return (new Double(0));
               }
        }
        return res;
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			RoadNetwork snapsement=new RoadNetwork("C:\\Users\\dxy\\road_network\\road_network.shp");
			
	        Coordinate point=new Coordinate();
	        point.x=116.07644300000004;
	        point.y=39.64440900000005;
	        SimpleFeature a=snapsement.getNearestSegment(point);
            Geometry geom = (MultiLineString) a.getDefaultGeometry();
            Coordinate[] b=geom.getCoordinates();
            for(Coordinate c:b)
            {
                System.out.println(c.x+" "+c.y);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print(e.getMessage());
		}  
	}
	
	
}
