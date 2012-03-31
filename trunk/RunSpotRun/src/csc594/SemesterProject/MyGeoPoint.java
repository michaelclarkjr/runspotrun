package csc594.SemesterProject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.maps.GeoPoint;

/** Class to hold our location information */
public class MyGeoPoint implements Parcelable
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
		
	public MyGeoPoint(int latitude, int longitude, String time, String dist, String name, MyPointType type) 
	{
		this.mName = name;
		this.mTime = time;
		this.mDistance = dist;
		this.mType = MyPointType.None;
		this.mPoint = new GeoPoint(latitude,longitude);
		this.mType = type;
	}
	
	public MyGeoPoint(int latitude, int longitude, MyPointType type) 
	{
		this(latitude,longitude, "12-1-1 12:12:22","1.1", "",type);
	}
	
	public MyGeoPoint(int latitude, int longitude)
	{
		this(latitude,longitude, "12-1-1 12:12:22","1.1", "", MyPointType.Normal);
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

//BELOW is Parcel Implementation Code 
// Basically the way we can pass a List of My GeoPoints into the Map Activity easily
	public int describeContents() {		
		return 0;
	}

    public void writeToParcel(Parcel out, int flags) {
    	//GeoPoint
        out.writeInt(mPoint.getLatitudeE6());
        out.writeInt(mPoint.getLongitudeE6());
        //Time
        out.writeString(mTime);        
        //Distance
        out.writeString(mDistance);
        //Name
        out.writeString(mName);
        //Type
        out.writeString(mType.toString());
    }

    public static final Parcelable.Creator<MyGeoPoint> CREATOR
            = new Parcelable.Creator<MyGeoPoint>() {
        public MyGeoPoint createFromParcel(Parcel in) {
            return new MyGeoPoint(in);
        }

        public MyGeoPoint[] newArray(int size) {
            return new MyGeoPoint[size];
        }
    };

    private MyGeoPoint(Parcel in) {
    	//MUST be same order as write above!!!
        int lat = in.readInt();
        int lon = in.readInt();
        mPoint = new GeoPoint(lat,lon);
        mTime = in.readString();
        mDistance = in.readString();
        mName = in.readString();
        String typeAsString = in.readString();
		if(typeAsString.equals(MyPointType.Normal.toString()))
		{ this.mType = MyPointType.Normal;}
		else if(typeAsString.equals(MyPointType.Start.toString()))
		{ this.mType = MyPointType.Start;}
		else if(typeAsString.equals(MyPointType.Stop.toString()))
		{ this.mType = MyPointType.Stop;}
		else//DEFAULT: if(type.equals(MyPointType.None.toString()))
		{ this.mType = MyPointType.None;}
    }
}
