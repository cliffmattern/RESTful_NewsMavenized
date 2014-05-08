/*
 * http://localhost:8080/RESTFul_News/rest/news/all
 * http://localhost:8080/RESTFul_News/rest/news/3
 */

package org.rest.news;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/news")
public class NewsResource {

    // alle News ausliefern
    @GET
    @Path("/all")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<News> findAll() {
        NewsDAO dao = NewsDAO.getInstance();
        return dao.findNews("findAll");
    }

    // neueste News ausliefern
    @GET
    @Path("/new")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<News> findNewest() {
        NewsDAO dao = NewsDAO.getInstance();
        return dao.findNews("findNewest");
    }

    // gewuenschte News ausliefern
    @GET
    @Path("{NewsID}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<News> findById(@PathParam("NewsID") String NewsID) {
        NewsDAO dao = NewsDAO.getInstance();
        return dao.findById(Integer.parseInt(NewsID));
    }

    // alle News loeschen
    @DELETE
    @Path("/all")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void deleteAll() {
        NewsDAO dao = NewsDAO.getInstance();
        dao.deleteNews("deleteAll");
    }

    // neueste News loeschen
    @DELETE
    @Path("/new")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void deleteNewest() {
        NewsDAO dao = NewsDAO.getInstance();
        dao.deleteNews("deleteNewest");
    }

    // gewuenschte News loeschen
    @DELETE
    @Path("{NewsID}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void deleteById(@PathParam("NewsID") String NewsID) {
        NewsDAO dao = NewsDAO.getInstance();
        dao.deleteById(Integer.parseInt(NewsID));
    }
}
