package edu.mtholyoke.cs341bd.bookz;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class HTMLView {

    private String metaURL;

    public HTMLView(String baseURL) {
        this.metaURL = "<base href=\"" + baseURL + "\">";
    }

    /**
     * HTML top boilerplate; put in a function so that I can use it for all the
     * pages I come up with.
     *
     * @param html  where to write to; get this from the HTTP response.
     * @param title the title of the page, since that goes in the header.
     */
    void printPageStart(PrintWriter html, String title) {
        html.println("<!DOCTYPE html>"); // HTML5
        html.println("<html>");
        html.println("  <head>");
        html.println("    <title>" + title + "</title>");
        html.println("    " + metaURL);
        html.println("    <link type=\"text/css\" rel=\"stylesheet\" href=\"" + getStaticURL("bookz.css") + "\">");
        html.println("<link href=\"https://fonts.googleapis.com/css?family=Amatic+SC|Dancing+Script|Gloria+Hallelujah|Indie+Flower|Shadows+Into+Light\" rel=\"stylesheet\">");
        html.println("    <script type=\"text/javascript\" src=\"" + "scripts/modal.js\" defer></script>");
        html.println("  </head>");
        html.println("  <body>");
    }

    void printSearchBar(PrintWriter html) {
        html.println("<ul class=\"topnav\">");
        html.println("<li>");
        html.println(" <form action=\"search\" method=\"GET\" class=\" validate search\">");
        html.println("<div class=\"input-group\">");
        html.println("<input type=\"text\" class=\"form-control search\" placeholder=\"Search\" name=\"query\">");
        html.println("<span class=\"input-group-btn\">");
        html.println("<button class=\"btn btn-theme searchB\" type=\"submit\" id=\"navButton\"");
        html.println(">Search</button>");
        html.println("</span>");
        html.println("</div>");
        html.println("</form>");
        html.println("</li>");
        html.println("<li id=\"logo\">Bookz</li>");
        html.println("</ul>");

    }

    public String getStaticURL(String resource) {
        return "static/" + resource;
    }

    /**
     * HTML bottom boilerplate; close all the tags we open in
     * printPageStart.
     *
     * @param html where to write to; get this from the HTTP response.
     */
    void printPageEnd(PrintWriter html) {
        html.println("  </body>");
        html.println("</html>");
    }

    public void showFlag(PrintWriter output, GutenbergBook book) {
        output.println("  <div id=\"flagModal\" class=\"modal\">");
        output.println("    <div class=\"modal-content\">");
        output.println("      <div id=\"top\">");
        output.println("      <span class=\"close\">&times;</span>");
        output.println("      <h1>Flag an issue</h1>");
        output.println("      </div>");
        output.println("      <form action=\"submit\" method=\"POST\">");
        output.println("       <div>");
        output.println("        <div class=\"side\">");
        output.println("           <label>Name:</label>");
        output.println("           <input type=\"text\" name=\"name\">");
        output.println("        </div>");
        output.println("<input type=\"hidden\" name=\"book\" value=\"" + book.title + "\"/>");
        output.println("       </div>");
        output.println("        <label>Error:</label>");
        output.println("        <textarea name=\"error\"></textarea><br>");
        output.println("        <input type=\"submit\" value=\"Submit\" id=\"submit\">");
        output.println("      </form>");
        output.println("    </div>");
        output.println("  </div>");
    }

    void showSearchResultsPage(HashSet<GutenbergBook> books, HttpServletResponse resp) throws IOException {
        try (PrintWriter html = resp.getWriter()) {
            printPageStart(html, "Bookz");
            printSearchBar(html);
            int count = 0;
            html.append("<div class=\"wrapper\">");
            html.append("<div class=\"row\">");
            for (GutenbergBook book : books) {
                showFlag(html, book);
                if (count > 4) {
                    count = 0;
                    html.append("</div>");
                    html.append("<<div class=\"row\">");
                }
                count++;
                html.append("<div class=\"column\" style=\"background:" + getRandomColor() + "; border: solid " + getSpineColors() + "; border-width: 0 0 0 10pt;\" " +
                        ">" +
                        "<p class=\"title\" style=\"font-family: '" + getRandomFont() + "; \">" + book.title + "<p>" +
                        "<p class=\"info\" > <button class=\"flag\">&#9654;</button><br><br>" + book.creator + "<br><br><a class=\"url\" href='" + book.getGutenbergURL() + "'>On Project Gutenberg</a><br><p>" +
                        "</div>");
            }
            if (count < 6) {
                html.append("</div>");
                html.append("</div>");
            }
            html.append("</div>");
            printPageEnd(html);
        }
    }

    public void printSumbitted(PrintWriter html) {
        html.println("<div id=\"submitted\"><p > Your issue has been added to our error log. We will try to fix it ASAP!  </p></div>");
    }

    void showFrontPage(Model model, HttpServletResponse resp) throws IOException {
        try (PrintWriter html = resp.getWriter()) {
            printPageStart(html, "Bookz");
            printSearchBar(html);

            List<GutenbergBook> randomBooks = model.getRandomBooks(15);
            printBooks(html, randomBooks);


            printPageEnd(html);
        }
    }

    public void printBooks(PrintWriter html, List<GutenbergBook> randomBooks) {
        int count = 0;
        html.append("<div class=\"wrapper\">");
        html.append("<div class=\"row\">");
        for (GutenbergBook book : randomBooks) {
            showFlag(html, book);
            if (count > 4) {
                count = 0;
                html.append("</div>");
                html.append("<<div class=\"row\">");
            }
            count++;
            html.append("<div class=\"column\" style=\"background:" + getRandomColor() + "; border: solid " + getSpineColors() + "; border-width: 0 0 0 10pt;\" " +
                    ">" +
                    "<p class=\"title\" style=\"font-family: '" + getRandomFont() + "; \">" + book.title + "<p>" +
                    "<p class=\"info\" > <button class=\"flag\">&#9654;</button><br><br>" + book.creator + "<br><br><a class=\"url\" href='" + book.getGutenbergURL() + "'>On Project Gutenberg</a><br><p>" +
                    "</div>");
        }
        if (count < 6) {
            html.append("</div>");
            html.append("</div>");
        }
        html.append("<div class=\"page\">");
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            html.println("<a href='/title/" + letter + "'>" + letter + "</a> ");
        }
        html.append("<br><br><br>");
        html.append("</div>");
        html.append("</div>");

    }

    public String getRandomColor() {
        String[] colors = {"#9FC6C1", "#6F7869", "#8A8184", "#E3A7A3", "#F1D0CA", "#EFF0DA"};
        Random rand = new Random();
        int n = rand.nextInt(colors.length);
        String color = colors[n];
        return color;
    }

    public String getSpineColors() {
        String[] colors = {"#635647", "#4D3925", "#A1703B", "#73502E", "#594F44", "#544A41"};
        Random rand = new Random();
        int n = rand.nextInt(colors.length);
        String color = colors[n];
        return color;
    }

    public String getRandomFont() {
        String[] colors = {"Amatic SC', cursive", "Indie Flower', cursive", "Gloria Hallelujah', cursive", "Shadows Into Light', cursive", "Dancing Script', cursive"};
        Random rand = new Random();
        int n = rand.nextInt(colors.length);
        String color = colors[n];
        return color;
    }

    public void showBookPage(GutenbergBook book, HttpServletResponse resp) throws IOException {
        try (PrintWriter html = resp.getWriter()) {
            printPageStart(html, "Bookz");
            printBookHTML(html, book);
            printPageEnd(html);
        }
    }

    public void printBookHTML(PrintWriter html, GutenbergBook book) {
        html.println("<div class='book'>");
        html.println("<a class='none' href='/book/" + book.id + "'>");
        html.println("<div class='title'>" + book.title + "</div>");
        html.println("  <h1\">flag <button class=\"flag\">\t&#9660;</button> </h1>");
        if (book.creator != null) {
            html.println("<div class='creator'>" + book.creator + "</div>");
        }
        html.println("<a href='" + book.getGutenbergURL() + "'>On Project Gutenberg</a>");
        // TODO, finish up fields.
        html.println("</a>");
        html.println("</div>");
    }

    public void showBookCollection(List<GutenbergBook> theBooks, HttpServletResponse resp, char firstChar) throws IOException {
        try (PrintWriter html = resp.getWriter()) {
            printPageStart(html, "Bookz");
            printSearchBar(html);
            html.println("<h2>Results starting with " + firstChar + "... ( " + theBooks.size() + " ) </h2>");

            printBooks(html, theBooks.subList(0, 20));

            printPageEnd(html);
        }
    }


    public void printResults(PrintWriter html) {

    }

    public String boldSearchTerms(HashSet<String> searchTerms, String title) {
        StringBuilder html = new StringBuilder();
        for (String term : title.split(" ")) {
            if (searchTerms.contains(term)) {
                html.append("<b> " + term + " </b>");
            } else {
                html.append(" " + term + " ");
            }
        }
        return html.toString();

    }

    public static String encodeParametersInURL(Map<String, String> params, String url) {
        StringBuilder output = new StringBuilder();
        output.append(url);
        output.append('?');

        try {
            int index = 0;
            for (Map.Entry<String, String> kv : params.entrySet()) {
                if (index > 0) output.append('&');
                output.append(URLEncoder.encode(kv.getKey(), "UTF-8"));
                output.append('=');
                output.append(URLEncoder.encode(kv.getValue(), "UTF-8"));
                index++;
            }
        } catch (UnsupportedEncodingException uee) {
            // This should never happen, because "UTF-8" is always installed in Java.
            throw new AssertionError(uee);
        }
        return output.toString();
    }
}
