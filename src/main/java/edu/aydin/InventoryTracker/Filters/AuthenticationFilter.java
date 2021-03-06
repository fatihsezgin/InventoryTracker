package edu.aydin.InventoryTracker.Filters;

import edu.aydin.InventoryTracker.Database.MongoAdapter;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    MongoAdapter mongoAdapter = new MongoAdapter();

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String uri = request.getRequestURI();

        HttpSession session = request.getSession(false);

        String username = "";
        try {
            username = request.getSession().getAttribute("username").toString();
        }catch (NullPointerException e){

        }


        if ((username == null || !(username.length()>0)) && !(uri.endsWith("exploded/")||uri.endsWith("index.jsp")
        ||uri.endsWith("handle_login"))){
            System.out.println("filter:"+username);
            response.sendRedirect("index.jsp");
        }else {

            chain.doFilter(req, resp);
        }




    }

    public void init(FilterConfig config) throws ServletException {


    }
    private boolean checkIsAdmin(String username){

        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put("username",username);
        FindIterable<Document> result = mongoAdapter.getUserCollection().find(whereQuery);
        MongoCursor<Document> cursor = result.iterator();
        boolean isAdmin = false;
        while (cursor.hasNext()){
            Document document = cursor.next();
            isAdmin = Boolean.parseBoolean(document.get("isAdmin").toString());
        }
        return isAdmin;

    }


    private boolean checkUser(String username, String password){
        //FindIterable<Document> result = mongoAdapter.getUserCollection().find();
        MongoCursor<Document> cursor = mongoAdapter.getUserCollection().find().iterator();

        while(cursor.hasNext()){
            Document document = cursor.next();
            System.out.println(document.get("_id"));
            System.out.println(document.get("username"));
            System.out.println(document.get("password"));

            String dbusername = document.get("username").toString();
            String dbpassword = document.get("password").toString();

            if(dbusername.equals(username) && dbpassword.equals(password))
                return true;
        }
        return false;
    }

}
