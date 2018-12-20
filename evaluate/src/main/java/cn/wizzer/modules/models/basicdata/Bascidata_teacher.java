package cn.wizzer.modules.models.basicdata;

import cn.wizzer.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_teacher")

public class Bascidata_teacher extends Model implements Serializable{

    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("项目")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String projectname;

    @Column
    @Comment("数据")
    @ColDefine(type = ColType.INT)
    private int provalue;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 300)
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

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
    }

    public int getProvalue() {
        return provalue;
    }

    public void setProvalue(int provalue) {
        this.provalue = provalue;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
