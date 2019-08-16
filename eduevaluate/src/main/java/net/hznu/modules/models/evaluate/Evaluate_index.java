package net.hznu.modules.models.evaluate;

import net.hznu.common.base.Model;
import net.hznu.modules.models.monitor.Monitor_index;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;


@Table("evaluate_index")
@View("evaluate_index_view")
public class Evaluate_index extends Model implements Serializable{
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
    @Comment("监测点代码")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String code;

    @Column
    @Comment("评估值")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double value;
    @Column
    @Comment("评估值")
    @ColDefine(type = ColType.VARCHAR, width = 8)
    private String svalue;

    @Column
    @Comment("评估分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score;

    @Column
    @Readonly
    private int location;
    @Column
    @Readonly
    private String indexname;

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

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSvalue() {
        return svalue;
    }

    public void setSvalue(String svalue) {
        this.svalue = svalue;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
