package cn.wizzer.modules.models.evaluate;

import cn.wizzer.common.base.Model;
import cn.wizzer.modules.models.monitor.Monitor_index;
import org.nutz.dao.entity.annotation.*;
import java.io.Serializable;
import java.util.List;

@Table("evaluate_remark")
@View("evaluate_remark_view")
public class Evaluate_remark extends Model implements Serializable{
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
    @Comment("指标ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String indexId;

    @One(field = "indexId")
    private Monitor_index index;

    @Column
    @Comment("自打分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score_s;


    @Column
    @Comment("自评理由")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String remark_s;

    @Column
    @Comment("专家打分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score_p;

    @Column
    @Comment("满分/扣分理由")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String remark_p;

    @Column
    @Comment("优势")
    @ColDefine(type = ColType.TEXT)
    private String advantage;

    @Column
    @Comment("不足")
    @ColDefine(type = ColType.TEXT)
    private String disadvantage;

    @Column
    @Comment("建议")
    @ColDefine(type = ColType.TEXT)
    private String suggestion;

    @Column
    @Comment("是否自评完成")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean selfeva = null;

    @Column
    @Comment("是否审核完成")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean verifyeva = null;

    @Column
    @Readonly
    private String specialid;
    @Column
    @Readonly
    private String department;
    @Column
    @Readonly
    private String depttype;
    @Column
    @Readonly
    private int location;
    @Column
    @Readonly
    private String indexname;
    @Column
    @Readonly
    private double weights;
    @Column
    @Readonly
    private String detail;

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

    public Monitor_index getIndex() {
        return index;
    }

    public void setIndex(Monitor_index index) {
        this.index = index;
    }

    public String getIndexid() {
        return indexId;
    }

    public void setIndexid(String indexid) {
        this.indexId = indexid;
    }

    public double getScore_s() {
        return score_s;
    }

    public void setScore_s(double score_s) {
        this.score_s = score_s;
    }

    public String getRemark_s() {
        return remark_s;
    }

    public void setRemark_s(String remark_s) {
        this.remark_s = remark_s;
    }

    public double getScore_p() {
        return score_p;
    }

    public void setScore_p(double score_p) {
        this.score_p = score_p;
    }

    public String getRemark_p() {
        return remark_p;
    }

    public void setRemark_p(String remark_p) {
        this.remark_p = remark_p;
    }

    public Boolean getSelfeva() {
        return selfeva;
    }

    public void setSelfeva(Boolean selfeva) {
        this.selfeva = selfeva;
    }

    public Boolean getVerifyeva() {
        return verifyeva;
    }

    public void setVerifyeva(Boolean verifyeva) {
        this.verifyeva = verifyeva;
    }

    public String getAdvantage() {
        return advantage;
    }

    public void setAdvantage(String advantage) {
        this.advantage = advantage;
    }

    public String getDisadvantage() {
        return disadvantage;
    }

    public void setDisadvantage(String disadvantage) {
        this.disadvantage = disadvantage;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSpecialid() {
        return specialid;
    }

    public void setSpecialid(String specialid) {
        this.specialid = specialid;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepttype() {
        return depttype;
    }

    public void setDepttype(String depttype) {
        this.depttype = depttype;
    }
    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
    public String getIndexname() {
        return indexname;
    }

    public void setIndexname(String indexname) {
        this.indexname = indexname;
    }

    public double getWeights() {
        return weights;
    }

    public void setWeights(double weights) {
        this.weights = weights;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<Evaluate_appendix> getAppendixList() {
        return appendixList;
    }

    public void setAppendixList(List<Evaluate_appendix> appendixList) {
        this.appendixList = appendixList;
    }
}
