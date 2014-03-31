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
import org.geotools.util.NullProgressListener;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

//import edu.ecnu.contest.taxi.test.MapMatchingMethod.TrPoint;

/** 
 * 鑾峰彇鐐瑰搴旂殑Road Segment
 * @description 
 * @author leyi  2013骞�0鏈�0鏃�
 * @version 0.0.1
 * @modify 
 * @Copyright 鍗庝笢甯堣寖澶у鏁版嵁绉戝涓庡伐绋嬬爺绌堕櫌鐗堟潈鎵�湁
 */
public class SnapSegment {
  //Logger logger=Logger.getLogger(SnapSegment.class.getName());
  
	 /**
	 * 璺綉鏁版嵁杈撳叆鏂囦欢
	 */
	String roadPathString;
	 /**
	 * STRtree绌洪棿绱㈠紩
	 */
	final SpatialIndex index = new STRtree();
	 /**
	 * 鏈�ぇ鎼滅储鑼冨洿
	 */
	double MAX_SEARCH_DISTANCE;
	 
    /**
     * Maximum time to spend running the snapping process (milliseconds)
     */
    final long DURATION = 500;
	
	public SnapSegment(String path) throws IOException{
		roadPathString=path;				
		buildIndex();
	}
	
	/**
	 * 寤虹珛璺鏌ヨ绱㈠紩
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
	                Geometry geom = (MultiLineString) simpleFeature.getDefaultGeometry();
	                // Just in case: check for  null or empty geometry
	                if (geom != null) {
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			SnapSegment snapsement=new SnapSegment("/Users/DXY/programming/road_network/road_network.shp");
			
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
