/* Lexical analyzer system for Python and Java */

import java.util.*;

public class LexicalAnalyzer {
    // Character classes
    private static final int LETTER = 0;
    private static final int DIGIT = 1;
    private static final int QUOTE = 2;
    private static final int UNKNOWN = 99;

    // Token codes
    private static final int KEYWORD = 10;
    private static final int IDENTIFIER = 11;
    private static final int INT_LITERAL = 12;
    private static final int FLOAT_LITERAL = 13;
    private static final int STRING_LITERAL = 14;
    private static final int CHAR_LITERAL = 15;
    private static final int ASSIGN_OP = 20;
    private static final int ADD_OP = 21;
    private static final int SUB_OP = 22;
    private static final int MULT_OP = 23;
    private static final int DIV_OP = 24;
    private static final int LEFT_PAREN = 25;
    private static final int RIGHT_PAREN = 26;
    private static final int LEFT_BRACE = 27;
    private static final int RIGHT_BRACE = 28;
    private static final int LEFT_BRACKET = 29;
    private static final int RIGHT_BRACKET = 30;
    private static final int SEMICOLON = 31;
    private static final int COMMA = 32;
    private static final int COLON = 33;
    private static final int DOT = 34;

    // Instance variables
    private int charClass;
    private StringBuilder lexeme;
    private char nextChar;
    private int token;
    private int nextToken;
    private int lineNum;
    private int colNum;
    private String inputBuffer;
    private int bufferPos;
    private int bufferLen;
    private String currentLanguage;

    // Keyword sets
    private static final Set<String> pythonKeywords = new HashSet<>(Arrays.asList(
            "def", "class", "if", "else", "elif", "while", "for", "in", "return",
            "import", "from", "as", "try", "except", "finally", "with", "pass",
            "break", "continue", "and", "or", "not", "is", "None", "True", "False",
            "print", "range", "len", "str", "int", "float", "list", "dict"
    ));

    private static final Set<String> javaKeywords = new HashSet<>(Arrays.asList(
            "public", "private", "protected", "static", "final", "abstract", "class",
            "interface", "extends", "implements", "void", "int", "float", "double",
            "char", "boolean", "byte", "short", "long", "String", "if", "else",
            "while", "for", "do", "switch", "case", "default", "break", "continue",
            "return", "try", "catch", "finally", "throw", "throws", "new", "this",
            "super", "null", "true", "false", "System", "out", "println", "main"
    ));

    // Counters for summary
    private int keywordCount = 0;
    private int identifierCount = 0;
    private int literalCount = 0;

    public static void main(String[] args) {
        LexicalAnalyzer analyzer = new LexicalAnalyzer();

        System.out.println("Lexical Analyzer for Python and Java");
        System.out.println("");

        // Python test code
        String pythonCode =
                "def add_numbers(num1, num2):\n" +
                        "    sum = num1 + num2\n" +
                        "    print('Sum: ',sum)";

        // Java test code
        String javaCode =
                "public class JavaExample {\n" +
                        "  public static void main(String[] args) {\n" +
                        "    int num1 = 5, num2 = 15,sum;\n" +
                        "        sum = num1+num2;\n" +
                        "        System.out.println(\"Sum of \"+num1+\" and \"+num2+\" is: \"+sum);\n" +
                        "  }\n" +
                        "}";

        // Analyze Python code
        analyzer.analyzeCode(pythonCode, "Python");

        System.out.println("\n-----------------------------------------------------------------\n");

        // Analyze Java code
        analyzer.analyzeCode(javaCode, "Java");
    }

    /* analyzeCode - a function to analyze the given source code and process its tokens*/

    public void analyzeCode(String code, String language) {
        this.currentLanguage = language;
        this.inputBuffer = code;
        this.bufferLen = code.length();
        this.bufferPos = 0;
        this.lineNum = 1;
        this.colNum = 1;
        this.keywordCount = 0;
        this.identifierCount = 0;
        this.literalCount = 0;
        this.lexeme = new StringBuilder();

        printHeader(language);
        System.out.println(code + "\n");

        System.out.println("LEXICAL ANALYSIS:");
        System.out.println("------------------");
        System.out.printf("%-15s %-20s %-10s%n", "TOKEN TYPE", "LEXEME", "LINE");
        System.out.printf("%-15s %-20s %-10s%n", "----------", "------", "----");

        getChar();
        do {
            lex();
        } while (nextToken != -1);  /* Continue until EOF */

        printSummary();
    }


    /* printHeader - a function to print the analysis   */


    private void printHeader(String language) {
        System.out.println("ANALYZING " + language + " SOURCE CODE");
        String underline = language.equals("Python") ? "-----------------" : "-----------------";
        System.out.println(underline);
    }

    /* printSummary - a function to print the analysis summary with token counts */

    private void printSummary() {
        System.out.println();
        System.out.println("RESULTS:");
        System.out.println("--------");
        System.out.println("Amount of Keywords found:    " + keywordCount);
        System.out.println("Amount of Identifiers found: " + identifierCount);
        System.out.println("Amount of Literals found:    " + literalCount);
    }

    /* lookup - a function to lookup operators and parentheses and return the token */

    private int lookup(char ch) {
        switch (ch) {
            case '(':
                addChar();
                nextToken = LEFT_PAREN;
                break;
            case ')':
                addChar();
                nextToken = RIGHT_PAREN;
                break;
            case '{':
                addChar();
                nextToken = LEFT_BRACE;
                break;
            case '}':
                addChar();
                nextToken = RIGHT_BRACE;
                break;
            case '[':
                addChar();
                nextToken = LEFT_BRACKET;
                break;
            case ']':
                addChar();
                nextToken = RIGHT_BRACKET;
                break;
            case '+':
                addChar();
                nextToken = ADD_OP;
                break;
            case '-':
                addChar();
                nextToken = SUB_OP;
                break;
            case '*':
                addChar();
                nextToken = MULT_OP;
                break;
            case '/':
                addChar();
                nextToken = DIV_OP;
                break;
            case '=':
                addChar();
                nextToken = ASSIGN_OP;
                break;
            case ';':
                addChar();
                nextToken = SEMICOLON;
                break;
            case ',':
                addChar();
                nextToken = COMMA;
                break;
            case ':':
                addChar();
                nextToken = COLON;
                break;
            case '.':
                addChar();
                nextToken = DOT;
                break;
            default:
                addChar();
                nextToken = UNKNOWN;
                break;
        }
        return nextToken;
    }

    /* isKeyword - a function to check if a word is a keyword in the current language  */

    private boolean isKeyword(String word) {
        if (currentLanguage.equals("Python")) {
            return pythonKeywords.contains(word);
        } else {
            return javaKeywords.contains(word);
        }
    }

    /* addChar - a function to add nextChar to lexeme   */

    private void addChar() {
        lexeme.append(nextChar);
    }

    /* getChar - a function to get the next character of input and determine its character class */

    private void getChar() {
        if (bufferPos < bufferLen) {
            nextChar = inputBuffer.charAt(bufferPos++);
            if (nextChar == '\n') {
                lineNum++;
                colNum = 1;
            } else {
                colNum++;
            }

            if (Character.isLetter(nextChar) || nextChar == '_') {
                charClass = LETTER;
            } else if (Character.isDigit(nextChar)) {
                charClass = DIGIT;
            } else if (nextChar == '"' || nextChar == '\'') {
                charClass = QUOTE;
            } else {
                charClass = UNKNOWN;
            }
        } else {
            nextChar = (char) -1;  /* EOF marker */
            charClass = -1;
        }
    }


    /* getNonBlank - a function to call getChar until it returns a non-whitespace character */

    private void getNonBlank() {
        while (Character.isWhitespace(nextChar) && nextChar != (char) -1) {
            getChar();
        }
    }

    /* printTokenInfo - a function to print token and information to the output */

    private void printTokenInfo() {
        String tokenName;

        switch (nextToken) {
            case KEYWORD:
                tokenName = "KEYWORD";
                keywordCount++;
                break;
            case IDENTIFIER:
                tokenName = "IDENTIFIER";
                identifierCount++;
                break;
            case INT_LITERAL:
                tokenName = "INT_LITERAL";
                literalCount++;
                break;
            case FLOAT_LITERAL:
                tokenName = "FLOAT_LITERAL";
                literalCount++;
                break;
            case STRING_LITERAL:
                tokenName = "STRING_LITERAL";
                literalCount++;
                break;
            case CHAR_LITERAL:
                tokenName = "CHAR_LITERAL";
                literalCount++;
                break;
            case ADD_OP:
                tokenName = "ADD_OP";
                break;
            case SUB_OP:
                tokenName = "SUB_OP";
                break;
            case MULT_OP:
                tokenName = "MULT_OP";
                break;
            case DIV_OP:
                tokenName = "DIV_OP";
                break;
            case ASSIGN_OP:
                tokenName = "ASSIGN_OP";
                break;
            case LEFT_PAREN:
                tokenName = "LEFT_PAREN";
                break;
            case RIGHT_PAREN:
                tokenName = "RIGHT_PAREN";
                break;
            case LEFT_BRACE:
                tokenName = "LEFT_BRACE";
                break;
            case RIGHT_BRACE:
                tokenName = "RIGHT_BRACE";
                break;
            case SEMICOLON:
                tokenName = "SEMICOLON";
                break;
            case COMMA:
                tokenName = "COMMA";
                break;
            case COLON:
                tokenName = "COLON";
                break;
            case DOT:
                tokenName = "DOT";
                break;
            default:
                tokenName = "UNKNOWN";
                break;
        }

        if (nextToken != -1) {
            System.out.printf("%-15s %-20s %-10d%n",
                    tokenName, lexeme.toString(), lineNum);
        }
    }

    /* lex - a simple lexical analyzer for Python and Java  */

    private int lex() {
        lexeme.setLength(0);
        getNonBlank();

        switch (charClass) {
            /* Parse identifiers and keywords */
            case LETTER:
                addChar();
                getChar();
                while (charClass == LETTER || charClass == DIGIT) {
                    addChar();
                    getChar();
                }

                /* Check if the identifier is a keyword */
                if (isKeyword(lexeme.toString())) {
                    nextToken = KEYWORD;
                } else {
                    nextToken = IDENTIFIER;
                }
                break;

            /* Parse integer and float literals */
            case DIGIT:
                addChar();
                getChar();
                while (charClass == DIGIT) {
                    addChar();
                    getChar();
                }

                /* Check for decimal point to identify floats */
                if (nextChar == '.') {
                    addChar();
                    getChar();
                    while (charClass == DIGIT) {
                        addChar();
                        getChar();
                    }
                    nextToken = FLOAT_LITERAL;
                } else {
                    nextToken = INT_LITERAL;
                }
                break;

            /* Parse string and character literals */
            case QUOTE:
                char quoteType = nextChar;
                addChar();
                getChar();
                while (nextChar != quoteType && nextChar != (char) -1) {
                    addChar();
                    getChar();
                }
                if (nextChar == quoteType) {
                    addChar();
                    getChar();
                }

                /* Determine if it's a character or string literal */
                if (lexeme.length() == 3 && lexeme.charAt(0) == lexeme.charAt(2)) {
                    nextToken = CHAR_LITERAL;
                } else {
                    nextToken = STRING_LITERAL;
                }
                break;

            /* Parentheses, operators, and delimiters */
            case UNKNOWN:
                lookup(nextChar);
                getChar();
                break;

            /* EOF */
            case -1:
                nextToken = -1;
                lexeme.append("EOF");
                break;
        }

        printTokenInfo();
        return nextToken;
    }
}
