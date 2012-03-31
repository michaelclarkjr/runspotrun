package csc594.SemesterProject;

import com.google.android.maps.GeoPoint;

/** Class to hold our location information */
public class MyGeoPoint 
{
	enum MyPointType{ None, Normal, Start, Stop}
	
	//point
	private GeoPoint mPoint;
	//time point was taken
	private String mTime;
	//distance from first point
	private String mDistance;
	//type of point
	private MyPointType mType;
	
	//name of point
	private String mName;
		
	public MyGeoPoint(int latitude, int longitude, String time, String dist, String name, String type) 
	{
		this.mName = name;
		this.mTime = time;
		this.mDistance = dist;
		this.mType = MyPointType.None;
		mPoint = new GeoPoint(latitude,longitude);
				
		if(type.equals(MyPointType.Normal.toString()))
		{ this.mType = MyPointType.Normal;}
		else if(type.equals(MyPointType.Start.toString()))
		{ this.mType = MyPointType.Start;}
		else if(type.equals(MyPointType.Stop.toString()))
		{ this.mType = MyPointType.Stop;}
		else//DEFAULT: if(type.equals(MyPointType.None.toString()))
		{ this.mType = MyPointType.None;}
	}
	
	public MyGeoPoint(int latitude, int longitude, MyPointType type) 
	{
		Double lat = (latitude/1E6);
		Double lon = (longitude/1E6);
		this.mName = lat.toString() + "\n" + lon.toString();
		this.mTime = "12-1-1 12:12:22";
		this.mDistance = "1.1";
		this.mType = type;
		mPoint = new GeoPoint(latitude,longitude);
	}
	
	public MyGeoPoint(int latitude, int longitude) 
	{
		Double lat = (latitude/1E6);
		Double lon = (longitude/1E6);
		this.mName = lat.toString() + "\n" + lon.toString();
		this.mTime = "12-1-1 12:12:22";
		this.mDistance = "1.1";
		this.mType = MyPointType.Normal;
		mPoint = new GeoPoint(latitude,longitude);
	}
	
	public MyGeoPoint(String name,double latitude, double longitude) 
	{
		this.mName = name;
		mPoint = new GeoPoint((int)(latitude*1e6),(int)(longitude*1e6));
	}

	public GeoPoint getPoint() {
		return mPoint;
	}

	public String getPointAsString() {
		return "Point: " + mPoint.toString();
	}
	
	public MyPointType getType() {
		return mType;
	}

	public String getTypeAsString() {
		return "GeoPoint: " + mType.toString();
	}
	
	public String getTime() {
		return mTime;
	}
	
	public String getTimeAsString() {
		return "Time: " + mTime;
	}
	
	public String getDistance() {
		return mDistance;
	}
	
	public String getDistanceAsString() {
		return "Distance:" + mDistance + " miles";
	}
	
	
	public String getName() {
		return mName;
	}
}
