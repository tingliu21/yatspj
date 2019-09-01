package net.hznu.modules.models.basicdata;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_researchsubject")

public class Bascidata_researchsubject extends Model implements Serializable{

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
    @Comment("课题名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String subjectname;

    @Column
    @Comment("课题组成员(负责人)")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String membercharge;

    @Column
    @Comment("课题组成员(本校成员)")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String memberschool;

    @Column
    @Comment("审批部门")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String approvaldepartment;

    @Column
    @Comment("起讫时间")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String startingtime;

    @Column
    @Comment("立项结题和获奖情况")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String status;

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

    public String getSubjectname() {
        return subjectname;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
    }

    public String getMembercharge() {
        return membercharge;
    }

    public void setMembercharge(String membercharge) {
        this.membercharge = membercharge;
    }

    public String getMemberschool() {
        return memberschool;
    }

    public void setMemberschool(String memberschool) {
        this.memberschool = memberschool;
    }

    public String getApprovaldepartment() {
        return approvaldepartment;
    }

    public void setApprovaldepartment(String approvaldepartment) {
        this.approvaldepartment = approvaldepartment;
    }

    public String getStartingtime() {
        return startingtime;
    }

    public void setStartingtime(String startingtime) {
        this.startingtime = startingtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
