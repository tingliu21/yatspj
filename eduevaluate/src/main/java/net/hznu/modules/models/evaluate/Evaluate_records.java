package net.hznu.modules.models.evaluate;

import net.hznu.common.base.Model;
import net.hznu.modules.models.sys.Sys_unit;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by liuting on 2019/8/14.
 */
@Table("evaluate_records")
@View("evaluate_record_view")
public class Evaluate_records  extends Model implements Serializable {

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("学年")
    @ColDefine(type = ColType.INT)
    private int year;

    @Column
    @Comment("单位ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitID;

    @One(field = "unitID")
    private Sys_unit unit;
    @Column
    @Comment("评估得分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score;

    @Column
    @Comment("评估状态")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean status;

    @Column
    @Readonly
    private String unitcode;
    @Column
    @Readonly
    private String xzqhmc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public Sys_unit getUnit() {
        return unit;
    }

    public void setUnit(Sys_unit unit) {
        this.unit = unit;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUnitcode() {
        return unitcode;
    }

    public void setUnitcode(String unitcode) {
        this.unitcode = unitcode;
    }

    public String getXzqhmc() {
        return xzqhmc;
    }

    public void setXzqhmc(String xzqhmc) {
        this.xzqhmc = xzqhmc;
    }
}
