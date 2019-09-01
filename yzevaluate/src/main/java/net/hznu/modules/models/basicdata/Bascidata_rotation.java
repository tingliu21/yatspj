package net.hznu.modules.models.basicdata;

import net.hznu.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_rotation")

public class Bascidata_rotation extends Model implements Serializable{

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
    @Comment("交流轮岗教师姓名")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String teachername;

    @Column
    @Comment("交流时间")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String communicatetime;

    @Column
    @Comment("交流总数")
    @ColDefine(type = ColType.INT)
    private int totalnum;

    @Column
    @Comment("符合交流教师数")
    @ColDefine(type = ColType.INT)
    private int compliantnum;

    @Column
    @Comment("占比")
    @ColDefine(type = ColType.FLOAT, width = 8)
    private double percent;

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

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getCommunicatetime() {
        return communicatetime;
    }

    public void setCommunicatetime(String communicatetime) {
        this.communicatetime = communicatetime;
    }

    public int getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(int totalnum) {
        this.totalnum = totalnum;
    }

    public int getCompliantnum() {
        return compliantnum;
    }

    public void setCompliantnum(int compliantnum) {
        this.compliantnum = compliantnum;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
