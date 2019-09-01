package net.hznu.modules.models.evaluate;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by liuting on 2018/12/21.
 */
//评估专家分配
@Table("evaluate_special")
public class Evaluate_special extends Model implements Serializable {
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
    private Evaluate_records evaluateRecords;

    @Column
    @Comment("专家ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String specialId;

    @Column
    @Comment("指标ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String indexId;

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

    public String getSpecialId() {
        return specialId;
    }

    public void setSpecialId(String specialId) {
        this.specialId = specialId;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public Evaluate_records getEvaluateRecords() {
        return evaluateRecords;
    }

    public void setEvaluateRecords(Evaluate_records evaluateRecords) {
        this.evaluateRecords = evaluateRecords;
    }
}
