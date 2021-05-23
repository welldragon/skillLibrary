package cn.welldragon.skill.library;

import org.junit.Test;

import java.io.File;
import java.util.function.Consumer;

public class JavaProjectClassificationUtil {
    @Test
    public void test() {
        String javaSrcDirPath = "/Users/huangpeijie/Documents/git_ke/helicarrier/jy-helicarrier-start/src/main";
        File javaSrcDirFile = new File(javaSrcDirPath);
        recursiveJavaFile(javaSrcDirFile, file -> {
            System.out.println(file.getAbsolutePath());
        });
    }

    /**
     * 递归java跟目录，找到所有java文件
     *
     * @param pFile
     */
    public void recursiveJavaFile(File pFile, Consumer<File> javaFileconsumer) {
        if (pFile == null) return;
        if (pFile.getName().startsWith(".")) return;
        if (pFile.isDirectory()) {
            for (File file : pFile.listFiles()) {
                recursiveJavaFile(file, javaFileconsumer);
            }
        } else {
            if (pFile.getName().endsWith(".java")) {
                javaFileconsumer.accept(pFile);
            }
        }
    }
}
