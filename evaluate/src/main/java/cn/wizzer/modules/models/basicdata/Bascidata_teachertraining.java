package cn.wizzer.modules.models.basicdata;

import cn.wizzer.common.base.Model;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by shehailong on 2018/12/2.
 */
@Table("bascidata_teachertraining")

public class Bascidata_teachertraining extends Model implements Serializable{

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
    @Comment("类别")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String category;

    @Column
    @Comment("培训内容")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String trainingcontent;

    @Column
    @Comment("起讫时间")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String startingtime;

    @Column
    @Comment("培训范围")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String trainingscale;

    @Column
    @Comment("培训学时")
    @ColDefine(type = ColType.INT)
    private int trainingcycle;

    @Column
    @Comment("参加人数")
    @ColDefine(type = ColType.INT)
    private int participantnum;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTrainingcontent() {
        return trainingcontent;
    }

    public void setTrainingcontent(String trainingcontent) {
        this.trainingcontent = trainingcontent;
    }

    public String getStartingtime() {
        return startingtime;
    }

    public void setStartingtime(String startingtime) {
        this.startingtime = startingtime;
    }

    public String getTrainingscale() {
        return trainingscale;
    }

    public void setTrainingscale(String trainingscale) {
        this.trainingscale = trainingscale;
    }

    public int getTrainingcycle() {
        return trainingcycle;
    }

    public void setTrainingcycle(int trainingcycle) {
        this.trainingcycle = trainingcycle;
    }

    public int getParticipantnum() {
        return participantnum;
    }

    public void setParticipantnum(int participantnum) {
        this.participantnum = participantnum;
    }
}
