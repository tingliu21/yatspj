package cn.wizzer.modules.models.evaluate;

import cn.wizzer.common.base.Model;
import cn.wizzer.modules.models.sys.Sys_unit;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by liuting on 2018/10/26.
 */
@Table("evaluate_records")
public class Evaluate_records extends Model implements Serializable{

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
    private double progress_s;

    @Column
    @Comment("自评状态")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean status_s;

    @Column
    @Comment("评估得分")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double score_p;

    @Column
    @Comment("评估进度")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double progress_p;

    @Column
    @Comment("评估状态")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean status_p;

    @Column
    @Comment("评估任务")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String taskname;

    @Column
    @Comment("评估状态")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean locked;

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

    public double getProgress_s() {
        return progress_s;
    }

    public void setProgress_s(double progress_s) {
        this.progress_s = progress_s;
    }

    public boolean isStatus_s() {
        return status_s;
    }

    public void setStatus_s(boolean status_s) {
        this.status_s = status_s;
    }

    public double getScore_p() {
        return score_p;
    }

    public void setScore_p(double score_p) {
        this.score_p = score_p;
    }

    public double getProgress_p() {
        return progress_p;
    }

    public void setProgress_p(double progress_p) {
        this.progress_p = progress_p;
    }

    public boolean isStatus_p() {
        return status_p;
    }

    public void setStatus_p(boolean status_p) {
        this.status_p = status_p;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}

