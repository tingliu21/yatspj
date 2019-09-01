package net.hznu.modules.models.basicdata;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_friendlyschool")

public class Bascidata_friendlyschool extends Model implements Serializable{

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
    @Comment("国家")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String nation;

    @Column
    @Comment("学校名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String schoolname;

    @Column
    @Comment("签订结好协议时间")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String agreementtime;

    @Column
    @Comment("交流次数")
    @ColDefine(type = ColType.INT)
    private int times;

    @Column
    @Comment("是否属于千校结好特色品牌项目")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean isproject;

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

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getAgreementtime() {
        return agreementtime;
    }

    public void setAgreementtime(String agreementtime) {
        this.agreementtime = agreementtime;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public boolean isIsproject() {
        return isproject;
    }

    public void setIsproject(boolean isproject) {
        this.isproject = isproject;
    }
}
