package utils;

import android.util.Log;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

import global.Constant;

/**
 * Created by mroot on 2018/4/12.
 */
@XStreamAlias("TestBean")
public class TestBean {

    @XStreamAlias("myAge")
    private int age;
    @XStreamAlias("myName")
    private String name;

    public TestBean(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public String object2xml() {
        //设置xml字段顺序
        SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        sorter.registerFieldOrder(TestBean.class,
                new String[]{
                        "age",
                        "name"
                });
        //XStream xStream = new XStream(new DomDriver("UTF-8"));
        XStream xStream = new XStream(new Sun14ReflectionProvider(new FieldDictionary(sorter)));
        xStream.setMode(XStream.NO_REFERENCES);
        //使用注解
        xStream.processAnnotations(TestBean.class);
        //转化为String，并保存入文件
        String xml = xStream.toXML(this);
        Log.i(Constant.TEST_FLAG, xml);
        return xml;
    }

    public static TestBean xml2obj(String xml){
        XStream xStream = new XStream(new DomDriver("UTF-8"));
        //使用注解
        xStream.processAnnotations(TestBean.class);
        //这个blog标识一定要和Xml中的保持一直，否则会报错
        xStream.alias("TestBean", TestBean.class);
        TestBean testBean=(TestBean)xStream.fromXML(xml);
        return testBean;
    }
}
