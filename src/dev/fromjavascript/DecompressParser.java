package dev.fromjavascript;

import edu.sysu.pmglab.commandParser.CommandMatcher;
import edu.sysu.pmglab.commandParser.CommandOptions;
import edu.sysu.pmglab.commandParser.CommandParser;
import edu.sysu.pmglab.commandParser.converter.value.PassedInConverter;
import edu.sysu.pmglab.commandParser.converter.value.StringConverter;
import edu.sysu.pmglab.commandParser.validator.EnsureFileExistsValidator;
import edu.sysu.pmglab.commandParser.validator.EnsureFileIsNotDirectoryValidator;

enum DecompressParser {
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

    DecompressParser() {
        // global options
        parser = new CommandParser(false);
        parser.setProgramName("bgzip decompress <file>");
        parser.offset(1);
        parser.debug(false);
        parser.registerGlobalRule(null);

        // add commandItems
        parser.register("--help", "-help", "-h")
              .addOptions(CommandOptions.HIDDEN, CommandOptions.HELP)
              .arity(0)
              .convertTo(new PassedInConverter() {})
              .setOptionGroup("Options");
        parser.register("decompress")
              .addOptions(CommandOptions.REQUEST, CommandOptions.HIDDEN)
              .arity(1)
              .convertTo(new StringConverter() {})
              .setDefaultByConverter("string")
              .validateWith(EnsureFileExistsValidator.INSTANCE, EnsureFileIsNotDirectoryValidator.INSTANCE)
              .setOptionGroup("Options");
        parser.register("--output", "-o")
              .arity(1)
              .convertTo(new StringConverter() {})
              .setOptionGroup("Options")
              .setDescription("Set the output file.")
              .setFormat("-o <file>");
        parser.register("--yes", "-y")
              .arity(0)
              .convertTo(new PassedInConverter() {})
              .setOptionGroup("Options")
              .setDescription("Overwrite output file without asking.");
    }
}
