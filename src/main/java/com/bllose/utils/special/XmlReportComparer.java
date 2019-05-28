package com.bllose.utils.special;

import com.bllose.format.DomHandler;
import com.bllose.io.FileHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @Author Bllose
 * @Date 2019/5/26 10:02
 */
public class XmlReportComparer {

    private static Logger logger = LogManager.getLogger(XmlReportComparer.class);

    public static void main(String[] args){
         if(args.length<1){
            logger.error("Work direction should be set!\r\nlike : java -jar *.jar /opt/work/direction");
            return;
         }

        File localDir = new File(args[0]);
         if(!localDir.isDirectory() || null == localDir.list() || localDir.list().length == 0){
             logger.error("The direction not exist, or is Empty!");
             return;
         }

         String encoding = "utf-8";
         if(args.length >= 2){
             encoding = args[1];
             logger.info("Set the Encoding:{}", encoding);
         }

         File[] xmlFiles = localDir.listFiles();

         compareXmlFiles(xmlFiles, encoding);
    }

    /**
     * <pre>
     *     1、首先读取所有文件，保存好文件名
     *     2、获取每个文件的内容List
     *     3、遍历所有List，将统一存在的数值保存到单独的List中。同时统计出最大长度。
     *     4、按照最大长度创建表头
     *     5、遍历所有List，将内容打印出来
     * </pre>
     * @Author Bllose
     * @Date 2019/5/26 10:21
     */
    private static void compareXmlFiles(File[] xmlFiles, String encoding) {
        List<File> xmlReportList = new LinkedList<>();
        List<String> xmlNameList = new LinkedList<>();

        /*
         * 由于可能存在被忽略的文件，那么xml的List可能接受到xmlFiles中的第2个，第3个文件
         * 从而导致LinkedList越界
         * @Author Bllose
         * @Date 2019/5/27 9:57
         */
        int cout = 0;
        for(int i = 0 ; i < xmlFiles.length ; i ++){
            File report = xmlFiles[i];

            if(report.getName().endsWith("xml")){
                xmlNameList.add(cout, report.getName());
                xmlReportList.add(cout, report);
                cout ++;
            }else{
                logger.warn("忽略非XML文件：{}", report.getName());
            }
        }

        compareXmlElements(xmlNameList, xmlReportList, encoding);
    }

    /**
     * 具体比较内容节点，同时将相同的节点全部清理到另外一个列表中，得到字段最大长度
     * @Author Bllose
     * @Date 2019/5/26 10:34
     */
    private static void compareXmlElements(List<String> xmlNameList, List<File> xmlReportList,
                                           String encoding) {

        int longest = 0;
        List<List<String>> reportContentList = new LinkedList<>();

        for(int i = 0 ; i < xmlReportList.size(); i ++){
            String fileContent = FileHandler.loadFile(xmlReportList.get(i).getAbsolutePath(), encoding);

            List<String> contentList = DomHandler.allElementName(
                    fileContent,"yyyy-MM-dd");
            logger.trace("Content be loaded from file : {}",fileContent);

            for(String content: contentList){
                if(content.length() > longest) longest = content.length();
            }

            reportContentList.add(i,contentList);
        }
        logger.trace("Element get form Files: {}", reportContentList);

        sameElementFilter(xmlNameList, reportContentList, longest);
    }

    /**
     * 将相同的元素抽取出来，留下特有的参数
     * @Author Bllose
     * @Date 2019/5/26 10:47
     */
    private static void sameElementFilter(List<String> xmlNameList, List<List<String>> reportContentList, int longest) {

        // 展示抬头
        String eachLine = "";
        for(String name : xmlNameList){
            eachLine += String.format("%-"+longest+"s",name);
        }
        System.out.println(eachLine);

        eachLine = "";
        // 展示日期
        for(List<String> contentList: reportContentList){
            eachLine += String.format("%-"+longest+"s",contentList.get(0));
        }
        System.out.println(eachLine);


        // 处理剩下的数据，将所有共有的参数剔除
        for(int i = 1; i < reportContentList.size() ; i ++){
            compare(reportContentList.get(i), reportContentList);
        }

        // 获取列表最深深度
        int count = getThelongestListLength(reportContentList);
//        List<String> longestList = reportContentList.get(theLongestList);
        for(int i = 1; i < count ; i ++){
            eachLine = "";
            for(List<String> content: reportContentList) {
                String txt = "";
                if (content.size() > i){
                    txt = content.get(i);
                }else{
                    txt = "";
                }
                eachLine += String.format("%-"+longest+"s",txt);
            }
            System.out.println(eachLine);
        }

        System.out.println("");
        System.out.println("所有相同的节点：" + theSameElements);
    }

    /**
     * 分别将每个列表都跟其他列表进行比较
     * @Author Bllose
     * @Date 2019/5/26 11:43
     */
    private static void compare(List<String> elements, List<List<String>> reportContentList) {
        final int total = reportContentList.size();

        for(String element: elements){
            int count = 0;
            if(element.startsWith("Earliest Time")) continue;
            for(List<String> content : reportContentList){
                if(content.contains(element)){
                    count ++;
                }else {
                    break;
                }
            }

            if(count == total){
                // 共同拥有的elemnt需要被剔除
                theSameElements.add(element);
            }
        }

        for(List<String> content: reportContentList) {
            for (String element : theSameElements) {
                content.remove(element);
            }
        }
    }

    private static int getThelongestListLength(List<List<String>> reportContentList) {
        int longestSize = 0;
        for(int i = 0; i < reportContentList.size() ; i ++){
            List contents = reportContentList.get(i);
            if(contents.size() > longestSize) {
                longestSize = contents.size();
                theLongestList = i;
            }
        }
        return longestSize;
    }

    // 拥有最多Elemt的List
    private static int theLongestList = 0;
    private static List<String> theSameElements = new ArrayList<>();
}
