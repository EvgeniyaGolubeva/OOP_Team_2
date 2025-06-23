package com.the_meow.blog_service.utils;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownUtil {
    public static String markdownToHtml(String markdownText) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
