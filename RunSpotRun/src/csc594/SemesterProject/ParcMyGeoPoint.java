package csc594.SemesterProject;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcMyGeoPoint implements Parcelable {

    private MyGeoPoint geoPoint;

    public ParcMyGeoPoint(MyGeoPoint point) {
         geoPoint = point;
    }

    public MyGeoPoint getGeoPoint() {
         return geoPoint;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
    	//GeoPoint
        out.writeInt(geoPoint.getPoint().getLatitudeE6());
        out.writeInt(geoPoint.getPoint().getLongitudeE6());
        //Time
        out.writeString(geoPoint.getTime());        
        //Distance
        out.writeString(geoPoint.getDistance());
        //Name
        out.writeString(geoPoint.getName());
        //Type
        out.writeString(geoPoint.getType().toString());
    }

    public static final Parcelable.Creator<ParcMyGeoPoint> CREATOR
            = new Parcelable.Creator<ParcMyGeoPoint>() {
        public ParcMyGeoPoint createFromParcel(Parcel in) {
            return new ParcMyGeoPoint(in);
        }

        public ParcMyGeoPoint[] newArray(int size) {
            return new ParcMyGeoPoint[size];
        }
    };

    private ParcMyGeoPoint(Parcel in) {
    	//MUST be same order as write above!!!
        int lat = in.readInt();
        int lon = in.readInt();
        String time = in.readString();
        String distance = in.readString();
        String name = in.readString();
        String typeAsString = in.readString();
        
        geoPoint = new MyGeoPoint(lat, lon, time, distance, name, typeAsString);
    }
}