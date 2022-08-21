package org.example;

import org.example.bean.User;

import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        BeanFactory.createBean();
        //查看创建的bean实例
        showBeans();

        //通过bean的id获取
        User user1 = (User) BeanFactory.getBean("user");
        System.out.println("user1:"+user1.getClass());
        //通过bean的id和Class模板获取 指定返回类型为传入的Class模板类型 省一次强制转换的动作 在方法内部已经实现类型转换
        User user2 = BeanFactory.getBean("user", User.class);
        System.out.println("user2:"+user2.getClass());
    }

    public static void showBeans(){
        System.out.println("------------IOC容器------------");
        //查看创建的bean
        for (Map.Entry<String, Object> cur : BeanFactory.CACHE.entrySet()) {
            System.out.println("beanName:"+cur.getKey()+";"+cur.getValue().toString());
        }
        System.out.println("-------------------------------");
    }
}
