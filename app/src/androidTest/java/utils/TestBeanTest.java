package utils;

import org.junit.Test;

/**
 * Created by mroot on 2018/4/12.
 */
public class TestBeanTest {
    @Test
    public void object2xml() throws Exception {
        TestBean bean = new TestBean(25, "xiaoming");
        String xml = bean.object2xml();
        TestBean bean1 = TestBean.xml2obj(xml);

    }

}