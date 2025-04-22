package com.myuanxi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("开始测试 AiEntity 框架...\n");
        
        // 1. 基本功能测试
        testBasicFunctionality();
        
        // 2. 边界条件测试
        testEdgeCases();
        
        // 3. 错误处理测试
        testErrorHandling();
    }
    
    private static void testBasicFunctionality() {
        System.out.println("=== 基本功能测试 ===");
        try {
            // 1.1 测试创建单个对象
            System.out.println("1.1 测试创建单个对象：");
            String inputText = "创建一个名为张三的人，年龄30岁，职业是工程师";
            Person person = PersonAiFactory.createByString(inputText);
            System.out.println("结果：" + person);
            System.out.println();
            
            // 1.2 测试创建多个对象
            System.out.println("1.2 测试创建多个对象：");
            String arrayInputText = "创建三个人：李四，25岁，学生；王五，35岁，医生；赵六，40岁，教师";
            Person[] people = PersonAiFactory.createArrayByString(arrayInputText);
            System.out.println("结果：");
            for (Person p : people) {
                System.out.println(p);
            }
            System.out.println();
            
            // 1.3 测试从文件创建对象
            System.out.println("1.3 测试从文件创建对象：");
            File testFile = new File("test_input.txt");
            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write("创建两个人：小明，20岁，学生；小红，22岁，学生");
            }
            
            Person[] filePeople = PersonAiFactory.createArrayByFile(testFile);
            System.out.println("结果：");
            for (Person p : filePeople) {
                System.out.println(p);
            }
            testFile.delete();
            System.out.println();
        } catch (Exception e) {
            System.err.println("基本功能测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testEdgeCases() {
        System.out.println("=== 边界条件测试 ===");
        try {
            // 2.1 测试最小年龄
            System.out.println("2.1 测试最小年龄：");
            String minAgeText = "创建一个年龄18岁的人小李，是学生";
            Person minAgePerson = PersonAiFactory.createByString(minAgeText);
            System.out.println("结果：" + minAgePerson);
            System.out.println();
            
            // 2.2 测试最大年龄
            System.out.println("2.2 测试最大年龄：");
            String maxAgeText = "创建一个年龄100岁的老人王老，是退休教师";
            Person maxAgePerson = PersonAiFactory.createByString(maxAgeText);
            System.out.println("结果：" + maxAgePerson);
            System.out.println();
            
            // 2.3 测试特殊字符
            System.out.println("2.3 测试特殊字符：");
            String specialText = "创建一个人：张*三，25岁，IT工程师，擅长C++/Java";
            Person specialPerson = PersonAiFactory.createByString(specialText);
            System.out.println("结果：" + specialPerson);
            System.out.println();
        } catch (Exception e) {
            System.err.println("边界条件测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testErrorHandling() {
        System.out.println("=== 错误处理测试 ===");
        
        // 3.1 测试空输入
        System.out.println("3.1 测试空输入：");
        try {
            Person person = PersonAiFactory.createByString("");
            System.out.println("错误：应该抛出异常但没有");
        } catch (Exception e) {
            System.out.println("成功捕获异常：" + e.getMessage());
        }
        System.out.println();
        
        // 3.2 测试不存在的文件
        System.out.println("3.2 测试不存在的文件：");
        try {
            Person[] people = PersonAiFactory.createArrayByFile(new File("non_existent.txt"));
            System.out.println("错误：应该抛出异常但没有");
        } catch (Exception e) {
            System.out.println("成功捕获异常：" + e.getMessage());
        }
        System.out.println();
        
        // 3.3 测试无效的输入格式
        System.out.println("3.3 测试无效的输入格式：");
        try {
            Person person = PersonAiFactory.createByString("这是一个无效的输入，没有包含任何人的信息");
            System.out.println("错误：应该抛出异常但没有");
        } catch (Exception e) {
            System.out.println("成功捕获异常：" + e.getMessage());
        }
        System.out.println();
    }
} 