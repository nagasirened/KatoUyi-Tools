package com.katouyi.tools.exercise.test;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.alibaba.fastjson.JSON;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CSVReaderTest {

    public static void main(String[] args) {
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file("/Users/katouyi/Downloads/80s.csv"));
        List<CsvRow> rows = data.getRows();
        Set<String> yearSet = rows.stream().skip(1)
                .map(item -> item.get(1)).collect(Collectors.toSet());

        Set<String> jinshuSet = reader.read(FileUtil.file("/Users/katouyi/Downloads/jinshu.csv"))
                .getRows().stream().skip(1)
                .map(item -> item.get(1)).collect(Collectors.toSet());

        Set<String> workSet = reader.read(FileUtil.file("/Users/katouyi/Downloads/work.csv"))
                .getRows().stream().skip(1)
                .map(item -> item.get(1)).collect(Collectors.toSet());

        Set<String> xingfenSet = reader.read(FileUtil.file("/Users/katouyi/Downloads/xingfen.csv"))
                .getRows().stream().skip(1)
                .map(item -> item.get(1)).collect(Collectors.toSet());

        System.out.println("80s: " + yearSet.size());
        System.out.println("jinshu: " + jinshuSet.size());
        System.out.println("work: " + workSet.size());
        System.out.println("xingfen: " + xingfenSet.size());

        // 求交集
        Collection<String> c1 = CollUtil.intersection(workSet, jinshuSet);
        System.out.println("c1Size: " + c1.size());
        System.out.println(JSON.toJSONString(c1));
        Collection<String> c2 = CollUtil.intersection(c1, xingfenSet);
        System.out.println("c2Size: " + c2.size());
        Collection<String> c3 = CollUtil.intersection(c2, yearSet);
        System.out.println("resSize: " + c3.size());
        System.out.println(JSON.toJSONString(c3));

    }
}
