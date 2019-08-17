package net.hznu.common.chart;

public class MonitorStat {
	//该类针对统计图表中的某个行政区划的一级指标、二级指标、监测点得分值
	private String name;
	private double[] value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double[] getValue() {
		return value;
	}
	public void setValue(double[] value) {
		this.value = value;
	}
	
}
