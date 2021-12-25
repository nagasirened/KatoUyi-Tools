package com.katouyi.tools.netty.nio;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.LongAdder;

public class _006Paths_Files {

    public static void testPaths() {
        Path source0 = Paths.get("1.txt"); // 相对路径 不带盘符 使用 user.dir 环境变量来定位 1.txt
        Path source1 = Paths.get("d:\\1.txt"); // 绝对路径 代表了  d:\1.txt 反斜杠需要转义
        Path source2 = Paths.get("d:/1.txt"); // 绝对路径 同样代表了  d:\1.txt
        Path projects = Paths.get("d:\\data", "projects"); // 代表了  d:\data\projects

        Path path = Paths.get("d:\\data\\projects\\a\\..\\b");
        System.out.println(path);
        System.out.println(path.normalize());
        // 正常化路径 会去除 . 以及 ..   结果如下
        // d:\data\projects\a\..\b
        // d:\data\projects\b
    }

    public static void testFiles() throws IOException {
        // 检查文件是否存在
        Path path = Paths.get("helloword/data.txt");
        System.out.println(Files.exists(path));

        // 创建一级目录，如果已经存在 FileAlreadyExistsException （不能创建多级目录）
        Files.createDirectories(path);

        // 创建多级目录
        Files.createDirectories(path);


        // 文件拷贝
        Path source = Paths.get("helloword/data.txt");
        Path target = Paths.get("helloword/target.txt");
        Files.copy(source, target);
        // 覆盖拷贝
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        // 移动文件  StandardCopyOption.ATOMIC_MOVE 保证文件移动的原子性
        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);

        // 删除文件 或 目录，如果目录里面还有东西就会抛出异常 DirectoryNotEmptyException
        Files.delete(target);
    }

    // 遍历文件使用方法 walkFileTree(Path, FileVisitor)
    /*
    Path：文件起始路径
    FileVisitor：文件访问器，使用访问者模式
        接口的实现类SimpleFileVisitor有四个方法
            preVisitDirectory：访问目录前的操作
            visitFile：访问文件的操作
            visitFileFailed：访问文件失败时的操作
            postVisitDirectory：访问目录后的操作
     */
    public static void walkTree() throws IOException {
        Path path = Paths.get("F:\\JDK 8");
        // 文件目录数目
        LongAdder dirCount = new LongAdder();
        // 文件数目
        LongAdder fileCount = new LongAdder();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("===>"+dir);
                // 增加文件目录数
                dirCount.add(1);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                // 增加文件数
                fileCount.add(1);
                return super.visitFile(file, attrs);
            }
        });
        // 打印数目
        System.out.println("文件目录数:"+dirCount.intValue());
        System.out.println("文件数:"+fileCount.intValue());

    }

}
