package edu.mtholyoke.cs341bd.bookz;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author jfoley
 */
public class BookzServer extends AbstractHandler {
	private Server jettyServer;
	private HTMLView view;
	private Model model;

	public BookzServer(String baseURL, int port) throws IOException {
		view = new HTMLView(baseURL);
		jettyServer = new Server(port);
		model = new Model();

		// We create a ContextHandler, since it will catch requests for us under
		// a specific path.
		// This is so that we can delegate to Jetty's default ResourceHandler to
		// serve static files, e.g. CSS & images.
		ContextHandler staticCtx = new ContextHandler();
		staticCtx.setContextPath("/static");
		ResourceHandler resources = new ResourceHandler();
		resources.setBaseResource(Resource.newResource("static/"));
		staticCtx.setHandler(resources);

		ContextHandler jsCtx = new ContextHandler();
		jsCtx.setContextPath("/scripts");
		ResourceHandler jsResources = new ResourceHandler();
		jsResources.setBaseResource(Resource.newResource("scripts/"));
		jsCtx.setHandler(jsResources);

		// This context handler just points to the "handle" method of this
		// class.
		ContextHandler defaultCtx = new ContextHandler();
		defaultCtx.setContextPath("/");
		defaultCtx.setHandler(this);

		// Tell Jetty to use these handlers in the following order:
		ContextHandlerCollection collection = new ContextHandlerCollection();
		collection.addHandler(jsCtx);
		collection.addHandler(staticCtx);
		collection.addHandler(defaultCtx);
		jettyServer.setHandler(collection);
	}

	/**
	 * Once everything is set up in the constructor, actually start the server
	 * here:
	 * 
	 * @throws Exception
	 *             if something goes wrong.
	 */
	public void run() throws Exception {
		jettyServer.start();
		jettyServer.join(); // wait for it to finish here! We're using threads behind the scenes; so this keeps the main thread around until something can happen!
	}

	/**
	 * The main callback from Jetty.
	 * 
	 * @param resource
	 *            what is the user asking for from the server?
	 * @param jettyReq
	 *            the same object as the next argument, req, just cast to a
	 *            jetty-specific class (we don't need it).
	 * @param req
	 *            http request object -- has information from the user.
	 * @param resp
	 *            http response object -- where we respond to the user.
	 * @throws IOException
	 *             -- If the user hangs up on us while we're writing back or
	 *             gave us a half-request.
	 * @throws ServletException
	 *             -- If we ask for something that's not there, this might
	 *             happen.
	 */
	@Override
	public void handle(String resource, Request jettyReq, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		System.out.println(jettyReq);

		String method = req.getMethod();
		String path = req.getPathInfo();

		if ("GET".equals(method)) {
			String titleCmd = Util.getAfterIfStartsWith("/title/", path);
			if(titleCmd != null) {
				char firstChar = titleCmd.charAt(0);
				view.showBookCollection(this.model.getBooksStartingWith(firstChar), resp, firstChar);
			}

			// Check for startsWith and substring
			String bookId = Util.getAfterIfStartsWith("/book/", path);
			if(bookId != null) {
				view.showBookPage(this.model.getBook(bookId), resp);
			}

			String search = Util.getAfterIfStartsWith("/search", path);
			if (search != null) {
				Map<String, String[]> parameterMap = req.getParameterMap();
				String query = Util.join(parameterMap.get("query"));
				System.out.println("--------Query------ " + query);
				HashSet<GutenbergBook> results = model.getSearchResults(query);
				view.showSearchResultsPage(results, resp);
			}

			// Front page!
			if ("/front".equals(path) || "/".equals(path)) {
				view.showFrontPage(this.model, resp);
				return;
			}
		} else if ("POST".equals(method) && "/submit".equals(path)) {
			Map<String, String[]> parameterMap = req.getParameterMap();
			String name = Util.join(parameterMap.get("name"));
			String error = Util.join(parameterMap.get("error"));
			String book = Util.join(parameterMap.get("book"));
			Flag f = new Flag(name, error, book, System.currentTimeMillis());
			f.writeErrorsToFile("errors.txt");
			List<GutenbergBook> randomBooks = model.getRandomBooks(15);
			try (PrintWriter html = resp.getWriter()) {
				html.flush();
				view.printPageStart(html, "Bookz");
				view.printSearchBar(html);
				view.printSumbitted(html);
				view.showFlag(html);
				view.printBooks(html, randomBooks);
				view.printPageEnd(html);
			}
		}
	}
}
