package net.hznu.modules.models.evaluate;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
/**
 * 基础性指标自评概述
 */
@Table("evaluate_summary")
public class Evaluate_summary extends Model implements Serializable{
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
    private String evaluateid;

    @Column
    @Comment("目录ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String catalogid;

    @Column
    @Comment("自评概述")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String summary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvaluateid() {
        return evaluateid;
    }

    public void setEvaluateid(String evaluateid) {
        this.evaluateid = evaluateid;
    }

    public String getCatalogid() {
        return catalogid;
    }

    public void setCatalogid(String catalogid) {
        this.catalogid = catalogid;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
