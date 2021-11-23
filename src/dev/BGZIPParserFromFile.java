package dev;

import edu.sysu.pmglab.suranyi.commandParser.CommandMatcher;
import edu.sysu.pmglab.suranyi.commandParser.CommandParser;
import edu.sysu.pmglab.suranyi.easytools.FileUtils;
import edu.sysu.pmglab.suranyi.unifyIO.bgztools.BGZTools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * @author suranyi
 * @description BGZIP 工具模式解析器
 */

public class BGZIPParserFromFile {
    public static int submit(String... args) throws IOException {
        CommandParser mainParser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/bgzip");
        CommandMatcher options = mainParser.parse(args);
        CommandParser subParser = getSubParser(options);

        if (subParser == null) {
            System.out.println(mainParser);
        } else {
            options = subParser.parse(args);
            if (options.isPassedIn("-h")) {
                System.out.println(subParser);
            } else {
                if (options.contain("compress")) {
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
                } else if (options.contain("decompress")) {
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
                } else if (options.contain("md5")) {
                    // 获取 md5 计算器
                    System.out.printf("MD5 (%s, decompressed file) = %s%n", options.get("md5"), BGZTools.md5((String) options.get("md5")));
                } else if (options.contain("convert")) {
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
                } else if (options.contain("concat")) {
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
                } else if (options.contain("extract")) {
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

    static CommandParser getSubParser(CommandMatcher options) throws IOException {
        if (options.isPassedIn("compress")) {
            CommandParser subParser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/compress");
            return subParser;
        } else if (options.isPassedIn("decompress")) {
            CommandParser subParser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/decompress");
            return subParser;
        } else if (options.isPassedIn("md5")) {
            CommandParser subParser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/md5");
            return subParser;
        } else if (options.isPassedIn("convert")) {
            CommandParser subParser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/convert");
            return subParser;
        } else if (options.isPassedIn("concat")) {
            CommandParser subParser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/concat");
            return subParser;
        } else if (options.isPassedIn("extract")) {
            CommandParser subParser = CommandParser.loadFromInnerResource(BGZIPParserFromFile.class, "/command/extract");
            return subParser;
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        BGZIPParserFromFile.submit(args);
    }
}