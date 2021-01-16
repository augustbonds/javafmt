package com.augustbonds.javafmt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static void main(String[] args) throws IOException {

        Files.lines(Paths.get("TestClass.java"))
                .map(Lexer::lex)
                .flatMap(Collection::stream)
                .forEach(System.out::println);
    }

    public static List<Token> lex(String input) {
        ArrayList<Token> tokens = new ArrayList<>();

        StringBuilder tokenPatternsBuffer = new StringBuilder();
        for (TokenType tokenType : TokenType.values()) {
            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
        }

        Pattern tokenPatterns = Pattern.compile(tokenPatternsBuffer.substring(1));

        Matcher matcher = tokenPatterns.matcher(input);
        while (matcher.find()) {
            for (TokenType type : TokenType.values()){
                if (type == TokenType.WHITESPACE){
                    continue;
                } else {
                    if (matcher.group(type.name()) != null) {
                        tokens.add(new Token(type, matcher.group(type.name())));
                    }
                }
            }
        }

        return tokens;

    }

    public enum TokenType {
        LINECOMMENT("//.*$"),
        KEYWORD("public|class|void"),
        LEFTPAREN("\\("),
        RIGHTPAREN("\\)"),
        LEFTBRACE("\\{"),
        RIGHTBRACE("\\}"),
        NUMBER("-?[0-9]+"),
        BINARYOP("[*|/|+|-]"),
        NAME("\\w[\\w0-9]*"),
        SEMICOLON(";"),
        WHITESPACE("[ \t\f\r\n]+");

        public final String pattern;

        TokenType(String pattern) {
            this.pattern = pattern;
        }
    }

    public static class Token {
        public final TokenType type;
        public final String data;

        public Token(TokenType type, String data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public String toString() {
            return String.format("(%s %s)",type.name(), data);
        }
    }
}
