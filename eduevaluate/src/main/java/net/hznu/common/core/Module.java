package net.hznu.common.core;


import net.hznu.common.shiro.ShiroSessionProvider;
import org.beetl.ext.nutz.BeetlViewMaker;

import org.nutz.mvc.annotation.*;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.plugins.view.pdf.PdfViewMaker;


/**
 * Created by wizzer on 2016/6/21.
 */
@Modules(scanPackage = true, packages = "net.hznu.modules")
@Ok("json:full")
@Fail("http:500")
@IocBy(type = ComboIocProvider.class, args = {"*json", "config/ioc/", "*anno", "net.hznu", "*jedis", "*tx", "*quartz", "*async"})
@Localization(value = "locales/", defaultLocalizationKey = "zh_CN")
@Encoding(input = "UTF-8", output = "UTF-8")
@Views({BeetlViewMaker.class, PdfViewMaker.class})
@SetupBy(value = Setup.class)
@ChainBy(args = "config/chain/eduevaluate-mvc-chain.json")
@SessionBy(ShiroSessionProvider.class)
public class Module {
}
