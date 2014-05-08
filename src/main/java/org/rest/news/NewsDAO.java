/**
 * @author: ${user}
 * @date: ${date}
 * @file: ${file_name}
 * @package: ${package_name}
 * @project: ${project_name}
 * @package: ${package_name}
 * 
 */

package org.rest.news;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NewsDAO {

    // Field hält Referenz auf einzigartige Instanz
    // private static NewsDAO instance = null;
    private static volatile NewsDAO instance    = null;
    private static long             createtime  = 0;
    private static long             currenttime = 0;

    // Privater Konstruktur verhindert Instanziierung durch Client
    private NewsDAO() {

    }

    // Stellt Einzigartigkeit sicher. Liefert Exemplar an Client.
    // Hier: Lazy Loading mit unsynchronisierter getInstance()-Methode, aber mit
    // synchronizierter Instanziierung und doppelten Null-Check
    // Quelle: http://www.philipphauer.de/study/se/design-pattern/singleton.php
    // http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
    // http://javarevisited.blogspot.de/2011/04/synchronization-in-java-synchronized.html
    // http://javarevisited.blogspot.de/2012/07/why-enum-singleton-are-better-in-java.html
    public static NewsDAO getInstance() {

        setCurrenttime();
        // System.out.println("0C) " + getCurrenttime());
        // System.out.println("0T) " + getCreatetime());
        long currenttime = getCurrenttime();
        long createtime = getCreatetime();

        // 5 Minuten = 300 Sekunden
        if (instance == null) {
            synchronized (NewsDAO.class) {

                if (instance == null || (createtime != 0 && ((currenttime - createtime) >= 300))) {

                    // Objekt instanziieren
                    instance = new NewsDAO();
                    // timstamp setzen
                    setCreatetime();
                    // System.out.println("1T) " + getCreatetime());
                }
            }
        }
        return instance;
    }

    List<News> findNews(String findType) {

        List<News> list = new ArrayList<News>();
        Connection c = null;
        String sql = "";

        switch (findType) {
            case "findAll":
                sql = "SELECT n.newsid, n.title, n.categoryid, c.name AS category, n.description, n.text, n.createdate "
                        + "FROM enbw01.news n, enbw01.categories c WHERE c.cat_id = n.categoryid order by n.id desc;";
                break;
            case "findNewest":
                sql = "SELECT n.newsid, n.title, n.categoryid, c.name AS category, n.description, n.text, n.createdate "
                        + "FROM news n, enbw01.categories c WHERE n.id=(SELECT MAX(n2.id) FROM news n2) and c.cat_id = n.categoryid;";
                break;
            default:
                break;
        }

        try {

            c = ConnectionHelper.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                list.add(processRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            ConnectionHelper.close(c);
        }

        // System.out.println("findNews/" + findType + "/list: " + list);

        // Liste mit mehreren Objekten
        return list;
    }

    List<News> findById(int newsid) {

        List<News> list = new ArrayList<News>();
        Connection c = null;

        String sql = "SELECT n.newsid, n.title, n.categoryid, c.name AS category, n.description, n.text, n.createdate "
                + "FROM news n, enbw01.categories c WHERE n.newsid=? and c.cat_id = n.categoryid;";
        try {

            c = ConnectionHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, newsid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                list.add(processRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            ConnectionHelper.close(c);
        }

        // System.out.println("findById/" + newsid + "/list: " + list);

        // Liste mit nur einem Objekt
        return list;
    }

    public boolean deleteNews(String deleteType) {

        Connection c = null;
        String sql = "";

        switch (deleteType) {
            case "deleteAll":
                sql = "TRUNCATE news;";
                break;
            case "deleteNewest":
                sql = "DELETE FROM news ORDER BY id DESC LIMIT 1;";
                break;
            default:
                break;
        }

        try {
            c = ConnectionHelper.getConnection();
            Statement s = c.createStatement();

            int count = s.executeUpdate(sql);

            // System.out.println("deleteNews/" + deleteType + "/list: " +
            // (count == 1));

            return count == 1;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            ConnectionHelper.close(c);
        }
    }

    public boolean deleteById(int newsid) {

        Connection c = null;

        String sql = "DELETE FROM news where newsid=?;";

        try {
            c = ConnectionHelper.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, newsid);

            int count = ps.executeUpdate();

            // System.out.println("deleteById/" + newsid + "/list: " + (count == 1));

            return count == 1;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            ConnectionHelper.close(c);
        }
    }

    // Verarbeitung der einzelnen Felder
    protected static News processRow(ResultSet rs) throws SQLException {

        News news = new News();

        news.setNewsid(rs.getInt("newsid"));
        news.setTitle(rs.getString("title"));
        news.setCategory(rs.getString("category"));
        news.setDescription(rs.getString("description"));
        news.setText(rs.getString("text"));
        news.setCreatedate(rs.getDate("createdate"));

        // System.out.println("processRow - newsid: " + rs.getInt("newsid"));

        return news;
    }

    // public static boolean isEmpty(String s) {
    // return s == null || s.trim().length() <= 0;
    // }

    public static long getCurrenttime() {
        return currenttime;
    }

    public static void setCurrenttime() {
        NewsDAO.currenttime = System.currentTimeMillis() / 1000;
    }

    public static long getCreatetime() {
        return createtime;
    }

    public static void setCreatetime() {
        NewsDAO.createtime = System.currentTimeMillis() / 1000;
    }
}
