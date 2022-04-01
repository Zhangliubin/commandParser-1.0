package dev.fromjavascript;

import edu.sysu.pmglab.commandParser.CommandMatcher;
import edu.sysu.pmglab.commandParser.CommandOptions;
import edu.sysu.pmglab.commandParser.CommandParser;
import edu.sysu.pmglab.commandParser.converter.map.NaturalLongRangeConverter;
import edu.sysu.pmglab.commandParser.converter.value.IntConverter;
import edu.sysu.pmglab.commandParser.converter.value.PassedInConverter;
import edu.sysu.pmglab.commandParser.converter.value.StringConverter;
import edu.sysu.pmglab.commandParser.validator.EnsureFileExistsValidator;
import edu.sysu.pmglab.commandParser.validator.EnsureFileIsNotDirectoryValidator;
import edu.sysu.pmglab.commandParser.validator.RangeValidator;

enum ExtractParser {
    /**
     * single instance
     */
    INSTANCE;

    final CommandParser parser;

    public static CommandParser getParser() {
        return INSTANCE.parser;
    }

    public static CommandMatcher parse(String... args) {
        return INSTANCE.parser.parse(args);
    }

    public static void toFile(String fileName) {
        INSTANCE.parser.toFile(fileName);
    }

    ExtractParser() {
        // global options
        parser = new CommandParser(false);
        parser.setProgramName("bgzip extract <file> --range <start-end>");
        parser.offset(1);
        parser.debug(false);
        parser.registerGlobalRule(null);

        // add commandItems
        parser.register("--help", "-help", "-h")
              .addOptions(CommandOptions.HIDDEN, CommandOptions.HELP)
              .arity(0)
              .convertTo(new PassedInConverter() {})
              .setOptionGroup("Options");
        parser.register("extract")
              .addOptions(CommandOptions.REQUEST, CommandOptions.HIDDEN)
              .arity(1)
              .convertTo(new StringConverter() {})
              .validateWith(EnsureFileExistsValidator.INSTANCE, EnsureFileIsNotDirectoryValidator.INSTANCE)
              .setOptionGroup("Options");
        parser.register("--range", "-r")
              .addOptions(CommandOptions.REQUEST)
              .arity(1)
              .convertTo(new NaturalLongRangeConverter() {})
              .setOptionGroup("Options")
              .setDescription("Set the range of the file pointer (decompressed file).")
              .setFormat("-r <start>-<end>");
        parser.register("--output", "-o")
              .arity(1)
              .convertTo(new StringConverter() {})
              .setOptionGroup("Options")
              .setDescription("Set the output file.")
              .setFormat("-o <file>");
        parser.register("--threads", "-t")
              .arity(1)
              .convertTo(new IntConverter() {})
              .setDefaultByConverter("4")
              .validateWith(new RangeValidator(1.0, 10.0))
              .setOptionGroup("Options")
              .setDescription("Set the number of threads for bgzip compression.")
              .setFormat("-t <int, 1-10>");
        parser.register("--level", "-l")
              .arity(1)
              .convertTo(new IntConverter() {})
              .setDefaultByConverter("5")
              .validateWith(new RangeValidator(0.0, 9.0))
              .setOptionGroup("Options")
              .setDescription("Compression level to use for bgzip compression.")
              .setFormat("-l <int, 0-9>");
        parser.register("--yes", "-y")
              .arity(0)
              .convertTo(new PassedInConverter() {})
              .setOptionGroup("Options")
              .setDescription("Overwrite output file without asking.");
    }
}
