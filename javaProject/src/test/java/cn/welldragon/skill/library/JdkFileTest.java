package cn.welldragon.skill.library;

import lombok.SneakyThrows;
import org.junit.Test;

import java.io.File;

public class JdkFileTest {
    @Test
    @SneakyThrows
    public void test() {
        File file = new File("/Users/huangpeijie/Desktop");
        System.out.println(file.getName());
        System.out.println(file.getParent());

        System.out.println(file.getAbsolutePath());
        System.out.println(file.getCanonicalPath());
    }
}
