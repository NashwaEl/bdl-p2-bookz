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

    /**
     * Print HTML for search bae
     *
     * @param html - print writer
     */
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
        html.println("<li id=\"logo\"><a href='#'>Bookz</a></li>");
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

    /**
     * Prints HTML for modal to flag books
     * @param output - print writer
     * @param book - book we are flagging
     */
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

    /**
     * Prints results from query
     * @param books - resulting books
     * @param resp
     * @throws IOException
     */
    void showSearchResultsPage(HashSet<GutenbergBook> books, HttpServletResponse resp) throws IOException {
        try (PrintWriter html = resp.getWriter()) {
            printPageStart(html, "Bookz");
            printSearchBar(html);
            List<GutenbergBook> resultBooks = new ArrayList<>(books);
            printBooks(html, resultBooks.subList(0,Math.min(books.size(), 20)));
            System.out.print("count:  " + books.size());
            int numPages = books.size()/20;
            if (numPages> 1){
            	printPagesLinks(html,numPages,1);
            }
            
            printPageEnd(html);
        }
    }


    /**
     * Print the resulting collection of books
     *
     * @param theBooks - general set of books
     * @param resp
     * @throws IOException
     */
    public void showBookCollection(List<GutenbergBook> theBooks, HttpServletResponse resp) throws IOException {
        try (PrintWriter html = resp.getWriter()) {
            printPageStart(html, "Bookz");
            printSearchBar(html);
            printBooks(html, theBooks.subList(0,Math.min(theBooks.size(), 20)));
            
            int numPages = theBooks.size()/20;
            if (numPages> 1){
            	printPagesLinks(html,numPages,1);
            }

            printPageEnd(html);
        }
    }

    /**
     *
     * @param books
     * @param pageNum
     * @param resp
     * @throws IOException
     */
    public void showBooksPage(List<GutenbergBook> books, int pageNum, HttpServletResponse resp) throws IOException {
    	try (PrintWriter html = resp.getWriter()) {
            printPageStart(html, "Bookz");
            printSearchBar(html);
            if(pageNum == 1){
            	printBooks(html, books.subList(1,20));
            }else {
            	int start = pageNum*20 +1;
            	int end = Math.min(books.size(), start + 20);
            	printBooks(html, books.subList(start, end));
            }
            
            int numPages = books.size()/20;
            if (numPages> 1){
            	printPagesLinks(html,numPages, pageNum);
            }
            
            printPageEnd(html);
        }
    }


    /**
     * Prints page numbers on bottom of screen
     *
     * @param html
     * @param numPages
     * @param page
     */
    public void printPagesLinks(PrintWriter html, int numPages, int page){
    	
    	if (numPages > 5){
    		html.append("<div class=\"page\">");
    		int start = Math.max(1, page - 1);
    		int page2 = page + 1;
    		int page3 = page + 2;
    		
    		if (page >= 3){
    			html.println("<a href='/page/" + 1 + "'>" + 1 + "</a> ");
    			if((start -1) > 1){
    				html.println("<a href='/page/" + page + "'>...</a> ");
    			}
    			
    		}
    		
    		if (page != 1 && page != numPages){
    			html.println("<a href='/page/" + start + "'>" + start + "</a> ");
    		}
    		
    		if (page == numPages){
    			page2 = start - 1;
        		page3 = start - 2;
        		html.println("<a href='/page/" + page3 + "'>" + page3 + "</a> ");
        		html.println("<a href='/page/" + page2 + "'>" + page2+ "</a> ");
    			html.println("<a href='/page/" + start + "'>" + start + "</a> ");
        		html.println("<a href='/page/" + numPages + "'>" + numPages + "</a> ");
    			
    		}else{
    			
    			html.println("<a href='/page/" + page + "'>" + page + "</a> ");
        		html.println("<a href='/page/" + page2 + "'>" + page2+ "</a> ");
        		html.println("<a href='/page/" + page3 + "'>" + page3 + "</a> ");
        		if((numPages - page3) > 1){
    				html.println("<a href='/page/" + page + "'>...</a> ");
    				html.println("<a href='/page/" + numPages + "'>" + numPages + "</a> ");
    			}
        		
    		}
    		
    		html.append("<br><br><br>");
            html.append("</div>");
            
    	}else {
    		html.append("<div class=\"page\">");
            for (int i =1; i<=numPages; i++ ){
            	html.println("<a href='/page/" + i + "'>" + i + "</a> ");
            }
            html.append("<br><br><br>");
            html.append("</div>");
    	}

    }

    /**
     * Div containing a message to let the user know that their error has been submitted
     * @param html - print writer
     */
    public void printSumbitted(PrintWriter html) {
        html.println("<div id=\"submitted\"><p > Your issue has been added to our error log. We will try to fix it ASAP!  </p></div>");
    }

    /**
     * Div containing a message to let the user know that this particular book has already been flagged by a user
     * @param html
     */
    public void printAlreadyFlagged(PrintWriter html) {
        html.println("<div id=\"submitted\"><p > This book has already been flagged by a user. We are currently trying to fix it ASAP!  </p></div>");
    }

    /**
     * Show the front page with random books when the user first loads Bookz
     * @param model
     * @param resp
     * @param flagged
     * @param alreadyFlagged
     * @throws IOException
     */
    void showFrontPage(Model model, HttpServletResponse resp, boolean flagged, boolean alreadyFlagged) throws IOException {
        try (PrintWriter html = resp.getWriter()) {
            printPageStart(html, "Bookz");
            printSearchBar(html);
            if (flagged && alreadyFlagged){
        		printAlreadyFlagged(html);
            }else if (flagged){
        		printSumbitted(html);
            }
            List<GutenbergBook> randomBooks = model.getRandomBooks(15);
            printBooks(html, randomBooks);
            printAlphabeticalLinks(html);

            printPageEnd(html);
        }
    }

    /**
     * Print the individual books
     * @param html - print writer
     * @param randomBooks - random books to display
     */
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
        html.append("</div>");

    }


    /**
     * Print alphabetical page navigation on bottom of the screen
     * @param html
     */
    public void printAlphabeticalLinks(PrintWriter html){
    	 html.append("<div class=\"page\">");
         for (char letter = 'A'; letter <= 'Z'; letter++) {
             html.println("<a href='/title/" + letter + "'>" + letter + "</a> ");
         }
         html.append("<br><br><br>");
         html.append("</div>");
    }


    /**
     * Returns a random hex code for book color
     * @return
     */
    public String getRandomColor() {
        String[] colors = {"#9FC6C1", "#6F7869", "#8A8184", "#E3A7A3", "#F1D0CA", "#EFF0DA"};
        Random rand = new Random();
        int n = rand.nextInt(colors.length);
        String color = colors[n];
        return color;
    }

    /**
     * Returns a random hex code for spine color
     * @return
     */
    public String getSpineColors() {
        String[] colors = {"#635647", "#4D3925", "#A1703B", "#73502E", "#594F44", "#544A41"};
        Random rand = new Random();
        int n = rand.nextInt(colors.length);
        String color = colors[n];
        return color;
    }

    /**
     * Returns a random font to be displayed on book cover
     * @return
     */
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
}
