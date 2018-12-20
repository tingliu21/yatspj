package cn.wizzer.modules.controllers.platform.basicdata;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.basicdata.Bascidata_researchsubject;
import cn.wizzer.modules.services.basicdata.BascidataResearchsubjectService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/basicdata/researchsubject")
@Filters({@By(type = PrivateFilter.class)})
public class BascidataResearchsubjectController {
	private static final Log log = Logs.get();
	@Inject
	private BascidataResearchsubjectService bascidataResearchsubjectService;

	@At("")
	@Ok("beetl:/platform/basicdata/researchsubject/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return bascidataResearchsubjectService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/basicdata/researchsubject/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Bascidata_researchsubject", msg = "")
    public Object addDo(@Param("..") Bascidata_researchsubject bascidataResearchsubject, HttpServletRequest req) {
		try {
			bascidataResearchsubjectService.insert(bascidataResearchsubject);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/basicdata/researchsubject/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return bascidataResearchsubjectService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Bascidata_researchsubject", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Bascidata_researchsubject bascidataResearchsubject, HttpServletRequest req) {
		try {

			bascidataResearchsubject.setOpAt((int) (System.currentTimeMillis() / 1000));
			bascidataResearchsubjectService.updateIgnoreNull(bascidataResearchsubject);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Bascidata_researchsubject", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				bascidataResearchsubjectService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				bascidataResearchsubjectService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/basicdata/researchsubject/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return bascidataResearchsubjectService.fetch(id);

		}
		return null;
    }

}
