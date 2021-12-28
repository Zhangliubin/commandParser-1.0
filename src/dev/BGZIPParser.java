package dev;

import edu.sysu.pmglab.suranyi.commandParser.CommandMatcher;
import edu.sysu.pmglab.suranyi.commandParser.CommandParser;
import edu.sysu.pmglab.suranyi.commandParser.CommandRuleType;
import edu.sysu.pmglab.suranyi.commandParser.converter.array.StringArrayConverter;
import edu.sysu.pmglab.suranyi.commandParser.converter.map.NaturalLongRangeConverter;
import edu.sysu.pmglab.suranyi.commandParser.converter.value.PassedInConverter;
import edu.sysu.pmglab.suranyi.commandParser.converter.value.StringConverter;
import edu.sysu.pmglab.suranyi.commandParser.validator.EnsureFileExistsValidator;
import edu.sysu.pmglab.suranyi.commandParser.validator.EnsureFileIsNotDirectoryValidator;
import edu.sysu.pmglab.suranyi.easytools.FileUtils;
import edu.sysu.pmglab.suranyi.unifyIO.bgztools.BGZTools;
import edu.sysu.pmglab.suranyi.unifyIO.partwriter.BGZOutputParam;

import java.io.IOException;
import java.util.Scanner;

import static edu.sysu.pmglab.suranyi.commandParser.CommandOptions.HIDDEN;
import static edu.sysu.pmglab.suranyi.commandParser.CommandOptions.REQUEST;

/**
 * @author suranyi
 * @description BGZIP 工具模式解析器
 */

public class BGZIPParser {
    public static int submit(String... args) throws IOException {
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
                .setDescription("Cut the bgzip file by pointer range (decompressed file).", "'extract <file> -r <start>-<end>'");

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

        CommandMatcher options = mainParser.parse(args);
        CommandParser subParser = getSubParser(options);

        if (subParser == null) {
            System.out.println(mainParser);
        } else {
            options = subParser.parse(args);
            if (options.isPassedIn("-h")) {
                System.out.println(subParser);
            } else {
                if (subParser.containCommandItem("compress")) {
                    String realOutputFileName = options.isPassedIn("-o") ? (String) options.get("-o") : options.get("compress") + ".gz";

                    // 切割文件模式
                    if (!(boolean) options.isPassedIn("-y")) {
                        if (FileUtils.exists(realOutputFileName)) {
                            Scanner scanner = new Scanner(System.in);
                            System.out.print("WARN    " + realOutputFileName + " already exists, do you wish to overwrite? (y or n) ");

                            // 不覆盖文件，则删除该文件
                            if (!scanner.next().trim().equalsIgnoreCase("y")) {
                                throw new IOException("GBC can't create " + realOutputFileName + ": file exists");
                            }
                        }
                    }

                    long start = System.currentTimeMillis();
                    BGZTools.compress((String) options.get("compress"), realOutputFileName + ".~$temp", (int) options.get("-l"), (int) options.get("-t"));
                    long end = System.currentTimeMillis();

                    // 修改文件名
                    FileUtils.rename(realOutputFileName + ".~$temp", realOutputFileName);
                    // 结束任务，输出日志信息
                    System.out.printf("INFO    Total Processing time: %.3f s; BGZ format size: %s%n",
                            (float) (end - start) / 1000, FileUtils.sizeTransformer(FileUtils.sizeOf(realOutputFileName), 3));
                } else if (subParser.containCommandItem("decompress")) {
                    String realOutputFileName;

                    if (!options.isPassedIn("-o")) {
                        String inputFileName = ((String) options.get("decompress"));
                        if (inputFileName.endsWith(".gz")) {
                            realOutputFileName = inputFileName.substring(0, inputFileName.length() - 3);
                        } else {
                            throw new IOException("ERROR   can't remove an extension from " + inputFileName + " -- please rename");
                        }
                    } else {
                        realOutputFileName = (String) options.get("-o");
                    }

                    // 判断输出文件是否存在
                    if (!(boolean) options.isPassedIn("-y")) {
                        if (FileUtils.exists(realOutputFileName)) {
                            Scanner scanner = new Scanner(System.in);
                            System.out.print("WARN    " + realOutputFileName + " already exists, do you wish to overwrite? (y or n) ");

                            // 不覆盖文件，则删除该文件
                            if (!scanner.next().trim().equalsIgnoreCase("y")) {
                                throw new IOException("GBC can't create " + realOutputFileName + ": file exists");
                            }
                        }
                    }

                    long start = System.currentTimeMillis();
                    BGZTools.decompress((String) options.get("decompress"), realOutputFileName + ".~$temp");
                    long end = System.currentTimeMillis();

                    // 修改文件名
                    FileUtils.rename(realOutputFileName + ".~$temp", realOutputFileName);

                    // 结束任务，输出日志信息
                    System.out.printf("INFO    Total Processing time: %.3f s; Output size: %s%n",
                            (float) (end - start) / 1000,
                            FileUtils.sizeTransformer(FileUtils.sizeOf(realOutputFileName), 3));
                } else if (subParser.containCommandItem("md5")) {
                    // 获取 md5 计算器
                    System.out.printf("MD5 (%s, decompressed file) = %s%n", options.get("md5"), BGZTools.md5((String) options.get("md5")));
                } else if (subParser.containCommandItem("convert")) {
                    String realOutputFileName = options.isPassedIn("-o") ? (String) options.get("-o") : (String) options.get("convert");

                    // 切割文件模式
                    if (!(boolean) options.isPassedIn("-y")) {
                        if (FileUtils.exists(realOutputFileName)) {
                            Scanner scanner = new Scanner(System.in);
                            System.out.print("WARN    " + realOutputFileName + " already exists, do you wish to overwrite? (y or n) ");

                            // 不覆盖文件，则删除该文件
                            if (!scanner.next().trim().equalsIgnoreCase("y")) {
                                throw new IOException("GBC can't create " + realOutputFileName + ": file exists");
                            }
                        }
                    }

                    long start = System.currentTimeMillis();
                    BGZTools.convert((String) options.get("convert"), realOutputFileName + ".~$temp", (int) options.get("-l"), (int) options.get("-t"));
                    long end = System.currentTimeMillis();

                    // 修改文件名
                    FileUtils.rename(realOutputFileName + ".~$temp", realOutputFileName);

                    // 结束任务，输出日志信息
                    System.out.printf("INFO    Total Processing time: %.3f s; BGZ format size: %s%n",
                            (float) (end - start) / 1000,
                            FileUtils.sizeTransformer(FileUtils.sizeOf(realOutputFileName), 3));
                } else if (subParser.containCommandItem("concat")) {
                    String realOutputFileName = options.isPassedIn("-o") ? (String) options.get("-o") : ((String[]) options.get("concat"))[0];

                    // 连接多个子文件模式
                    if (!(boolean) options.isPassedIn("-y")) {
                        if (FileUtils.exists(realOutputFileName)) {
                            Scanner scanner = new Scanner(System.in);
                            System.out.print("WARN    " + realOutputFileName + " already exists, do you wish to overwrite? (y or n) ");

                            // 不覆盖文件，则删除该文件
                            if (!scanner.next().trim().equalsIgnoreCase("y")) {
                                throw new IOException("GBC can't create " + realOutputFileName + ": file exists");
                            }
                        }
                    }

                    long start = System.currentTimeMillis();
                    BGZTools.concat(realOutputFileName + ".~$temp", (String[]) options.get("extract"));
                    long end = System.currentTimeMillis();

                    // 修改文件名
                    FileUtils.rename(realOutputFileName + ".~$temp", realOutputFileName);

                    // 结束任务，输出日志信息
                    System.out.printf("INFO    Total Processing time: %.3f s; BGZ format size: %s%n",
                            (float) (end - start) / 1000,
                            FileUtils.sizeTransformer(FileUtils.sizeOf(realOutputFileName), 3));
                } else if (subParser.containCommandItem("extract")) {
                    String realOutputFileName = options.isPassedIn("-o") ? (String) options.get("-o") : (String) options.get("extract");

                    // 切割文件模式
                    if (!(boolean) options.isPassedIn("-y")) {
                        if (FileUtils.exists(realOutputFileName)) {
                            Scanner scanner = new Scanner(System.in);
                            System.out.print("WARN    " + realOutputFileName + " already exists, do you wish to overwrite? (y or n) ");

                            // 不覆盖文件，则删除该文件
                            if (!scanner.next().trim().equalsIgnoreCase("y")) {
                                throw new IOException("GBC can't create " + realOutputFileName + ": file exists");
                            }
                        }
                    }

                    long[] pointer = (long[]) options.get("-r");
                    long start = System.currentTimeMillis();
                    BGZTools.extract((String) options.get("extract"), realOutputFileName + ".~$temp", pointer[0], pointer[1], (int) options.get("-l"), (int) options.get("-t"));
                    long end = System.currentTimeMillis();

                    // 修改文件名
                    FileUtils.rename(realOutputFileName + ".~$temp", realOutputFileName);

                    // 结束任务，输出日志信息
                    System.out.printf("INFO    Total Processing time: %.3f s; BGZ format size: %s%n",
                            (float) (end - start) / 1000,
                            FileUtils.sizeTransformer(FileUtils.sizeOf(realOutputFileName), 3));
                }
            }
        }

        return 0;
    }

    static CommandParser getSubParser(CommandMatcher options) {
        if (options.isPassedIn("compress")) {
            CommandParser subParser = new CommandParser("bgzip compress <input>");
            subParser.register("compress").setOptions(REQUEST, HIDDEN)
                    .defaultTo(new StringConverter() {
                    })
                    .validateWith(EnsureFileExistsValidator.INSTANCE, EnsureFileIsNotDirectoryValidator.INSTANCE);

            subParser.register("--output", "-o")
                    .convertTo(new StringConverter() {
                    })
                    .setDescription("Set the output file.", "'-o <file>'");

            subParser.register("--threads", "-t")
                    .defaultTo(4)
                    .validateWith(1, 10)
                    .setDescription("Set the number of threads for bgzip compression.", "'-t <int>' (" + 1 + "~" + 10 + ")");

            subParser.register("--level", "-l")
                    .defaultTo(BGZOutputParam.DEFAULT_LEVEL)
                    .validateWith(BGZOutputParam.MIN_LEVEL, BGZOutputParam.MAX_LEVEL)
                    .setDescription("Compression level to use for bgzip compression.", "'-l <int>' (0~9)");

            subParser.register("--yes", "-y")
                    .convertTo(new PassedInConverter() {
                    })
                    .setDescription("Overwrite output file without asking.");

            subParser.offset(1);
            return subParser;
        } else if (options.isPassedIn("decompress")) {
            CommandParser subParser = new CommandParser("bgzip decompress <input>");
            subParser.register("decompress").setOptions(REQUEST, HIDDEN)
                    .defaultTo(new StringConverter() {
                    })
                    .validateWith(EnsureFileExistsValidator.INSTANCE, EnsureFileIsNotDirectoryValidator.INSTANCE);

            subParser.register("--output", "-o")
                    .convertTo(new StringConverter() {
                    })
                    .setDescription("Set the output file.", "'-o <file>'");

            subParser.register("--yes", "-y")
                    .convertTo(new PassedInConverter() {
                    })
                    .setDescription("Overwrite output file without asking.");

            subParser.offset(1);
            return subParser;
        } else if (options.isPassedIn("md5")) {
            CommandParser subParser = new CommandParser("bgzip md5 <input>");
            subParser.register("md5").setOptions(REQUEST, HIDDEN)
                    .defaultTo(new StringArrayConverter() {
                    }).validateWith(EnsureFileExistsValidator.INSTANCE, EnsureFileIsNotDirectoryValidator.INSTANCE);

            subParser.offset(1);
            return subParser;
        } else if (options.isPassedIn("convert")) {
            CommandParser subParser = new CommandParser("bgzip convert <input>");
            subParser.register("convert").setOptions(REQUEST, HIDDEN)
                    .defaultTo(new StringConverter() {
                    })
                    .validateWith(EnsureFileExistsValidator.INSTANCE, EnsureFileIsNotDirectoryValidator.INSTANCE);

            subParser.register("--output", "-o")
                    .convertTo(new StringConverter() {
                    })
                    .setDescription("Set the output file.", "'-o <file>'");

            subParser.register("--threads", "-t")
                    .defaultTo(1)
                    .validateWith(1, 10)
                    .setDescription("Set the number of threads for bgzip compression.", "'-t <int>' (" + 1 + "~" + 10 + ")");

            subParser.register("--level", "-l")
                    .defaultTo(BGZOutputParam.DEFAULT_LEVEL)
                    .validateWith(BGZOutputParam.MIN_LEVEL, BGZOutputParam.MAX_LEVEL)
                    .setDescription("Compression level to use for bgzip compression.", "'-l <int>' (0~9)");

            subParser.register("--yes", "-y")
                    .convertTo(new PassedInConverter() {
                    })
                    .setDescription("Overwrite output file without asking.");

            subParser.offset(1);
            return subParser;
        } else if (options.isPassedIn("concat")) {
            CommandParser subParser = new CommandParser("bgzip concat <input(s)>");
            subParser.register("concat").setOptions(REQUEST, HIDDEN)
                    .defaultTo(new StringArrayConverter() {
                    })
                    .validateWith(EnsureFileExistsValidator.INSTANCE, EnsureFileIsNotDirectoryValidator.INSTANCE);

            subParser.register("--output", "-o")
                    .convertTo(new StringConverter() {
                    })
                    .setDescription("Set the output file.", "'-o <file>'");

            subParser.register("--yes", "-y")
                    .convertTo(new PassedInConverter() {
                    })
                    .setDescription("Overwrite output file without asking.");

            subParser.offset(1);
            return subParser;
        } else if (options.isPassedIn("extract")) {
            CommandParser subParser = new CommandParser("bgzip extract <input> --range <start-end>");
            subParser.register("extract").setOptions(REQUEST, HIDDEN)
                    .validateWith(EnsureFileExistsValidator.INSTANCE, EnsureFileIsNotDirectoryValidator.INSTANCE);

            subParser.register("--range", "-r").setOptions(REQUEST)
                    .convertTo(new NaturalLongRangeConverter() {
                    })
                    .setDescription("Set the range of the file pointer (decompressed file).", "-r <start>-<end>");

            subParser.register("--output", "-o")
                    .convertTo(new StringConverter() {
                    })
                    .setDescription("Set the output file.", "'-o <file>'");

            subParser.register("--threads", "-t")
                    .defaultTo(4)
                    .validateWith(1, 10)
                    .setDescription("Set the number of threads for bgzip compression.", "'-t <int>' (" + 1 + "~" + 10 + ")");

            subParser.register("--level", "-l")
                    .defaultTo(BGZOutputParam.DEFAULT_LEVEL)
                    .validateWith(BGZOutputParam.MIN_LEVEL, BGZOutputParam.MAX_LEVEL)
                    .setDescription("Compression level to use for bgzip compression.", "'-l <int>' (0~9)");

            subParser.register("--yes", "-y")
                    .convertTo(new PassedInConverter() {
                    })
                    .setDescription("Overwrite output file without asking.");

            subParser.offset(1);
            return subParser;
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        BGZIPParser.submit(args);
    }
}