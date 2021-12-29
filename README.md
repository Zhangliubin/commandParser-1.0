# CommandParser

## 简介

CommandParser 是一个基于 Java 平台开发的小型框架，用于解析、管理命令行参数。CommandParser 提供了脚本指令设计方式及图形化设计方式，便于用户方便地设计、管理命令行程序。

CommandParser 兼有传统命令行设计工具 Jcommander 的特点及独特的 GUI 图形化设计功能，在实践阶段已证实可以有效降低 Java 开发者的使用门槛，降低代码耦合度。

## 安装与使用 CommandParser

CommandParser 在 JDK 8 中开发完成，得益于 Java 跨平台的特性，我们预计它也可以在所有支持 Java 语言的软件与硬件环境中运行。

- 在 Windows 和 MacOS 下，当设备准确配置了JDK运行环境时，可以双击启动图形界面；
- 在 Java 脚本程序中，可以通过导入该包进行使用。

| Type         | File                                                         |
| ------------ | ------------------------------------------------------------ |
| Package      | [CommandParser-1.0.jar](http://pmglab.top/commandParser/commandParser-1.0.jar) |
| Source codes | [CommandParser-1.0](https://github.com/Zhangliubin/commandParser-1.0) |

## 快速入门

### 1. 启动参数管理界面

**方法1:** 在具有图形界面的操作系统上双击 commandParser.jar，打开图形界面 (需要操作系统配置了 java 环境)

**方法2:** 在具有图形界面的操作系统上使用终端 (terminal)，输入 `java -jar ./commandParser.jar `

![图1](https://tva1.sinaimg.cn/large/008i3skNgy1gxtnd2n1u1j31e00u00uh.jpg)

### 2. 添加参数

**方法1:** 双击空白处，在末尾追加新的参数

**方法2:** 点击左下角 `+`，在选中位置插入新的参数

**方法3:** 点击右下角 `Open` 或拖拽一个文件到窗口中，将导入该参数文件中所有的参数

### 3. 配置参数属性 (Command 面板)

每个参数都有 12 个基本属性，其中 commandName, convertTo 和 arity 是建议设置的属性。每个属性的含义如下：

| 属性         | 含义                          | 描述                                                         |
| ------------ | ----------------------------- | ------------------------------------------------------------ |
| commandName  | 识别的参数名 (关键字)         | 1. 参数名支持的字符类型: 阿拉伯数字 `0-9`、大小写字母 `a-zA-z`、部分特殊字符 `+-_@` <br />2. 多个参数名定位到同一参数时，使用 `,` 进行分隔，第一个参数名将注册为主参数名<br />3. 所有的参数名 (无论是否为主参数名) 都不可重复 |
| request      | 是否为必备参数                | 在输出文档中，必备参数前有 `*` 标记                          |
| default      | 默认值                        | 1. convertTo 为数组类型时，使用 `,` 作为不同元素的分隔符<br />2. 未指定默认值时，将设置为 `null` |
| convertTo    | 参数转换的数据类型            | 1. 默认值 (default) 和输入的参数值都会被 convertTo 转为对应的 Java 对象<br />2. built-in 类型需要在 Java 脚本中重新调用 `parser.getCommandItem($commandName).convertTo($myConvertor)`进行设置 |
| validateWith | 使用验证器验证参数值          | 1. 多个验证器使用 `;` 进行分隔<br />2. built-in 类型需要在 Java 脚本中重新调用 `parser.getCommandItem($commandName).validateWith()($myValidator)`进行设置 |
| arity        | 参数长度                      | 1. 捕获到参数关键字时，之后的 arity 个字段都识别为它的值<br />2. 参数长度为 `≥1` 时，将一直捕捉随后的字段，直到遇到下一个参数关键字 |
| group        | 参数组                        | 文档提示：设置参数所在的参数组                               |
| description  | 描述文档                      | 文档提示：设置参数的描述文档                                 |
| format       | 格式                          | 文档提示：设置参数的输入格式                                 |
| hidden       | 在文档中隐藏该参数            | 被隐藏的参数可以使用，但是不会在文档中显示                   |
| help         | 该参数是否识别为帮助指令      | 用户传入帮助指令时，允许输入错误的参数，并且不会对参数的规则进行检验 |
| debug        | 是否为 debug 模式下可用的参数 | 在非 debug 模式下无法使用勾选了该项的参数，该属性建议用于标记一些未完成开发或内部测试的参数 |

### 4. 配置参数规则 (Other Option 面板)

参数之间若存在搭配规则，建议在 Other Option 面板中进行配置。目前 commandParser 支持 5 种类型的参数规则：

| 规则         | 含义                                                         | 表达式        |
| ------------ | ------------------------------------------------------------ | ------------- |
| AT_MOST_ONE  | command1 和 command2 至多传入一个                            | $p_1+p_2\le1$ |
| AT_LEAST_ONE | command1 和 command2 至少传入一个                            | $p_1+p_2\ge1$ |
| REQUEST_ONE  | command1 和 command2 需要有其中的一个                        | $p_1+p_2=1$   |
| PRECONDITION | 传入了 command1 才能 command2 <br />即: command1 是 command2 的前置条件 | $p_1\ge p_2$  |
| SYMBIOSIS    | command1 和 command2 同时传入或同时不传入                    | $p_1+p_2\ne1$ |

<details><summary><b>案例1</b> 程序的输出格式可以为 <code>--o-bgz</code>(使用 bgzip 压缩输出的数据) 或 <code>--o-text</code> (纯文本输出), 两者不能同时使用</summary>指令设计如下: </br><img src="https://tva1.sinaimg.cn/large/008i3skNgy1gxuuiqdolhj31jk0c640m.jpg" alt="案例1-1" style="zoom:100%;" /> </br></br>规则设计如下: </br><img src="https://tva1.sinaimg.cn/large/008i3skNgy1gxuujgz73jj31jk08l3zg.jpg" alt="案例1-2" style="zoom:100%;" /> </br> 输出格式为 <code>--o-bgz</code> 时，可以指定并行压缩的线程数 (-t) 和压缩级别 (-l)</details>

### 5. 配置全局规则 (Other Option 面板)





### 使用图形界面设计指令

#### Step1: 设计命令行指令

双击启动 commandParser.jar，打开如下图形界面，并设计所需的参数：

![未命名.001](https://tva1.sinaimg.cn/large/008i3skNgy1gwp37mnf4gj319609mabh.jpg)

> 该文件名为 bgzip，仅作为子工具的入口。因此：
>
> - 左图中所有参数的 convertTo 都是 string-array 类型，arity 都是 $\ge1$；
> - 右图中指定 offset=1，表明不解析第一个参数值 (即 bgzip 不解析，而是解析从 bgzip 之后的参数)；Global Rule 指定为 REQUEST_ONE，表明需要传递一个参数。

效果：

![image-20211123145916343](https://tva1.sinaimg.cn/large/008i3skNgy1gwp3dn10ekj30ly0bxq40.jpg)

同理，其余的 6 个文件设计后存放在 ./resource/command 目录下。

#### Step2: 在 Java 中使用该解析器

指令的使用参考程序见：src/dev/BGZIPParserFromFile.java。

```java
// 从文件中加载解析器 (文件打包为 jar 包时，相对路径失效，因此需要使用以下语句表明在 jar 工程中的文件)
CommandParser mainParser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/bgzip");

// 使用该解析器解析指令
CommandMatcher options = mainParser.parse("bgzip", "compress", "-h");

// 查看该解析器是否包含某个指令
boolean containCompress = options.isPassedIn("compress");

// 获取该指令捕获的参数值 (compress 指定的 convertor 是 string-array)
String[] compressValues = (String[]) options.get("compress");
System.out.println(Arrays.toString(compressValues));
```

### 使用脚本设计指令

#### Step1: 设计命令行指令

```java
CommandParser mainParser = new CommandParser("bgzip <input>");
mainParser.register("compress")
  .convertTo(new StringArrayConverter() {
  })
  .setDescription("Compression using parallel-bgzip (supported by CLM algorithm).", "'compress <file>'");

mainParser.register("convert")
  .convertTo(new StringArrayConverter() {
  })
  .setDescription("Convert *.gz format to *.bgz format.", "'convert <file>'");

mainParser.register("decompress")
  .convertTo(new StringArrayConverter() {
  })
  .setDescription("Decompression.", "'decompress <file>'");

mainParser.register("extract")
  .convertTo(new StringArrayConverter() {
  })
  .setDescription("Cut the bgzip file by pointer range (decompressed file).", "'cut <file> <start>-<end>'");

mainParser.register("concat")
  .convertTo(new StringArrayConverter() {
  })
  .setDescription("Concatenate multiple files.", "'concat <file>,<file>,...'");

mainParser.register("md5")
  .convertTo(new StringArrayConverter() {
  })
  .setDescription("Calculate a message-digest fingerprint (checksum) for decompressed file.", "'md5 <file>'");

mainParser.registerGlobalRule(CommandRuleType.REQUEST_ONE);
mainParser.offset(1);
```

同理，其余的 6 个文件设计后存放在 ./resource/command 目录下。

#### Step2: 在 Java 中使用该解析器

指令的使用参考程序见：src/dev/BGZIPParser.java，语法与图形界面几乎一致 (区别在于创建 Parser 的流程)。

### 将脚本设计指令导出为命令文件

对 Parser 对象使用 `parser.toFile("$path")` 将导出 command 文件，该文件可以被图形界面直接使用于修改、二次创建。

### 使用 bgzip-tools

```shell
# 打开图形界面
java -jar ./commandParser-1.0.jar

# 使用 bgzip-tools 工具集
java -jar ./commandParser-1.0.jar bgzip compress ./resource/command/bgzip
```

## Command 文件格式

Command 文件是 CommandParser 用于储存命令信息的格式。Command 文件包含注释信息行、标题行、数据行。其中：

- 注释信息行的行首为“##”，内容是键值对的形式，通常包含 Command 文件版本信息、程序名、指令偏移量、全局规则、指令规则等
- 标题行的行首为“#”，包含 11 个顺序固定的字段，分别为 commandName (指令名)，request (是否为必备参数)，default (默认值, 该值默认为字符串格式, 并按照 convertTo 进行格式转换), convertTo (指令值的格式, 内置 19 种常见格式，详见 API 文档), validateWith (验证器, 内置 4 种常见格式), arity (参数长度), group (参数组), description (描述信息), format (指令使用格式), hidden (是否在 -h 帮助文档中隐藏该参数), help (是否捕获为帮助指令)
- 之后的数据行中每一个指令占用一行，指令的参数按照标题行顺序进行填写，使用制表符分割数据，缺失信息使用“.”占位

```shell
##commandParserV1.0
##programName=<value="bgzip <input>";description="when '-h' were passed in, would be show 'Usage: $value [options]'">
##offset=<value=1;description="skip the $value arguments before the command argument passed in">
##globalRule=<value="REQUEST_ONE";description="one of the following rules is supported: {'.','AT_MOST_ONE','AT_LEAST_ONE','REQUEST_ONE'}">
#commandName	request	default	convertTo	validateWith	arity	group	description	format	hidden	help
--help,-help,-h	false	.	passedIn	.	0	Options	.	.	true	true
compress	false	.	string-array	.	-1	Options	Compression using parallel-bgzip (supported by CLM algorithm).	'compress <file>'	false	false
convert	false	.	string-array	.	-1	Options	Convert *.gz format to *.bgz format.	'convert <file>'	false	false
decompress	false	.	string-array	.	-1	Options	Decompression.	'decompress <file>'	false	false
extract	false	.	string-array	.	-1	Options	Cut the bgzip file by pointer range (decompressed file).	'cut <file> <start>-<end>'	false	false
concat	false	.	string-array	.	-1	Options	Concatenate multiple files.	'concat <file>,<file>,...'	false	false
md5	false	.	string-array	.	-1	Options	Calculate a message-digest fingerprint (checksum) for decompressed file.	'md5 <file>'	false	false
```

## 开发文档

指令的解析与使用分四步进行：用户传入指令 `args` $\to$ 创建解析器 `parser` $\to$ 使用解析器解析用户指令，并得到参数集 `matcher` $\to$ 从参数集中获取参数信息。

### 创建解析器

解析器可以使用脚本进行注册，也可以从文件中导入。

```java
// 使用脚本创建解析器
CommandParser parser = new CommandParser("bgzip <input>");

// 从文件中导入解析器 (不建议使用该语句)
CommandParser parser = CommandParser.loadFromFile("/command/bgzip");

// 从文件中导入解析器 (建议使用该语句)
CommandParser parser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/bgzip");
```

CommandParser 包含三个注册器:

- CommandParser(String programName): 创建解析器，并设置程序名为 programName，添加参数名为 `--help, -help, -h` 的帮助指令
- CommandParser(boolean help): 创建解析器，并设置程序名为 `<main class>`，根据 `help` 决定是否往解析器中添加默认的帮助指令
- CommandParser(boolean help, String programName): 创建解析器，并设置程序名为 programName。根据 `help` 决定是否往解析器中添加默认的帮助指令

### 设置解析器的全局参数

- parser.setProgramName(String programName): 设置程序名
- parser.offset(int length): 设置偏移量 (偏移量指对于用户传入的指令 `args`，将忽略前面的 length 个参数)
- parser.registerGlobalRule(CommandRuleType ruleType): 设置全局规则。全局规则接受一个 CommandRuleType 参数 (这是一个枚举类)，支持 AT_MOST_ONE (至多包含 1 个参数)、AT_LEAST_ONE (至少包含 1 个参数)、REQUEST_ONE (恰好 1 个参数)
- parser.createOptionGroup(String optionGroup): 创建参数组

<img src="https://tva1.sinaimg.cn/large/008i3skNgy1gwpc7o2zdsj30lx0dw40k.jpg" alt="image-20211123155539971" style="zoom: 67%;" />

### 向解析器中注册指令

初始情况下解析器中不包含任何指令，通过 parser.register(String... commandNames) 进行指令注册，该指令的返回类型是 CommanItem，即以 commandNames 作为指令名的指令项目。

> commandName 的格式要求: 大小写英文字母、数字、横杠-、加号+、下划线_，并且该参数名不能与该解析器中已经注册的任何参数重复

```java
// 注册参数名为 --threads, -t 的指令项目
CommandItem threadItem = parser.register("--threads", "-t");
```

### 设置指令的参数

CommandItem 具有参数:

- item.convertTo(IConverter<T> converter): 将指令的捕获值转为指定的格式
- item.validateWith(IValidator... validators): 验证该指令的捕获值，
  - item.validateWith(int MIN, int MAX): 创建一个整数范围验证器，参数值必须在 [MIN, MAX] 内
  - item.validateWith(double MIN, double MAX): 创建一个浮点数范围验证器，参数值必须在 [MIN, MAX] 内
- item.arity(int length): 设置参数长度，-1 表示至少 1 个参数
- item.defaultTo(Object defaultValue): 设置该指令的默认值，若未设置转换器，则自动设置为与 默认值 一致的转换器 (如: defaultTo(new String[]{}), 则转换器自动设置为 StringArrayConverter)
- item.addOptions(CommandOptions... options): 追加指令的权限参数，可选 REQUEST (必备参数)、HIDDEN (隐藏参数)、HELP (帮助参数)
  - item.setOptions(CommandOptions... options): 设置指令的权限参数，可选 REQUEST (必备参数)、HIDDEN (隐藏参数)、HELP (帮助参数)，该方法会覆盖现有的权限参数信息
- item.setOptionGroup(String optionGroup): 设置该指令的参数组名 (默认情况下由 Parser 指令)
- item.setDescription(String description): 设置该指令的描述文档
- item.setDescription(String description, String format): 设置该指令的描述文档、格式

每一个方法的返回值都是 CommandItem 本身，因此支持链式调用：

```java
// 注册指令，并配置其参数
parser.register("--threads", "-t")
	.defaultTo(4)
	.validateWith(1, 10)
	.setDescription("Set the number of threads for bgzip compression.", "'-t <int>' (" + 1 + "~" + 10 + ")");
```

CommandParser 内置的 19 种转换器如下：

| 转换器类型                            | 默认参数长度 | 输入格式示例        | 转换格式                                                   |
| ------------------------------------- | ------------ | ------------------- | ---------------------------------------------------------- |
| passedIn                              | 0            |                     | (该指令无转换值，而是通过 matcher.isPassedIn 判断是否传入) |
| boolean                               | 1            | true                | true                                                       |
| short                                 | 1            | 10086               | 10086                                                      |
| integer                               | 1            | 20119823            | 20119823                                                   |
| long                                  | 1            | 20119823            | 20119823                                                   |
| double                                | 1            | 1.0                 | 1.0                                                        |
| string                                | 1            | sysu                | "sysu"                                                     |
| short-array                           | -1           | 1 2 3 4 5           | short[]{1, 2, 3, 4 ,5}                                     |
| integer-array                         | -1           | 1 2 3 4 5           | int[]{1, 2, 3, 4 ,5}                                       |
| long-array                            | -1           | 1 2 3 4 5           | long[]{1, 2, 3, 4 ,5}                                      |
| double-array                          | -1           | 0.1 0.2 0.4 0.8 1.0 | double[]{0.1, 0.2, 0.4, 0.8, 1.0}                          |
| string-array                          | -1           | sysu pku thu        | String[]{"sysu", "pku", "thu"}                             |
| <start\>-\<end\> (integer)            | 1            | 10177-20119823      | int[]{10177, 20119823}                                     |
| <start\>-\<end\> (long)               | 1            | 10177-20119823      | long[]{10177, 20119823}                                    |
| <start\>-\<end\> (double)             | 1            | 0.2-0.8             | double[]{0.2, 0.8}                                         |
| \<index\>:\<start\>-\<end\> (integer) | 1            | 22:290000-1000000   | int[]{22, 290000, 1000000}                                 |
| <start\>-\<end\> (string)             | 1            | 290000-1000000      | String[]{"290000", "1000000"}                              |
| \<index\>:\<start\>-\<end\> (string)  | 1            | 22:290000-1000000   | String[]{"22", "290000", "1000000"}                        |
| k1=v1;k2=v2;...                       | 1            | chrom=1;ALT=A;REF=T | HashMap{"chrom":"1", "ALT":"A", "REF":"T"}                 |

CommandParser 内置的 4 种验证器如下：

| 验证器                                     | 描述                                | 验证器                                      | 描述                                   |
| ------------------------------------------ | ----------------------------------- | ------------------------------------------- | -------------------------------------- |
| EnsureFileExistsValidator.INSTANCE         | 确保文件存在 (不存在时报错)         | new DoubleValidator(double MIN, double MAX) | 传入值的范围不在 [MIN, MAX] 之间时报错 |
| EnsureFileIsNotDirectoryValidator.INSTANCE | 确保文件不是文件夹 (为文件夹时报错) | new IntValidator(double MIN, double MAX)    | 传入值的范围不在 [MIN, MAX] 之间时报错 |

### 设置指令间的规则

多个 CommandItem 之间可以通过以下指令设置彼此间的规则：

- parser.registerRule(String item1, String item2, CommandRuleType ruleType)
- parser.registerRule(String item1, String[] items, CommandRuleType ruleType)
- parser.registerRule(String[] items1, String[] items2, CommandRuleType ruleType)

指令项目必须先进行注册，才能添加指令规则。指令规则 CommandRuleType 支持:  AT_MOST_ONE (至多包含 1 个参数)、AT_LEAST_ONE (至少包含 1 个参数)、REQUEST_ONE (恰好 1 个参数)、PRECONDITION (前置, command2 只有在 command1 传入时才能被指定)、SYMBIOSIS (共生, 同时传入或同时不传入)

```java
// 注册指令 --no-qc 与其他质控参数的规则，含义为：指令了 --no-qc (不质控) 时，禁止用户传入其他质控参数
parser.registerRule("--no-qc", new String[]{"--gty-gq", "--gty-dp", "--seq-qual", "--seq-dp", "--seq-mq", "--seq-ac", "--seq-af", "--seq-an", "--max-allele"}, CommandRuleType.AT_MOST_ONE);
```

### 导出解析器

创建完成的解析器可以导出 command 文件，以便于后续修改、复用：

- parser.toFile(String fileName)

### 使用解析器解析指令

创建完成的解析器使用 `parser.parse(args)` 解析指令，并获得 `CommandMatcher` 对象。CommandMatcher 对象包含 3 个方法：

- matcher.isPassedIn(String commandKey): 是否传入 commandKey 参数
- matcher.get(String commandKey): 获取 commandKey 对应的参数值，未传入时将获得参数的 defaultValue (默认为 null)
- matcher.contain(String commandKey): 是否包含 commandKey 参数 (无论是否传入)

### 高级语法: 自定义转换器与验证器

有时候一个指令往往需要对应多种用法，此时默认的 19 种参数转换器便不再适用。commandParser 可以通过自定义转换器与验证器，实现复杂的参数转换与验证。

实现该功能需要指定的方法实现相应的接口 (IConverter 与 IValidator)。对于自定义的转换器 (见“高级语法”篇)，在图形界面和指令文件中都将以 `built-in` 形式存在，导入到 java 中时将创建 `AbstractConvertor` 和  `AbstractValidator` 占位，用户需要使用：

```
parser.getCommandItem("$commandName").convertTo($convertor)
parser.getCommandItem("$commandName").validateWith($convertor)
```

重新链接自定义的方法。

例如：实现一个名为 `--subject` 的指令，包含两种输入格式：`--subject s1,s2,s3,...` 与 `--subject @<file>`，后者表示从文件中读取信息并作为值被 `--subject` 捕获。

```java
parser.register("--subject", "-s")
        .arity(1)
        .convertTo(new StringArrayConverter(",") {
            @Override
            public String[] convert(String... params) {
                Assert.that(params.length == 1);
                String subjects = params[0];
                SmartList<String> converted = new SmartList<>();

                if (subjects.startsWith("@")) {
                    // 读取文件模式
                    subjects = subjects.substring(1);
                    try (FileStream fs = new FileStream(subjects, subjects.endsWith(".gz") ? FileOptions.BGZIP_READER : FileOptions.DEFAULT_READER)) {
                        String line;
                        while ((line = fs.readLineToString()) != null) {
                            converted.add(line.split(this.separator));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    for (String param : params) {
                        converted.add(param.split(this.separator));
                    }
                }

                return converted.toStringArray();
            }
        })
        .setDescription("Extract the information of the specified subjects. Subject name can be stored in a file with ',' delimited form, and pass in via '-s @file'", "'-s <string>,<string>,...' or '-s @<file>'");
```

例如：实现一个名为 `--model` 的指令，它的转换格式为 ILDModel 类型。输入的 `geno, --geno, --geno-ld, --geno-r2` 将被识别为 `ILDModel.GENOTYPE_LD`，而输入的 `hap, --hap, --hap-ld, --hap-r2` 将被识别为 `ILDModel.HAPLOTYPE_LD`。

```java
parser.register("--model", "-m")
        .convertTo((IConverter<ILDModel>) params -> {
            if (params.length == 1) {
                String param = params[0].toUpperCase();

                if (param.equals("GENO") || param.equals("--GENO") || param.equals("--GENO-LD") || param.equals("--GENO-R2")) {
                    return ILDModel.GENOTYPE_LD;
                } else if (param.equals("HAP") || param.equals("--HAP") || param.equals("--HAP-LD") || param.equals("--HAP-R2")) {
                    return ILDModel.HAPLOTYPE_LD;
                }
            }

            throw new ParameterException("unable convert " + Arrays.toString(params) + " to " + ILDModel.class);
        })
        .setDescription("Calculate pairwise the linkage disequilibrium (--hap, --hap-ld, --hap-r2) or genotypic correlation (--geno, --geno-ld, --geno-r2)", "'-m <string>'");
```

## 项目实例

- [GBC-1.1](https://pmglab.top/gbc)
- 本示例程序中的 bgzip-tools
