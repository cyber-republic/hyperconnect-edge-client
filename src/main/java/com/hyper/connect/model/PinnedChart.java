package com.hyper.connect.model;


import com.hyper.connect.model.enums.EventAverage;
import com.hyper.connect.model.enums.PinnedChartWindow;

public class PinnedChart{
	private int id;
	private int attributeId;
	private String attributeName;
	private PinnedChartWindow window; /*** hour, day, month ***/
	private EventAverage average; /*** real-time, 1m, 5m, 15m, 1h, 3h, 6h, 1d ***/

	public PinnedChart(int id, int attributeId, String attributeName, PinnedChartWindow window, EventAverage average){
		this.id=id;
		this.attributeId=attributeId;
		this.attributeName=attributeName;
		this.window=window;
		this.average=average;
	}

	public int getId(){
		return this.id;
	}

	public int getAttributeId(){
		return this.attributeId;
	}

	public String getAttributeName(){
		return this.attributeName;
	}

	public PinnedChartWindow getWindow(){
		return this.window;
	}

	public EventAverage getAverage(){
		return this.average;
	}

	public void setId(int id){
		this.id=id;
	}

	public void setAttributeId(int attributeId){
		this.attributeId=attributeId;
	}

	public void setAttributeName(String attributeName){
		this.attributeName=attributeName;
	}

	public void setWindow(PinnedChartWindow window){
		this.window=window;
	}

	public void setAverage(EventAverage average){
		this.average=average;
	}
	
	@Override
	public String toString(){
		String pinnedChart="id: "+id+", "+
			"attributeId: "+attributeId+", "+
			"attributeName: "+attributeName+", "+
			"window: "+window+", "+
			"average: "+average;
		return pinnedChart;
	}
}