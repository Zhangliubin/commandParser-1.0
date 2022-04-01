package edu.sysu.pmglab.commandParser;

import edu.sysu.pmglab.commandParser.converter.IConverter;
import edu.sysu.pmglab.commandParser.converter.value.StringConverter;
import edu.sysu.pmglab.commandParser.validator.IValidator;

import java.util.regex.Pattern;

/**
 * @author suranyi
 * @description 命令权限
 */

public enum CommandOptions {
    /**
     * 必备参数
     */
    REQUEST,

    /**
     * 不在文档中显示该参数
     */
    HIDDEN,

    /**
     * 该指令为 help 指令
     */
    HELP,

    /**
     * 该指令为 debug 指令
     */
    DEBUG;

    /**
     * parser 版本信息
     */
    final static String VERSION = "commandParserV1.0";

    /**
     * 默认参数组名称
     */
    final static String DEFAULT_OPTION_GROUP = "Options";

    /**
     * 参数名规则: 数字、大小写字母、横杠-、下划线_、加号+、@
     */
    final static Pattern COMMAND_NAME_RULE = Pattern.compile("(^[a-zA-Z0-9+_\\-]+$)");

    /**
     * 帮助文档中的预留关键字
     */
    final static String MISS_VALUE = ".";
    final static String SEPARATOR = "\t";

    /**
     *
     */
    final static String HEADER = "#commandName\trequest\tdefault\tconvertTo\tvalidateWith\tarity\tgroup\tdescription\tformat\thidden\thelp\tdebug";

    /**
     * 参数默认值
     */
    final static boolean DEFAULT_REQUEST = false;
    final static boolean DEFAULT_HIDDEN = false;
    final static boolean DEFAULT_HELP = false;
    final static boolean DEFAULT_DEBUG = false;
    final static int DEFAULT_LENGTH = 1;
    final static Object DEFAULT_VALUE = null;
    final static IConverter<String> DEFAULT_CONVERTER = new StringConverter() {
    };
    final static IValidator[] DEFAULT_VALIDATOR = new IValidator[]{};
    final static String DEFAULT_DESCRIPTION = MISS_VALUE;
    final static String DEFAULT_FORMAT = MISS_VALUE;

    static boolean checkCommandName(String commandName) {
        return commandName != null && commandName.length() != 0 && COMMAND_NAME_RULE.matcher(commandName).matches();
    }
}