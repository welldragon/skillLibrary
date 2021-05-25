package cn.welldragon.skill.library;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Consumer;

public class JavaProjectClassificationUtil {

    @Test
    public void findAllSpringDependency() {
        File javaSrcDirFile = new File("../../../git_ke/helicarrier/jy-helicarrier-start/src/main/java");
        recursiveJavaFile(javaSrcDirFile, file -> {
            try {
//                System.out.println(file.getCanonicalPath());
                List<String> lines = Files.readLines(file, Charset.defaultCharset());
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if ("".equals(line)) continue;
                    if (line.startsWith("@Component")
                            || line.startsWith("@Controller")
                            || line.startsWith("@Repository")
                            || line.startsWith("@Service")
                    ) {
                        for (int j = i; j < lines.size(); j++) {
                            String nextLine = lines.get(j).trim();
                            if ("".equals(line)) continue;
                            if (nextLine.startsWith("public class ")) {
                                System.out.println(line);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void findAllImport() {
        Set<String> importSet = new TreeSet<>();
        List<String> ignorePrefixList = ImmutableList.of(
                "com.ke", "com.lianjia", "com.icbc"
                , "java.io", "java.awt", "java.text", "java.sql", "java.util" // JDK
                , "cn.smallbun.screw" // 数据库表结构文档生成工具
                , "com.actionsoft.bpms", "com.actionsoft.sdk" // ke流程中心
                , "com.alibaba.csp.sentinel" // sentinel 熔断降级
                , "com.alibaba.druid" // druid 数据库连接池
                , "com.alibaba.excel", "com.alibaba.easyexcel" // easyexcel Excel工具
                , "com.alibaba.fastjson" // fastjson json工具
                , "com.alibaba.dubbo" // dubbo RPC调用
        );
        for (String javaSrcDirPath : new String[]{
                "../../../git_ke"
        }) {
            findAllImportInDir(javaSrcDirPath, importSet, ignorePrefixList);
        }
        System.out.println("size: " + importSet.size());
        importSet.forEach(o -> System.out.println(o));
    }

    @SneakyThrows
    public void findAllImportInDir(String javaSrcDirPath, Set<String> importSet, List<String> ignorePrefixList) {
        File javaSrcDirFile = new File(javaSrcDirPath);
//        System.out.println(javaSrcDirFile.getCanonicalPath());
        recursiveJavaFile(javaSrcDirFile, file -> {
            try {
//                System.out.println(file.getCanonicalPath());
                List<String> lines = Files.readLines(file, Charset.defaultCharset());
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("import") && !line.startsWith("import static")) {
                        String[] words = line.split(" ");
                        String importClass = words[1].substring(0, words[1].length() - 1);
                        for (String ignorePrefix : ignorePrefixList) {
                            if (importClass.startsWith(ignorePrefix)) return;
                        }
                        importSet.add(importClass);
//                        System.out.println(line);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
