package cn.wizzer.modules.models.evaluate;

import cn.wizzer.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * 学校自评附件
 */
@Table("evaluate_appendix")
public class Evaluate_appendix extends Model implements Serializable{
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
    @Comment("指标评价ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String remarkid;

    @Column
    @Comment("附件名称")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String apname;

    @Column
    @Comment("附件地址")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String apurl;

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

    public String getRemarkid() {
        return remarkid;
    }

    public void setRemarkid(String remarkid) {
        this.remarkid = remarkid;
    }

    public String getApname() {
        return apname;
    }

    public void setApname(String apname) {
        this.apname = apname;
    }

    public String getApurl() {
        return apurl;
    }

    public void setApurl(String apurl) {
        this.apurl = apurl;
    }
}
