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
    @Comment("建议评语")
    @ColDefine(type = ColType.VARCHAR, width = 2000)
    private String remark;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
