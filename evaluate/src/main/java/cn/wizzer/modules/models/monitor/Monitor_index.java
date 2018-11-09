package cn.wizzer.modules.models.monitor;

import cn.wizzer.common.base.Model;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by liuting on 2018/10/23.
 */
@Table("monitor_index")
public class Monitor_index extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Prev(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("评估观测点名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("目标值")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String mvalue;

    @Column
    @Comment("权重")
    @ColDefine(type = ColType.INT, width = 3)
    private Integer weights;

    @Column
    @Comment("是否达标性指标")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean qualify;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db= DB.MYSQL,value = "SELECT IFNULL(MAX(location),0)+1 FROM monitor_index"),
            @SQL(db= DB.ORACLE,value = "SELECT COALESCE(MAX(location),0)+1 FROM monitor_index")
    })
    private Integer location;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String catalogId;

    @One(field = "catalogId")
    private Monitor_catalog catalog;

    @Column
    @Comment("学校类别")
    @ColDefine(type = ColType.VARCHAR, width = 3)
    private String unitType;

    @Column
    @Comment("评分细则")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String detail;

    @Column
    @Comment("评分公式")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String formula;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMvalue() {
        return mvalue;
    }

    public void setMvalue(String mvalue) {
        this.mvalue = mvalue;
    }

    public Integer getWeights() {
        return weights;
    }

    public void setWeights(Integer weights) {
        this.weights = weights;
    }

    public boolean isQualify() {
        return qualify;
    }

    public void setQualify(boolean qualify) {
        this.qualify = qualify;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public Monitor_catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Monitor_catalog catalog) {
        this.catalog = catalog;
    }
    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
