package net.hznu.common.chart;

public class CustomStat {
    //该类针对发展性指标内容
    private String indexname;
    private String taskname;
    private String taskdetail;
    private String analysis_s;

    private double weight;
    private double score_s;


    public String getIndexname() {
        return indexname;
    }

    public void setIndexname(String indexname) {
        this.indexname = indexname;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getTaskdetail() {
        return taskdetail;
    }

    public void setTaskdetail(String taskdetail) {
        this.taskdetail = taskdetail;
    }

    public String getAnalysis_s() {
        return analysis_s;
    }

    public void setAnalysis_s(String analysis_s) {
        this.analysis_s = analysis_s;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getScore_s() {
        return score_s;
    }

    public void setScore_s(double score_s) {
        this.score_s = score_s;
    }

}
