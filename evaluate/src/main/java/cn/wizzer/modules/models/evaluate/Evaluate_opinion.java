package cn.wizzer.modules.models.evaluate;

import cn.wizzer.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * 专家意见建议类
 */
@Table("evaluate_opinion")
public class Evaluate_opinion  extends Model implements Serializable {
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
    @Comment("专家ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String specialId;
    @Column
    @Comment("概述")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String intro;

    @Column
    @Comment("办学水平")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String ability;
    @Column
    @Comment("优势与亮点")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String advantage;
    @Column
    @Comment("问题")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String problem;
    @Column
    @Comment("建议")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String suggestion;

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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public String getAdvantage() {
        return advantage;
    }

    public void setAdvantage(String advantage) {
        this.advantage = advantage;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
