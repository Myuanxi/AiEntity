# AiEntity Framework

[English](#english) | [中文](#chinese)

## English

### Introduction
AiEntity is a Java framework that simplifies the process of creating objects from natural language descriptions using AI. It provides annotations and processors to automatically generate factory classes for AI-powered object creation.

### Features
- Create objects from natural language descriptions
- Support for single object and array creation
- File-based object creation
- OpenAI API integration (and compatible APIs)
- Customizable field descriptions and constraints
- Annotation-based configuration

### Current Status
⚠️ **Note**: This framework is in its initial stage and may contain bugs or limitations. Use with caution in production environments.

### Requirements
- Java 17 or higher
- Maven 3.6 or higher
- AI API key (OpenAI or compatible)

### Installation
⚠️ **Note**: The framework is not yet available in Maven Central. You'll need to build it from source:

1. Clone the repository:
```bash
git clone https://github.com/yourusername/AiEntity.git
cd AiEntity
```

2. Build and install to local Maven repository:
```bash
mvn clean install
```

3. Add the dependency to your project's `pom.xml`:
```xml
<dependency>
    <groupId>com.myuanxi</groupId>
    <artifactId>ai-processor</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage
1. Create an entity class with `@AiEntity` annotation:

```java
@AiEntity
public class Person {
    @AiField(description = "Person's name, 2-4 Chinese characters")
    private String name;
    
    @AiField(description = "Age, between 18-100")
    private int age;
    
    @AiField(description = "Occupation, e.g., engineer, doctor, teacher")
    private String occupation;
    
    // Getters and setters
}
```

2. Use the generated factory class:

```java
// Create a single object
Person person = PersonAiFactory.createByString("Create a person named John, age 30, engineer");

// Create multiple objects
Person[] people = PersonAiFactory.createArrayByString("Create three people: Alice, 25, student; Bob, 35, doctor; Charlie, 40, teacher");

// Create objects from file
Person[] filePeople = PersonAiFactory.createArrayByFile(new File("input.txt"));
```

### Configuration
The framework supports various AI API providers that are compatible with OpenAI's API format. You can configure the API settings using environment variables:

```bash
# Required: API Key
export OPENAI_API_KEY=your_api_key_here

# Optional: API URL (default: https://api.openai.com/v1/chat/completions)
export OPENAI_API_URL=your_api_url_here

# Optional: Model name (default: gpt-3.5-turbo)
export OPENAI_MODEL=your_model_name_here
```

#### Compatible AI Providers
Many AI providers support OpenAI's API format, including:
- OpenAI
- Azure OpenAI
- Anthropic Claude
- Google Vertex AI
- LocalAI
- And many others...

To use a different provider, simply set the appropriate API URL and model name in the environment variables.

### Limitations
- Currently only supports OpenAI-compatible API format
- Limited error handling
- No support for complex object relationships
- No caching mechanism

### Future Plans
- Enhanced error handling
- Support for complex object relationships
- Caching mechanism
- More comprehensive documentation and examples

---

## Chinese

### 简介
AiEntity 是一个 Java 框架，用于简化通过自然语言描述使用 AI 创建对象的过程。它提供注解和处理器来自动生成用于 AI 驱动对象创建的工厂类。

### 特性
- 从自然语言描述创建对象
- 支持单个对象和数组创建
- 基于文件的对象创建
- OpenAI API 集成（及兼容的 API）
- 可自定义字段描述和约束
- 基于注解的配置

### 当前状态
⚠️ **注意**：本框架处于初始阶段，可能存在 bug 或限制。在生产环境中使用时请谨慎。

### 环境要求
- Java 17 或更高版本
- Maven 3.6 或更高版本
- AI API 密钥（OpenAI 或兼容的 API）

### 安装
⚠️ **注意**：本框架尚未发布到 Maven 中央仓库。您需要从源代码构建：

1. 克隆仓库：
```bash
git clone https://github.com/yourusername/AiEntity.git
cd AiEntity
```

2. 构建并安装到本地 Maven 仓库：
```bash
mvn clean install
```

3. 在项目的 `pom.xml` 中添加依赖：
```xml
<dependency>
    <groupId>com.myuanxi</groupId>
    <artifactId>ai-processor</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用方法
1. 使用 `@AiEntity` 注解创建实体类：

```java
@AiEntity
public class Person {
    @AiField(description = "人的名字，2-4个汉字")
    private String name;
    
    @AiField(description = "年龄，范围在18-100之间")
    private int age;
    
    @AiField(description = "职业描述，例如：工程师、医生、教师等")
    private String occupation;
    
    // Getter 和 Setter 方法
}
```

2. 使用生成的工厂类：

```java
// 创建单个对象
Person person = PersonAiFactory.createByString("创建一个名为张三的人，年龄30岁，职业是工程师");

// 创建多个对象
Person[] people = PersonAiFactory.createArrayByString("创建三个人：李四，25岁，学生；王五，35岁，医生；赵六，40岁，教师");

// 从文件创建对象
Person[] filePeople = PersonAiFactory.createArrayByFile(new File("input.txt"));
```

### 配置
本框架支持各种兼容 OpenAI API 格式的 AI 提供商。您可以使用环境变量配置 API 设置：

```bash
# 必需：API 密钥
export OPENAI_API_KEY=your_api_key_here

# 可选：API URL（默认：https://api.openai.com/v1/chat/completions）
export OPENAI_API_URL=your_api_url_here

# 可选：模型名称（默认：gpt-3.5-turbo）
export OPENAI_MODEL=your_model_name_here
```

#### 兼容的 AI 提供商
许多 AI 提供商支持 OpenAI 的 API 格式，包括：
- OpenAI
- Azure OpenAI
- Anthropic Claude
- Google Vertex AI
- LocalAI
- 以及其他许多提供商...

要使用不同的提供商，只需在环境变量中设置相应的 API URL 和模型名称即可。

### 限制
- 目前仅支持 OpenAI 兼容的 API 格式
- 错误处理有限
- 不支持复杂的对象关系
- 没有缓存机制

### 未来计划
- 增强错误处理
- 支持复杂的对象关系
- 添加缓存机制
- 更全面的文档和示例 