package org.example;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.lang.reflect.Field;
import java.rmi.NoSuchObjectException;
import java.util.*;

public class BeanFactory {
    /**
     * 将加载到的bean缓存在内存中
     */
    public final static Map<String,Object> CACHE = new HashMap<>();

    /**
     * 解析xml文件创建Bean
     */
    public static void createBean(){
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read("src/main/resources/spring.xml");
            //得到根标签
            Element rootElement = document.getRootElement();
            //TODO 配置bean的包扫描 暂时未完善 后续有时间加上这个功能

            //获取beans标签
            Element beans = rootElement.element("beans");
            //获取beans下的bean标签
            List<Element> beanList = beans.elements("bean");
            //遍历beanList
            for (Element bean : beanList) {
                //id
                String id = bean.attribute("id").getValue();
                //class
                String className = bean.attribute("class").getValue();
                Class<?> beanClass = Class.forName(className);
                Object obj = beanClass.newInstance();
                //遍历读取property标签 并根据字段名及配置内容 注入属性值
                for (Element property : bean.elements("property")) {
                    Attribute nameAttr = property.attribute("name");
                    String fieldName = null;
                    //字段名一定不能为空 否则无法通过反射注入属性值 所以这里字段名为空就给一个异常
                    if(nameAttr != null){
                        fieldName = nameAttr.getValue();
                    }else{
                        throw new NoSuchFieldException();
                    }
                    //直接使用getField会异常 因为没有访问权限 getDeclaredField可以获取私有字段
                    Field field = beanClass.getDeclaredField(fieldName);
                    //设置私有字段为可访问权限
                    field.setAccessible(true);

                    Attribute ref = property.attribute("ref");
                    String refName = null;
                    if ( ref != null){
                        refName = ref.getValue();
                        //如果有ref属性 但是内容为空
                        if (refName.isEmpty()){
                            throw new ClassNotFoundException();
                        }
                    }

                    //如果不是ref 就是基本类型或string
                    if (refName == null){
                        String text = null;
                        //由于不确定是什么类型 所以用Object类 具体set进去的是什么类型 那么运行时就是什么类型
                        Object value = null;
                        //判断值写在标签内 还是value属性上
                        if (property.attribute("value") != null){
                            text = property.attribute("value").getValue();
                        }else{
                            text = property.getText();
                        }
                        // TODO 检查配置的text值是否合法
                        //判断字段的类型 根据反射获取字段的Class类型
                        Class<?> type =  field.getType();
                        if (type == int.class){
                            value = Integer.valueOf(text);
                        }else if (type == long.class){
                            value = Long.valueOf(text);
                        }else if (type == double.class){
                            value = Double.valueOf(text);
                        }else if (type == float.class){
                            value = Float.valueOf(text);
                        }else if(type == String.class){
                            value = property.getText();
                        }
                        field.set(obj,value);
                    }else{//todo 注入引用类型
                        field.set(obj,getBean(refName));
                    }
                }
                //加入到缓存
                CACHE.put(id,obj);
            }
        } catch (DocumentException | IllegalAccessException | InstantiationException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Object getBean(String id){
        return CACHE.get(id);
    }

    public static <T> T getBean(String id, Class<T> clazz){
        return clazz.cast(CACHE.get(id));
    }
}

