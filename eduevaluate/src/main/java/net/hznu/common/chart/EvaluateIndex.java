package net.hznu.common.chart;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

@Table("evaluate_index_view")
public class EvaluateIndex {
    @Column("unitcode")
    private String xzqhdm;
    @Column("year")
    private int year;
    @Column("xzqhmc")
    private String xzqhmc;
    @Column("value")
    private double value;
    @Column("score")
    private double score;
    @Column("catacode")
    private String catacode;
    public String getXzqhdm() {
        return xzqhdm;
    }
    public void setXzqhdm(String xzqhdm) {
        this.xzqhdm = xzqhdm;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public String getXzqhmc() {
        return xzqhmc;
    }
    public void setXzqhmc(String xzqhmc) {
        this.xzqhmc = xzqhmc;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }
    public String getcatacode() {
        return catacode;
    }
    public void setcatacode(String catacode) {
        this.catacode = catacode;
    }
}
