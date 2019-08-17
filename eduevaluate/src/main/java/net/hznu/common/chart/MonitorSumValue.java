package net.hznu.common.chart;

public class MonitorSumValue {
	//该类针对统计图表中的某个行政区划的总得分值
	private String code;
	private String name;
	private double value;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
