package cn.wizzer.modules.models.evaluate;

import cn.wizzer.common.base.Model;
import cn.wizzer.modules.models.monitor.Monitor_index;
import org.nutz.dao.entity.annotation.*;
import java.io.Serializable;

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
    @Comment("自评价")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String remark_s;

    @Column
    @Comment("专家打分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score_p;

    @Column
    @Comment("专家评价")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String remark_p;

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
}
