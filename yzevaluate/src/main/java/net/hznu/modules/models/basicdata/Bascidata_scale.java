package net.hznu.modules.models.basicdata;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_scale")

public class Bascidata_scale extends Model implements Serializable{

    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("年级")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String grade;

    @Column
    @Comment("计划招生数")
    @ColDefine(type = ColType.INT)
    private int planenrollnum;

    @Column
    @Comment("实际学生数")
    @ColDefine(type = ColType.INT)
    private int actualenrollnum;

    @Column
    @Comment("班级数")
    @ColDefine(type = ColType.INT)
    private int classnum;

    @Column
    @Comment("班均人数")
    @ColDefine(type = ColType.INT)
    private int averagenum;

    @Column
    @Comment("需要说明的情况")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    private String instruction;

    @Column
    @Comment("年份")
    @ColDefine(type = ColType.INT)
    private int year;

    @Column
    @Comment("学校ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String schoolID;

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getPlanenrollnum() {
        return planenrollnum;
    }

    public void setPlanenrollnum(int planenrollnum) {
        this.planenrollnum = planenrollnum;
    }

    public int getActualenrollnum() {
        return actualenrollnum;
    }

    public void setActualenrollnum(int actualenrollnum) {
        this.actualenrollnum = actualenrollnum;
    }

    public int getClassnum() {
        return classnum;
    }

    public void setClassnum(int classnum) {
        this.classnum = classnum;
    }

    public int getAveragenum() {
        return averagenum;
    }

    public void setAveragenum(int averagenum) {
        this.averagenum = averagenum;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
