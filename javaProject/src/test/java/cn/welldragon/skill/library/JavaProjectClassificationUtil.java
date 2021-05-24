package cn.welldragon.skill.library;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

public class JavaProjectClassificationUtil {
    @Test
    public void test() {
        String javaSrcDirPath = "/Users/huangpeijie/Documents/git_ke/helicarrier/jy-helicarrier-start/src/main";
        File javaSrcDirFile = new File(javaSrcDirPath);
        Set<String> importSet = new TreeSet<>();
        recursiveJavaFile(javaSrcDirFile, file -> {
            try {
//                System.out.println(file.getAbsolutePath());
                List<String> lines = Files.readLines(file, Charset.defaultCharset());
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("import")) {
                        importSet.add(line);
//                        System.out.println(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        importSet.forEach(o -> System.out.println(o));
    }

    /**
     * 递归java跟目录，找到所有java文件
     *
     * @param pFile
     */
    public void recursiveJavaFile(File pFile, Consumer<File> javaFileConsumer) {
        if (pFile == null) return;
        if (pFile.getName().startsWith(".")) return;
        if (pFile.isDirectory()) {
            for (File file : pFile.listFiles()) {
                recursiveJavaFile(file, javaFileConsumer);
            }
        } else {
            if (pFile.getName().endsWith(".java")) {
                javaFileConsumer.accept(pFile);
            }
        }
    }
}
