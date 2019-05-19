package be.vsop;

import be.vsop.exceptions.LexerException;
import be.vsop.lexer.VSOPLexer;
import be.vsop.tokens.Token;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * This is the main class of our VSOP compiler, it parses the arguments, calls the relevant functions, and outputs
 * what has been asked (that depends on the arguments)
 */
public class vsopc {
    // This class is used to redirect runtime commands' outputs to stdout / stderr
    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    public static void main(String[] args) {
        // parse the program arguments. Note that we check for a -dir argument, which contains the absolute path
        // to the vsopcompiler folder. This was needed to compliant with the submission platform
        String fileName = null;
        String mode = "";
        boolean skipNext = false;
        String languageDirPath = ".";
        for (String arg : args) {
            if (!skipNext) {
                if (arg.startsWith("-")) {
                    if (arg.equals("-dir")) {
                        skipNext = true;
                        continue;
                    }
                    mode = arg;
                } else {
                    fileName = arg;
                }
            } else {
                languageDirPath = arg;
                skipNext = false;
            }
        }

        if (fileName == null) {
            System.err.println("Missing Argument : path to the file to parse");
            System.exit(-1);
        }

        FileReader reader = null;
        try {
            reader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.err.println("In vsopc.java, reading input file : file " + fileName + " not found.");
            System.exit(-1);
        }

        Compiler compiler = new Compiler(fileName, languageDirPath);

        if (mode.contentEquals("-lex")) {
            VSOPLexer lexer = new VSOPLexer(reader);
            try {
                while (true) {
                    Token t = lexer.yylex();
                    if (t == null)
                        break;

                    if(t.getTokenType() == Token.Tokens.STRING_LITERAL)
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName() + "," +
                                convertToEscapeSymbols(t.getValue()));
                    else if (t.getValue() != null)
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName() + "," + t.getValue());
                    else
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName());

                }
            }catch (LexerException e){
                System.err.println(fileName+":"+e.getLine()+":"+e.getColumn()+": lexical error :" + e.getMessage());
                System.exit(-1);
            }catch (IOException e){
                System.err.println("IOException during lexing in " + fileName);
                System.exit(-1);
            }
            System.exit(0);
        }

        if (mode.contentEquals("-parse")) {
            /*TODO improve errors : for instance if a className does not start with an uppercase letter we get :
             * tests/test.vsop:7:7: be.vsop.semantic error :Symbol found is : IDENTIFIER expected Symbols are [] */
            compiler.buildAST().print(false);
        }

        else if(mode.contentEquals("-check")) {
            if (args.length > 2) {
                compiler.doSemanticAnalysis(null, languageDirPath + "/language/");
            } else {
                compiler.doSemanticAnalysis(null, null);
            }
        }

        else if(mode.contentEquals("-llvm")) {
            if (args.length > 2) {
                compiler.doSemanticAnalysis(null, languageDirPath + "/language/", false);
            } else {
                compiler.doSemanticAnalysis(null, null, false);
            }

            String llvm = compiler.generateLlvm();
            System.out.println(llvm);

        } else {
            // generate executable
            if (args.length > 2) {
                compiler.doSemanticAnalysis(null, languageDirPath + "/language/", false);
            } else {
                compiler.doSemanticAnalysis(null, null, false);
            }

            String llvm = compiler.generateLlvm();

            String executableFileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.indexOf(".vsop"));
            try {
                // Write llvm code in a temporary file
                File tmpLlvmFile = File.createTempFile("vsop", executableFileName + ".ll");

                tmpLlvmFile.deleteOnExit();
                FileWriter writer = new FileWriter(tmpLlvmFile, false);
                writer.write(llvm);
                writer.close();

                // Executes a clang command to generate an executable
                boolean isWindows = System.getProperty("os.name")
                        .toLowerCase().startsWith("windows");
                Process process;

                if (isWindows) {
                    // The windows subsystem for linux needs to be activated for this to work on a Windows OS
                    String[] cmd = {"wsl", "clang", "-Wno-override-module", "-o", executableFileName,
                            windowsToWslAbsolutePath(tmpLlvmFile.getAbsolutePath())};
                    process = Runtime.getRuntime().exec(cmd);
                } else {
                    String[] cmd = {"clang", "-Wno-override-module", "-o", executableFileName, tmpLlvmFile.getAbsolutePath()};
                    process = Runtime.getRuntime().exec(cmd);
                }

                // Consumes process output stream with stdout (and stderr)
                StreamGobbler streamGobbler =
                        new StreamGobbler(process.getInputStream(), System.out::println);
                Executors.newSingleThreadExecutor().submit(streamGobbler);
                streamGobbler =
                        new StreamGobbler(process.getErrorStream(), System.err::println);
                Executors.newSingleThreadExecutor().submit(streamGobbler);

                // Check if process executed normally
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.err.println("cmd creating executable exited with error");
                    System.exit(-1);
                }

                // After a call to getRuntime, auto-exit is disabled (or seems to be)
                System.exit(0);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.err.println("In vsopc while generating executable");
                System.exit(-1);
            }
        }

    }

    /**
     * Convert non-printable characters to their escape sequences
     *
     * @param str the string
     * @return the string with non-printable characters converted
     */
    private static String convertToEscapeSymbols(String str){
        String result = str;
        result = result.replace("\\b", "\\x08");
        result = result.replace("\\f", "\\x0c");
        result = result.replace("\\n", "\\x0a");
        result = result.replace("\\r", "\\x0d");
        result = result.replace("\\t", "\\x09");
        result = result.replace("\\v", "\\x0b");

        return result;
    }

    /**
     * Converts a windows path, for instance D:\Documents\foo\file, to a windows subsystem for linux equivalent path.
     * It would give /mnt/d/Documents/foo/file if given our example
     *
     * @param windowsPath the windows path as a string
     *
     * @return the equivalent Windows Subsystem for Linux path
     */
    private static String windowsToWslAbsolutePath(String windowsPath) {
        StringBuilder unixPath = new StringBuilder();
        // A windows fileName can't contain : (in theory, we could face problems with wsl)
        int columnIndex = windowsPath.indexOf(":");
        if (columnIndex == 1) {
            String driver = windowsPath.substring(0, 1).toLowerCase();
            unixPath.append("/mnt/").append(driver);
            windowsPath = windowsPath.substring(columnIndex + 1);
        }
        unixPath.append(windowsPath.replace("\\", "/"));
        return  unixPath.toString();
    }

}