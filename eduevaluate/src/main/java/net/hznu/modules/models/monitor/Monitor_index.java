package net.hznu.modules.models.monitor;

import net.hznu.common.base.Model;
import net.hznu.modules.models.sys.Sys_role;
import net.hznu.modules.models.sys.Sys_unit;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by liuting on 2018/10/23.
 */
@Table("monitor_index")
@View("monitor_index_view")
public class Monitor_index extends Model implements Serializable {
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
    @Comment("监测点代码")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String code;
    @Column
    @Comment("监测点名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("目标值")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String mvalue;

    @Column
    @Comment("权重")
    @ColDefine(type = ColType.FLOAT, width = 3)
    private double weights;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db= DB.MYSQL,value = "SELECT IFNULL(MAX(location),0)+1 FROM monitor_index"),
            @SQL(db= DB.ORACLE,value = "SELECT COALESCE(MAX(location),0)+1 FROM monitor_index")
    })
    private Integer location;
    @Column
    @Comment("监测点等级")
    @ColDefine(type=ColType.INT)
    private int level;
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String catalogId;

    @One(field = "catalogId")
    private Monitor_catalog catalog;

    @Column
    @Comment("有子节点")
    private boolean hasChildren;

    @Column
    @Comment("评分细则")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String detail;

    @Column
    @Comment("评分公式")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String formula;
    @Column
    @Comment("送审部门")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String department;
    @One(field = "department")
    private Sys_unit dept;


    @Column
    @Comment("送审专家角色")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String masterrole;
    @One(field = "masterrole")
    private Sys_role masterRole;

    @Column
    @Readonly
    private String masterrolename;
    @Column
    @Readonly
    private String deptname;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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

    public double getWeights() {
        return weights;
    }

    public void setWeights(double weights) {
        this.weights = weights;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


    public Sys_unit getDept() {
        return dept;
    }

    public void setDept(Sys_unit dept) {
        this.dept = dept;
    }

    public String getMasterrole() {
        return masterrole;
    }

    public void setMasterrole(String masterrole) {
        this.masterrole = masterrole;
    }

    public Sys_role getMasterRole() {
        return masterRole;
    }

    public void setMasterRole(Sys_role masterRole) {
        this.masterRole = masterRole;
    }

    public String getMasterrolename() {
        return masterrolename;
    }

    public void setMasterrolename(String masterrolename) {
        this.masterrolename = masterrolename;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
