package net.hznu.modules.models.evaluate;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by liuting on 2019/8/23.
 */
@Table("evaluate_special")

public class Evaluate_special extends Model implements Serializable {
    @Column
    @Name
    @Comment("评估ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String evaluateId;

    @Column
    @Comment("专家ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String specialId;

    @Column
    @Comment("一级指标达成情况")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String remark1;
    @Column
    @Comment("二级指标达成情况")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String remark2;
    @Column
    @Comment("监测点达成情况")
    @ColDefine(type = ColType.VARCHAR, width = 1000)
    private String remarkp;
    @Column
    @Comment("建议评语")
    @ColDefine(type = ColType.VARCHAR, width = 2000)
    private String suggestion;

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

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark) {
        this.remark1 = remark;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getRemarkp() {
        return remarkp;
    }

    public void setRemarkp(String remarkp) {
        this.remarkp = remarkp;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
