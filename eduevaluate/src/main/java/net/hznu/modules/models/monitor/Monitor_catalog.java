package net.hznu.modules.models.monitor;

import net.hznu.common.base.Model;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;


/**
 * Created by liuting on 2018/10/23.
 */
@Table("monitor_catalog")
public class Monitor_catalog extends Model implements Serializable{
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("父级ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String parentId;

    @Column
    @Comment("指标体系代码")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String catacode;


    @Column
    @Comment("指标名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("打开方式")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String target;

    @Column
    @Comment("是否图表显示")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean isShow;

    @Column
    @Comment("有子节点")
    private boolean hasChildren;
    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db= DB.MYSQL,value = "SELECT IFNULL(MAX(location),0)+1 FROM monitor_catalog"),
            @SQL(db= DB.ORACLE,value = "SELECT COALESCE(MAX(location),0)+1 FROM monitor_catalog")
    })
    private Integer location;

    @Column
    @Comment("指标等级")
    @ColDefine(type=ColType.INT)
    private int level;

    @Column
    @Comment("权重")
    @ColDefine(type = ColType.FLOAT, width = 3)
    private double weights;
    @Column
    @Comment("评估年度")
    @ColDefine(type=ColType.INT)
    private int year;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCatacode() {
        return catacode;
    }

    public void setCatacode(String catacode) {
        this.catacode = catacode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    public double getWeights() {
        return weights;
    }

    public void setWeights(double weights) {
        this.weights = weights;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
