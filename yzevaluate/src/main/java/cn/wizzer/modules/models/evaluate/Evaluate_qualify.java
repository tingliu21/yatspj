package cn.wizzer.modules.models.evaluate;

import cn.wizzer.common.base.Model;
import cn.wizzer.modules.models.monitor.Monitor_index;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by liuting on 2018/10/25.
 */
@Table("evaluate_qualify")
@View("evaluate_quality_view")
public class Evaluate_qualify extends Model implements Serializable {
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

    @One(field = "evaluateId")
    private Evaluate_records evaluate;

    @Column
    @Comment("指标ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String indexId;

    @One(field = "indexId")
    private Monitor_index index;

    @Column
    @Comment("实际值")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String pvalue;

    @Column
    @Comment("是否达标")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean qualify = null;

    @Column
    @Comment("是否自评完成")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean selfeva = null;

    @Column
    @Comment("是否审核完成")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean verifyeva = null;


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

    public Evaluate_records getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(Evaluate_records evaluate) {
        this.evaluate = evaluate;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public Monitor_index getIndex() {
        return index;
    }

    public void setIndex(Monitor_index index) {
        this.index = index;
    }

    public String getPvalue() {
        return pvalue;
    }

    public void setPvalue(String pvalue) {
        this.pvalue = pvalue;
    }

    public Boolean getQualify() {
        return qualify;
    }

    public void setQualify(Boolean qualify) {
        this.qualify = qualify;
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
}
