package net.hznu.modules.models.basicdata;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_moraleducation")

public class Bascidata_moraleducation extends Model implements Serializable{

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
    @Comment("项目类别")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String category;

    @Column
    @Comment("获奖交流或报道时间")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String awardtime;

    @Column
    @Comment("授奖交流或媒体报道层次")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String awardlevel;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String remark;

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

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAwardtime() {
        return awardtime;
    }

    public void setAwardtime(String awardtime) {
        this.awardtime = awardtime;
    }

    public String getAwardlevel() {
        return awardlevel;
    }

    public void setAwardlevel(String awardlevel) {
        this.awardlevel = awardlevel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
