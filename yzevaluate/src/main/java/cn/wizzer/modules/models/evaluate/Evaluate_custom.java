package cn.wizzer.modules.models.evaluate;

import cn.wizzer.common.base.Model;
import cn.wizzer.modules.models.monitor.Monitor_index;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.List;

@Table("evaluate_custom")
public class Evaluate_custom extends Model implements Serializable{
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("评估ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String evaluateId;

    @Column
    @Comment("指标")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    private String indexname;

    @Column
    @Comment("具体发展性目标（任务)")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String taskname;

    @Column
    @Comment("达成标志")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String taskdetail;

    @Column
    @Comment("原有基础自我分析")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String analysis_s;
    @Column
    @Comment("分值")
    @ColDefine(type = ColType.FLOAT, width = 3)
    private double weights;

    @Column
    @Comment("自打分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score_s;

    @Column
    @Comment("自评理由")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String remark_s;

    @Column
    @Comment("督评分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score_p;

    @Column
    @Comment("督评理由")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String remark_p;

    @Column
    @Comment("是否自评完成")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean selfeva = null;

    @Column
    @Comment("是否审核完成")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean verifyeva = null;


    @Column
    @Comment("指标序号")
    private int location;

    @Many(target=Evaluate_appendix.class,field = "remarkid")
    private List<Evaluate_appendix> appendixList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getEvaluateId() {
        return evaluateId;
    }

    public void setEvaluateId(String evaluateId) {
        this.evaluateId = evaluateId;
    }



    public double getScore_s() {
        return score_s;
    }

    public void setScore_s(double score_s) {
        this.score_s = score_s;
    }




    public double getScore_p() {
        return score_p;
    }

    public void setScore_p(double score_p) {
        this.score_p = score_p;
    }



    public Boolean getSelfeva() {
        return selfeva;
    }

    public void setSelfeva(Boolean selfeva) {
        this.selfeva = selfeva;
    }

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

    public double getWeights() {
        return weights;
    }

    public void setWeights(double weights) {
        this.weights = weights;
    }

    public String getRemark_s() {
        return remark_s;
    }

    public void setRemark_s(String remark_s) {
        this.remark_s = remark_s;
    }

    public String getRemark_p() {
        return remark_p;
    }

    public void setRemark_p(String remark_p) {
        this.remark_p = remark_p;
    }

    public Boolean getVerifyeva() {
        return verifyeva;
    }

    public void setVerifyeva(Boolean verifyeva) {
        this.verifyeva = verifyeva;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
    public List<Evaluate_appendix> getAppendixList() {
        return appendixList;
    }

    public void setAppendixList(List<Evaluate_appendix> appendixList) {
        this.appendixList = appendixList;
    }
}
