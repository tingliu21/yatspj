package cn.wizzer.modules.models.basicdata;

import cn.wizzer.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_studenthonor")

public class Bascidata_studenthonor extends Model implements Serializable{

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
    @Comment("项目名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String projectname;

    @Column
    @Comment("获奖学生")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String studentname;

    @Column
    @Comment("获奖时间")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String awardtime;

    @Column
    @Comment("授奖部门")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String awarddepartment;

    @Column
    @Comment("辅导教师")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String teachername;

    @Column
    @Comment("学校ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String schoolID;

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

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
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

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }
}
