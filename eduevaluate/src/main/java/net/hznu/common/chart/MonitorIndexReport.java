package net.hznu.common.chart;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("monitor_index_report")
public class MonitorIndexReport {
    @Column
    @Name
    private String code ;
    @Column
    private String name ;
    @Column
    private int location;
    @Column
    private double mvalue_threshold ;
    @Column
    private String mremark;
    @Column
    private String catacode;
    @Column
    private String condition;
    public String getcode() {
        return code;
    }
    public void setcode(String code) {
        this.code = code;
    }
    public String getname() {
        return name;
    }
    public void setname(String name) {
        this.name = name;
    }
    public int getlocation() {
        return location;
    }
    public void setlocation(int location) {
        this.location = location;
    }
    public double getMvalue_threshold() {
        return mvalue_threshold;
    }
    public void setMvalue_threshold(double mvalue_threshold) {
        this.mvalue_threshold = mvalue_threshold;
    }
    public String getMremark() {
        return mremark;
    }
    public void setMremark(String mremark) {
        this.mremark = mremark;
    }
    public String getcatacode() {
        return catacode;
    }
    public void setcatacode(String catacode) {
        this.catacode = catacode;
    }
    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
}
