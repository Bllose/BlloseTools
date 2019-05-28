package com.bllose.format;

import org.dom4j.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <pre>
 * 提供处理 Document类型数据 的功能
 * </pre>
 *
 * @Author Bllose
 * @Date 2019/5/26 4:40
 */
public class DomHandler {

    /**
     * 获取所有元素
     * @Author Bllose
     * @Date 2019/5/26 4:52
     * @return 所有元素名称的列表
     */
    public static List<String> allElementName(String documentXml, String timeShouldAnaly){
        // 需要初始化。因为每次进入该方法，都是针对新的XML报告
        // 而如果不初始化，会导致上一个报告的时间影响本次的最小时间
        smallestTime = "";

        try {
            timeRegex = analyTimeRegex(timeShouldAnaly);

            Document document = DocumentHelper.parseText(documentXml);

            return treeWalk(document, timeShouldAnaly);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String analyTimeRegex(String timeShouldAnaly) {
        switch(timeShouldAnaly){
            case "yyyy-MM-dd": return "\\d{4}-\\d{2}-\\d{2}";
            case "yyyy-MM-dd HH:mm:ss" : return "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        }

        return "";
    }

    private static List<String> treeWalk(Document document, String timeShouldAnaly) {
        List<String> elementTree = new LinkedList<>();
        treeWalk(document.getRootElement(), document.getRootElement().getName(), elementTree, timeShouldAnaly);

        if(!"".equals(smallestTime)) {
            elementTree.add(0,"Earliest Time:" + smallestTime);
        }else{
            elementTree.add(0,"Earliest Time does not Exist!");
        }

        return elementTree;
    }

    private static void treeWalk(Element element, String key, List<String> elementTree, String timeShouldAnaly) {

        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);

            if (node instanceof Element) {
                treeWalk((Element) node, key + "." +node.getName(), elementTree, timeShouldAnaly);
            }
            else {
                // 将不重复的参数插入列表中
                if(!elementTree.contains(key)) elementTree.add(key);
                // 识别日期，尝试拿到最小日期
                if(null != timeShouldAnaly && !"".equals(timeShouldAnaly.trim()))
                    analyTimeTarget(node, timeShouldAnaly);
            }
        }

    }

    private static void analyTimeTarget(Node node, String timeShouldAnaly) {
        String value = node.getStringValue();
        if(null == value) return;

        value = value.trim();
        if("".equals(value)) return;

        if( Pattern.matches(timeRegex, value)){
            if("".equals(smallestTime)){
                smallestTime = value;
            }else if(theValueSmaller(value,timeShouldAnaly)){
                smallestTime = value;
            }
        }


    }

    private static boolean theValueSmaller(String value, String timeShouldAnaly) {
//        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(timeShouldAnaly);
        try {
            long targetTime = sdf.parse(value).getTime();
            long smallestTimeL = sdf.parse(smallestTime).getTime();

            if(targetTime < smallestTimeL) return true;
        } catch (ParseException e) {
            e.printStackTrace();

        }

        return false;
//        String date = simpleDateFormat.format(new Date());
//        System.out.println(date);
    }

    private static String smallestTime = "";
    private static String timeRegex = "";
}
