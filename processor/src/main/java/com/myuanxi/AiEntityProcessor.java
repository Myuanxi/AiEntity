// AiEntityProcessor.java
package com.myuanxi;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.myuanxi.AiEntity"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AiEntityProcessor extends AbstractProcessor {
    
    private static final String INDENT = "    ";
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AiEntity.class)) {
            if (element.getKind() != ElementKind.CLASS) continue;
            
            TypeElement classElement = (TypeElement) element;
            processClass(classElement);
        }
        return true;
    }
    
    private void processClass(TypeElement classElement) {
        try {
            // 获取类的基本信息
            String className = classElement.getSimpleName().toString();
            String packageName = processingEnv.getElementUtils()
                    .getPackageOf(classElement)
                    .getQualifiedName().toString();
            String factoryClassName = className + "AiFactory";
            
            // 获取注解配置
            AiEntity config = classElement.getAnnotation(AiEntity.class);
            
            // 分析类的字段
            List<FieldInfo> fields = analyzeFields(classElement);
            
            // 生成工厂类
            generateFactoryClass(packageName, className, factoryClassName, config, fields);
            
        } catch (Exception e) {
            // 使用 Messager 报告错误
            processingEnv.getMessager().printError("Failed to process class: " + classElement + ". Error: " + e.getMessage());
        }
    }
    
    private List<FieldInfo> analyzeFields(TypeElement classElement) {
        return classElement.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.FIELD)
                .map(element -> {
                    VariableElement field = (VariableElement) element;
                    AiField aiField = field.getAnnotation(AiField.class);
                    String description = aiField != null ? aiField.description() : "";
                    return new FieldInfo(
                            field.getSimpleName().toString(),
                            field.asType().toString(),
                            description
                    );
                })
                .collect(Collectors.toList());
    }
    
    private void generateFactoryClass(
            String packageName,
            String originalClassName,
            String factoryClassName,
            AiEntity config,
            List<FieldInfo> fields
    ) throws IOException {
        JavaFileObject factoryFile = processingEnv.getFiler()
                .createSourceFile(packageName + "." + factoryClassName);
        
        try (Writer writer = factoryFile.openWriter()) {
            // 写入包声明
            if (!packageName.isEmpty()) {
                writer.write("package " + packageName + ";\n\n");
            }
            
            // 写入导入语句
            writeImports(writer);
            
            // 开始写入类定义
            writer.write("public class " + factoryClassName + " {\n\n");
            
            // 写入静态字段
            writeStaticFields(writer, config);
            
            // 写入工具方法
            writeUtilityMethods(writer, originalClassName, fields);
            
            // 写入主要方法
            writeMainMethods(writer, originalClassName, fields);
            
            // 结束类定义
            writer.write("}\n");
        }
    }
    
    private void writeImports(Writer writer) throws IOException {
        writer.write("import java.io.File;\n");
        writer.write("import java.io.IOException;\n");
        writer.write("import java.nio.file.Files;\n");
        writer.write("import java.net.URI;\n");
        writer.write("import java.net.http.HttpClient;\n");
        writer.write("import java.net.http.HttpRequest;\n");
        writer.write("import java.net.http.HttpResponse;\n");
        writer.write("import com.fasterxml.jackson.databind.ObjectMapper;\n");
        writer.write("import com.fasterxml.jackson.databind.JsonNode;\n");
        writer.write("import com.fasterxml.jackson.core.type.TypeReference;\n\n");
    }
    
    private void writeStaticFields(Writer writer, AiEntity config) throws IOException {
        writer.write(INDENT + "private static final HttpClient httpClient = HttpClient.newHttpClient();\n");
        writer.write(INDENT + "private static final ObjectMapper MAPPER = new ObjectMapper();\n");
        writer.write(INDENT + "private static final String MODEL = \"" + config.model() + "\";\n");
        writer.write(INDENT + "private static final String API_URL = \"" + config.url() + "\";\n");
        writer.write(INDENT + "private static final String API_KEY = \"" + config.apikey() + "\";\n\n");
    }
    
    private void writeUtilityMethods(Writer writer, String originalClassName, List<FieldInfo> fields) throws IOException {
        // 生成构建系统提示的方法
        writer.write(INDENT + "private static String buildSystemPrompt() {\n");
        writer.write(INDENT + INDENT + "StringBuilder prompt = new StringBuilder();\n");
        writer.write(INDENT + INDENT + "prompt.append(\"You are a JSON generator for the " + originalClassName + " class. \");\n");
        writer.write(INDENT + INDENT + "prompt.append(\"Generate valid JSON for the following fields: \");\n");
        
        // 添加字段信息到提示中
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo field = fields.get(i);
            String description = field.description.isEmpty() ? field.name : field.description;
            writer.write(INDENT + INDENT + "prompt.append(\"" + field.name + " (" + field.type + "): " + description + "\");\n");
            if (i < fields.size() - 1) {
                writer.write(INDENT + INDENT + "prompt.append(\", \");\n");
            }
        }
        
        writer.write(INDENT + INDENT + "return prompt.toString();\n");
        writer.write(INDENT + "}\n\n");
        
        // 写入 callAI 方法
        writeCallAiMethod(writer);
    }
    
    private void writeCallAiMethod(Writer writer) throws IOException {
        writer.write(INDENT + "private static String callAI(String inputText) {\n");
        writer.write(INDENT + INDENT + "try {\n");
        writer.write(INDENT + INDENT + INDENT + "String systemPrompt = buildSystemPrompt();\n");
        writer.write(INDENT + INDENT + INDENT + "String payload = String.format(\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "\"{\\\"model\\\": \\\"%s\\\", " +
                "\\\"messages\\\": [" +
                "{\\\"role\\\": \\\"system\\\", \\\"content\\\": \\\"%s\\\"}, " +
                "{\\\"role\\\": \\\"user\\\", \\\"content\\\": \\\"%s\\\"}], " +
                "\\\"response_format\\\": {\\\"type\\\": \\\"json_object\\\"}, " +
                "\\\"temperature\\\": 0.7}\"," +
                "\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "MODEL,\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "systemPrompt.replace(\"\\\"\", \"\\\\\\\"\"),\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "inputText.replace(\"\\\"\", \"\\\\\\\"\")\n");
        writer.write(INDENT + INDENT + INDENT + ");\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "System.out.println(\"Sending request to AI API...\");\n");
        writer.write(INDENT + INDENT + INDENT + "System.out.println(\"Request payload: \" + payload);\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL))\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + ".header(\"Content-Type\", \"application/json\")\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + ".header(\"Authorization\", \"Bearer \" + API_KEY)\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + ".POST(HttpRequest.BodyPublishers.ofString(payload))\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + ".build();\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());\n");
        writer.write(INDENT + INDENT + INDENT + "String responseBody = response.body();\n");
        writer.write(INDENT + INDENT + INDENT + "System.out.println(\"API Response: \" + responseBody);\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "if (response.statusCode() != 200) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "throw new RuntimeException(\"API request failed with status code: \" + response.statusCode());\n");
        writer.write(INDENT + INDENT + INDENT + "}\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "JsonNode rootNode = MAPPER.readTree(responseBody);\n");
        writer.write(INDENT + INDENT + INDENT + "if (rootNode.has(\"error\")) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "throw new RuntimeException(\"API Error: \" + rootNode.get(\"error\").get(\"message\").asText());\n");
        writer.write(INDENT + INDENT + INDENT + "}\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "if (!rootNode.has(\"choices\") || rootNode.get(\"choices\").size() == 0) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "throw new RuntimeException(\"Invalid API response format: missing choices\");\n");
        writer.write(INDENT + INDENT + INDENT + "}\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "JsonNode firstChoice = rootNode.get(\"choices\").get(0);\n");
        writer.write(INDENT + INDENT + INDENT + "if (!firstChoice.has(\"message\") || !firstChoice.get(\"message\").has(\"content\")) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "throw new RuntimeException(\"Invalid API response format: missing message content\");\n");
        writer.write(INDENT + INDENT + INDENT + "}\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "String content = firstChoice.get(\"message\").get(\"content\").asText();\n");
        writer.write(INDENT + INDENT + INDENT + "System.out.println(\"Extracted content: \" + content);\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "return content;\n");
        writer.write(INDENT + INDENT + "} catch (Exception e) {\n");
        writer.write(INDENT + INDENT + INDENT + "System.err.println(\"Error in callAI: \" + e.getMessage());\n");
        writer.write(INDENT + INDENT + INDENT + "e.printStackTrace();\n");
        writer.write(INDENT + INDENT + INDENT + "throw new RuntimeException(\"AI request failed\", e);\n");
        writer.write(INDENT + INDENT + "}\n");
        writer.write(INDENT + "}\n\n");
    }
    
    private void writeMainMethods(Writer writer, String originalClassName, List<FieldInfo> fields) throws IOException {
        // createByString 方法
        writer.write(INDENT + "public static " + originalClassName + " createByString(String inputText) {\n");
        writer.write(INDENT + INDENT + "String jsonResponse = callAI(inputText);\n");
        writer.write(INDENT + INDENT + "try {\n");
        writer.write(INDENT + INDENT + INDENT + "return MAPPER.readValue(jsonResponse, " + originalClassName + ".class);\n");
        writer.write(INDENT + INDENT + "} catch (Exception e) {\n");
        writer.write(INDENT + INDENT + INDENT + "throw new RuntimeException(\"Failed to parse AI response\", e);\n");
        writer.write(INDENT + INDENT + "}\n");
        writer.write(INDENT + "}\n\n");
        
        // createArrayByString 方法
        writer.write(INDENT + "public static " + originalClassName + "[] createArrayByString(String inputText) {\n");
        writer.write(INDENT + INDENT + "String jsonResponse = callAI(inputText);\n");
        writer.write(INDENT + INDENT + "try {\n");
        writer.write(INDENT + INDENT + INDENT + "JsonNode jsonNode = MAPPER.readTree(jsonResponse);\n");
        writer.write(INDENT + INDENT + INDENT + "JsonNode arrayNode = null;\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "// 尝试从不同的路径获取数组\n");
        writer.write(INDENT + INDENT + INDENT + "if (jsonNode.isArray()) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "arrayNode = jsonNode;\n");
        writer.write(INDENT + INDENT + INDENT + "} else if (jsonNode.has(\"persons\")) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "arrayNode = jsonNode.get(\"persons\");\n");
        writer.write(INDENT + INDENT + INDENT + "} else if (jsonNode.has(\"data\")) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "arrayNode = jsonNode.get(\"data\");\n");
        writer.write(INDENT + INDENT + INDENT + "} else if (jsonNode.has(\"results\")) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "arrayNode = jsonNode.get(\"results\");\n");
        writer.write(INDENT + INDENT + INDENT + "} else {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "// 如果找不到数组，尝试将整个响应包装成数组\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "arrayNode = MAPPER.createArrayNode().add(jsonNode);\n");
        writer.write(INDENT + INDENT + INDENT + "}\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "if (!arrayNode.isArray()) {\n");
        writer.write(INDENT + INDENT + INDENT + INDENT + "throw new RuntimeException(\"Expected JSON array response\");\n");
        writer.write(INDENT + INDENT + INDENT + "}\n\n");
        
        writer.write(INDENT + INDENT + INDENT + "return MAPPER.treeToValue(arrayNode, " + originalClassName + "[].class);\n");
        writer.write(INDENT + INDENT + "} catch (Exception e) {\n");
        writer.write(INDENT + INDENT + INDENT + "throw new RuntimeException(\"Failed to parse AI response array\", e);\n");
        writer.write(INDENT + INDENT + "}\n");
        writer.write(INDENT + "}\n\n");
        
        // createArrayByFile 方法
        writer.write(INDENT + "public static " + originalClassName + "[] createArrayByFile(File file) {\n");
        writer.write(INDENT + INDENT + "try {\n");
        writer.write(INDENT + INDENT + INDENT + "String content = Files.readString(file.toPath());\n");
        writer.write(INDENT + INDENT + INDENT + "return createArrayByString(content);\n");
        writer.write(INDENT + INDENT + "} catch (IOException e) {\n");
        writer.write(INDENT + INDENT + INDENT + "throw new RuntimeException(\"Failed to read file\", e);\n");
        writer.write(INDENT + INDENT + "}\n");
        writer.write(INDENT + "}\n");
    }
    
    private static class FieldInfo {
        final String name;
        final String type;
        final String description;
        
        FieldInfo(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }
    }
    
    private void printError(String message) {
        processingEnv.getMessager().printError(message);
    }
}