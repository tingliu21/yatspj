package net.hznu.modules.models.basicdata;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_teachcontest")

public class Bascidata_teachcontest extends Model implements Serializable{

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
    @Comment("获奖级别与等级")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String awardname;

    @Column
    @Comment("获奖教师")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String awardteacher;

    @Column
    @Comment("获奖时间")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String awardtime;

    @Column
    @Comment("授奖部门")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String awarddapartment;

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

    public String getAwardname() {
        return awardname;
    }

    public void setAwardname(String awardname) {
        this.awardname = awardname;
    }

    public String getAwardteacher() {
        return awardteacher;
    }

    public void setAwardteacher(String awardteacher) {
        this.awardteacher = awardteacher;
    }

    public String getAwardtime() {
        return awardtime;
    }

    public void setAwardtime(String awardtime) {
        this.awardtime = awardtime;
    }

    public String getAwarddapartment() {
        return awarddapartment;
    }

    public void setAwarddapartment(String awarddapartment) {
        this.awarddapartment = awarddapartment;
    }
}
