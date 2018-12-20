package cn.wizzer.modules.models.basicdata;

import cn.wizzer.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_boutiqueclass")
public class Bascidata_boutiqueclass extends Model implements Serializable{

    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("序号")
    @ColDefine(type = ColType.INT)
    private int num;

    @Column
    @Comment("级别")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String rank;

    @Column
    @Comment("课程名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String coursename;

    @Column
    @Comment("主讲教师")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String lecturer;

    @Column
    @Comment("获得时间")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String awardtime;

    @Column
    @Comment("授奖部门")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String awarddepartment;

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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getAwardtime() {
        return awardtime;
    }

    public void setAwardtime(String awardtime) {
        this.awardtime = awardtime;
    }

    public String getAwarddepartment() {
        return awarddepartment;
    }

    public void setAwarddepartment(String awarddepartment) {
        this.awarddepartment = awarddepartment;
    }
}
