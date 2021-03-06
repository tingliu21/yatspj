package net.hznu.modules.models.sys;

import net.hznu.common.base.Model;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by wizzer on 2016/6/21.
 */
@Table("sys_unit")
@TableIndexes({@Index(name = "INDEX_SYS_UNIT_PATH", fields = {"path"}, unique = true)})
public class Sys_unit extends Model implements Serializable {
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
    @Comment("树路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String path;
    @Column
    @Comment("单位等级")
    @ColDefine(type=ColType.INT)
    private int level;
    @Column
    @Comment("单位名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("单位别名")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String aliasName;

    @Column
    @Comment("机构编码")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String unitcode;

    @Column
    @Comment("行政区名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String xzqhmc;

    @Column
    @Comment("单位地址")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String address;

    @Column
    @Comment("联系电话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String telephone;

    @Column
    @Comment("单位邮箱")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String email;

    @Column
    @Comment("单位网站")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String website;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db= DB.MYSQL,value = "SELECT IFNULL(MAX(location),0)+1 FROM sys_unit"),
            @SQL(db= DB.ORACLE,value = "SELECT COALESCE(MAX(location),0)+1 FROM sys_unit")
    })
    private Integer location;

    @Column
    @Comment("有子节点")
    private boolean hasChildren;
    @Column
    @Comment("是否评估")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean evaluate;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getUnitcode() {
        return unitcode;
    }

    public void setUnitcode(String unitcode) {
        this.unitcode = unitcode;
    }

    public String getXzqhmc() {
        return xzqhmc;
    }

    public void setXzqhmc(String xzqhmc) {
        this.xzqhmc = xzqhmc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }


    public boolean isEvaluate() {
        return evaluate;
    }

    public void setEvaluate(boolean evaluate) {
        this.evaluate = evaluate;
    }
}
