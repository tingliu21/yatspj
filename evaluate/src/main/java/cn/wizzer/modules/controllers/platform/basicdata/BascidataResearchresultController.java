package cn.wizzer.modules.controllers.platform.basicdata;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.basicdata.Bascidata_researchresult;
import cn.wizzer.modules.services.basicdata.BascidataResearchresultService;
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
@At("/platform/basicdata/researchresult")
@Filters({@By(type = PrivateFilter.class)})
public class BascidataResearchresultController {
	private static final Log log = Logs.get();
	@Inject
	private BascidataResearchresultService bascidataResearchresultService;

	@At("")
	@Ok("beetl:/platform/basicdata/researchresult/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return bascidataResearchresultService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/basicdata/researchresult/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Bascidata_researchresult", msg = "")
    public Object addDo(@Param("..") Bascidata_researchresult bascidataResearchresult, HttpServletRequest req) {
		try {
			bascidataResearchresultService.insert(bascidataResearchresult);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/basicdata/researchresult/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return bascidataResearchresultService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Bascidata_researchresult", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Bascidata_researchresult bascidataResearchresult, HttpServletRequest req) {
		try {

			bascidataResearchresult.setOpAt((int) (System.currentTimeMillis() / 1000));
			bascidataResearchresultService.updateIgnoreNull(bascidataResearchresult);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Bascidata_researchresult", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				bascidataResearchresultService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				bascidataResearchresultService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/basicdata/researchresult/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return bascidataResearchresultService.fetch(id);

		}
		return null;
    }

}
