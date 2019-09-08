package net.hznu.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import net.hznu.common.chart.CustomStat;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XwpfUtil {
    /**
     * 导出word文件
     * @param params
     * @param is
     * @param request
     * @param response
     * @param xwpfUtil
     */
    public void exportWord(Map<String, Object> wordDataMap, InputStream is,
                           HttpServletResponse response, String filename) {
        try {

            OutputStream os = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition","attachment;filename="+filename);//文件名中文不显示

//            File file = new File("/Users/maofeng/Documents/GitHub/yatspj/evaluate/target/nutzwk/WEB-INF/classes/template/SelfEvaluate.docx");//改成你本地文件所在目录

            // 读取word模板
//            FileInputStream fileInputStream = new FileInputStream(file);
            WordTemplate template = new WordTemplate(is);

            // 替换数据
            template.replaceDocument(wordDataMap);

            template.getDocument().write(os);

            //关闭流
            close(os);
            close(is);
            os.flush();
            os.close();
        } catch (IOException e) {
//            logger.error("文件导出错误");
        }
    }

    /**
     * 正则匹配字符串
     * @param str
     * @return
     */
    public Object matcher(String str) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    /**
     * 关闭输入流
     * @param is
     */
    public void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     * @param os
     */
    public void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 替换word模板文档段落中的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    public void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();

        XWPFParagraph para;
        while(iterator.hasNext()){
            para = iterator.next();
            this.replaceInPara(para,params);

        }

    }

    /**
     * 替换段落中的变量
     * @param para 要替换的段落
     * @param params 替换参数
     */
    public void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        List<XWPFRun> runs;
        if (((Matcher) this.matcher(para.getParagraphText())).find()) {
            runs = para.getRuns();
            int start = -1;
            int end = -1;
            String str = "";
            for (int i = 0; i < runs.size(); i++) {
                XWPFRun run = runs.get(i);
                String runText = run.toString().trim();
                if (StringUtils.isNotBlank(runText)&&'$' == runText.charAt(0)) {
                    start = i;
                }
                if (StringUtils.isNotBlank(runText)&&(start != -1)) {
                    str += runText;
                }
                if (StringUtils.isNotBlank(runText)&&'}' == runText.charAt(runText.length() - 1)) {
                    if (start != -1) {
                        end = i;
                        break;
                    }
                }
            }
            for (int i = start; i <= end; i++) {
                para.removeRun(i);
                i--;
                end--;

            }
            if(StringUtils.isBlank(str)){
                String temp = para.getParagraphText();
                str = temp.trim().substring(temp.indexOf("${"),temp.indexOf("}")+1);
            }
            for(String key :params.keySet()){
                if(key.equals(str)){
                    Object oValue = params.get(str);
                    /*if(oValue instanceof StatChart){//图表添加
                        StatChart chart = (StatChart)oValue ;
                        try {

                            InputStream input = new FileInputStream(chart.getFilePath());

                            para.createRun().addPicture(input, getPictureType(chart.getFileType()),
                                    chart.getFilePath(), Units.toEMU(chart.getWidth()), Units.toEMU(chart.getHeight()));
                        } catch (InvalidFormatException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }else {//文本替换
                        String value = String.valueOf(oValue);
                        if(value!=""){
                            int iNewLine = value.indexOf("\n");
                            if(iNewLine!=-1){
                                XWPFRun run = para.createRun();
                                run.setText(value.substring(0,iNewLine));
                                run.addCarriageReturn();
                                run.addTab();
                                run.setText(value.substring(iNewLine,value.length()));

                            }else {
                                para.createRun().setText(value);
                            }
                        }
                    }*/


                    break;
                }
            }


        }
    }
}
