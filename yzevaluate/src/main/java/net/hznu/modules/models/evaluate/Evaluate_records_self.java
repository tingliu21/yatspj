package net.hznu.modules.models.evaluate;

import net.hznu.common.base.Model;
import net.hznu.modules.models.sys.Sys_unit;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by liuting on 2018/10/26.
 */
@Table("evaluate_records")
public class Evaluate_records_self extends Model implements Serializable{

    private static final long serialVersionUID = 1L;

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
    @Comment("学校ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String schoolId;

    @One(field = "schoolId")
    private Sys_unit school;

    @Column
    @Comment("权重")
    @ColDefine(type = ColType.INT, width = 3)
    private Integer weights;

    @Column
    @Comment("自评得分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score_s;

    @Column
    @Comment("自评进度")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private float progress_s;

    @Column
    @Comment("自评状态")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean status_s;

    @Column
    @Comment("自评报告路径")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String selfevaurl;

    @Column
    @Comment("3年发展规划路径")
    @ColDefine(type = ColType.VARCHAR,width = 100)
    private String planurl;

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

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public Sys_unit getSchool() {
        return school;
    }

    public void setSchool(Sys_unit school) {
        this.school = school;
    }

    public Integer getWeights() {
        return weights;
    }

    public void setWeights(Integer weights) {
        this.weights = weights;
    }

    public double getScore_s() {
        return score_s;
    }

    public void setScore_s(double score_s) {
        this.score_s = score_s;
    }

    public float getProgress_s() {
        return progress_s;
    }

    public void setProgress_s(float progress_s) {
        this.progress_s = progress_s;
    }

    public boolean isStatus_s() {
        return status_s;
    }

    public void setStatus_s(boolean status_s) {
        this.status_s = status_s;
    }

    public String getSelfevaurl() {
        return selfevaurl;
    }

    public void setSelfevaurl(String selfevaurl) {
        this.selfevaurl = selfevaurl;
    }

    public String getPlanurl() {
        return planurl;
    }

    public void setPlanurl(String planurl) {
        this.planurl = planurl;
    }
}

