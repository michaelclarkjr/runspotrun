package csc594.SemesterProject;

import java.util.ArrayList;

public class RouteItem 
{
	private String name;
	private String date;
	private String time;
	private String duration;
	private double distance;
	private int key;
	
	public int getKey() { return key; }
	public void setKey(int _key) { this.key = _key; }
	
	public String getName() { return name;  }
	public void setName(String name) { this.name = name; }
	
	public String getDate() { return date; }
	public void setDate(String _date) { this.date = _date; }
	
	public String getTime() { return time; }
	public void setTime(String _time) { this.time = _time; }
	
	public String getDuration() { return duration; }
	public void setDuration(String _dur) { this.duration = _dur; }
	
	public Double getDistance() { return distance; }
	public void setDistance(double _distance) { this.distance = _distance; }
	
	
	// / TEMP DEBUG--------------------
	public ArrayList getItems() {
	    ArrayList list = new ArrayList();
	    RouteItem item;
	
	    item = new RouteItem();
	    item.setName("item 1");
	    item.setDate("4/1/12");
	    item.setTime("12:11 pm");
	    item.setDistance(1.4);
	    list.add(item);
	
	    item = new RouteItem();
	    item.setName("item 1");
	    item.setDate("4/4/12");
	    item.setTime("2:11 pm");
	    item.setDistance(2.5);
	    list.add(item);
	
	    item = new RouteItem();
	    item.setName("item 1");
	    item.setDate("4/6/12");
	    item.setTime("3:11 pm");
	    item.setDistance(4.0);
	    list.add(item);
	
	    item = new RouteItem();
	    item.setName("item 1");
	    item.setDate("4/1/12");
	    item.setTime("12:11 pm");
	    item.setDistance(1.4);
	    list.add(item);
	
	    item = new RouteItem();
	    item.setName("item 1");
	    item.setDate("4/4/12");
	    item.setTime("2:11 pm");
	    item.setDistance(2.5);
	    list.add(item);
	
	    item = new RouteItem();
	    item.setName("item 1");
	    item.setDate("4/6/12");
	    item.setTime("3:11 pm");
	    item.setDistance(4.0);
	    list.add(item);
	    item = new RouteItem();
	    item.setName("item 1");
	    item.setDate("4/1/12");
	    item.setTime("12:11 pm");
	    item.setDistance(1.4);
	    list.add(item);
	
	    return list;
	}
} 