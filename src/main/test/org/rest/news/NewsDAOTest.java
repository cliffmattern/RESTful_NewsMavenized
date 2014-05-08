/**
 * Gut zu wissen:
 *  ~ https://github.com/junit-team/junit/wiki
 *  ~ https://github.com/junit-team/junit/wiki/Test-execution-order
 *  ~ http://stackoverflow.com/questions/14115876/new-junit-4-11-features-including-change-test-name-and-set-execution-order-do-no
 *  ~ http://alvinalexander.com/java/edu/pj/jdbc/jdbc0002
 *  
 *  ~ rest-assured - Java DSL for easy testing of REST services:
 *  ~ https://code.google.com/p/rest-assured/wiki/GettingStarted
 *  
 */
package org.rest.news;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

//import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NewsDAOTest {

    private NewsDAO      dao;
    private NewsResource nrs;
    private News         news;

    static String        sqlCategories = "INSERT INTO `categories` (`cat_id`, `name`) VALUES ('0', 'Allgemein'), ('10', 'Sport'), ('20', 'Welt'), ('30', 'Lokales');";

    static String        sqlNews       = "INSERT INTO `news` (`newsid`, `title`, `description`, `categoryid`, `text`, `createdate`) VALUES"
                                               + "(1, 'Lorem Ipsum', 'Beschreibung 1', 10, 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibu', '2014-04-08 16:43:10'),"
                                               + "(2, 'Lorem Ipsum auf Deutsch', 'Beschreibung 2', 20, 'Auch gibt es niemanden, der den Schmerz an sich liebt, sucht oder w�nscht, nur, weil er Schmerz ist, es sei denn, es kommt zu zuf�lligen Umst�nden, in denen M�hen und Schmerz ihm gro�e Freude bereiten k�nnen. Um ein triviales Beispiel zu nehmen, wer von uns unterzieht sich je anstrengender k�rperlicher Bet�tigung, au�er um Vorteile daraus zu ziehen? Aber wer hat irgend ein Recht, einen Menschen zu tadeln, der die Entscheidung trifft, eine Freude zu genie�en, die keine unangenehmen Folgen hat, od', '2014-04-08 16:43:11'),"
                                               + "(3, 'Li Europan Lingues', 'Beschreibung 3', 30, 'Li Europan lingues es membres del sam familie. Lor separat existentie es un myth. Por scientie, musica, sport etc, litot Europa usa li sam vocabular. Li lingues differe solmen in li grammatica, li pronunciation e li plu commun vocabules. Omnicos directe al desirabilite de un nov lingua franca: On refusa continuar payar custosi traductores. At solmen va esser necessi far uniform grammatica, pronunciation e plu sommun paroles. Ma quande lingues coalesce, li grammatica del resultant lingue es plu s', '2014-04-08 16:43:12'),"
                                               + "(4, 'Hinter den Wortbergen', 'Beschreibung 4', 10, 'Weit hinten, hinter den Wortbergen, fern der L�nder Vokalien und Konsonantien leben die Blindtexte. Abgeschieden wohnen sie in Buchstabhausen an der K�ste des Semantik, eines gro�en Sprachozeans.\\r\\n\\r\\nEin kleines B�chlein namens Duden flie�t durch ihren Ort und versorgt sie mit den n�tigen Regelialien. Es ist ein paradiesmatisches Land, in dem einem gebratene Satzteile in den Mund fliegen. Nicht einmal von der allm�chtigen Interpunktion werden die Blindtexte beherrscht � ein geradezu unorthographisch', '2014-04-08 16:43:13'),"
                                               + "(5, 'Werther', 'Beschreibung 5', 20, 'Eine wunderbare Heiterkeit hat meine ganze Seele eingenommen, gleich den s��en Fr�hlingsmorgen, die ich mit ganzem Herzen genie�e. Ich bin allein und freue mich meines Lebens in dieser Gegend, die f�r solche Seelen geschaffen ist wie die meine.\n\nIch bin so gl�cklich, mein Bester, so ganz in dem Gef�hle von ruhigem Dasein versunken, da� meine Kunst darunter leidet. Ich k�nnte jetzt nicht zeichnen, nicht einen Strich, und bin nie ein gr��erer Maler gewesen als in diesen Augenblicken. Wenn das liebe', '2014-04-08 16:43:14'),"
                                               + "(6, 'Kafka', 'Beschreibung 6', 30, 'Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas B�ses getan h�tte, wurde er eines Morgens verhaftet. �Wie ein Hund! � sagte er, es war, als sollte die Scham ihn �berleben.\\r\\n\\r\\nAls Gregor Samsa eines Morgens aus unruhigen Tr�umen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Best�tigung ihrer neuen Tr�ume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen K�rper dehnte. �Es ist e', '2014-04-08 16:43:15'),"
                                               + "(7, 'Trapattoni `98', 'Beschreibung 7', 10, 'Es gibt im Moment in diese Mannschaft, oh, einige Spieler vergessen ihnen Profi was sie sind. Ich lese nicht sehr viele Zeitungen, aber ich habe geh�rt viele Situationen.\\r\\n\\r\\nErstens: wir haben nicht offensiv gespielt. Es gibt keine deutsche Mannschaft spielt offensiv und die Name offensiv wie Bayern. Letzte Spiel hatten wir in Platz drei Spitzen: Elber, Jancka und dann Zickler. Wir m�ssen nicht vergessen Zickler. Zickler ist eine Spitzen mehr, Mehmet eh mehr Basler. Ist klar diese W�rter, ist m�gli\r\n', '2014-04-08 16:43:16'),"
                                               + "(8, 'Er h�rte leise', 'Beschreibung 8', 20, 'Er h�rte leise Schritte hinter sich. Das bedeutete nichts Gutes. Wer w�rde ihm schon folgen, sp�t in der Nacht und dazu noch in dieser engen Gasse mitten im �bel beleumundeten Hafenviertel?\\r\\n\\r\\nGerade jetzt, wo er das Ding seines Lebens gedreht hatte und mit der Beute verschwinden wollte! Hatte einer seiner zahllosen Kollegen dieselbe Idee gehabt, ihn beobachtet und abgewartet, um ihn nun um die Fr�chte seiner Arbeit zu erleichtern? Oder geh�rten die Schritte hinter ihm zu einem der unz�hligen Geset', '2014-04-08 16:43:17'),"
                                               + "(9, 'Typoblindtext', 'Beschreibung 9', 30, 'Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks oder Handgloves, um Schriften zu testen.\\r\\n\\r\\nManchmal S�tze, die alle Buchstaben des Alphabets enthalten - man nennt diese S�tze �Pangrams�. Sehr bekannt ist dieser: The quick brown fox jumps over the lazy old dog. Oft werden in Typoblindtexte auch fremdsprachige Satzteile eingebaut (AVAIL� and Wefox� are testing aussi la Kerning), um die Wirkun', '2014-04-08 16:43:18'),"
                                               + "(10, 'Webstandards ', 'Beschreibung 10', 10, '�berall dieselbe alte Leier. Das Layout ist fertig, der Text l�sst auf sich warten. Damit das Layout nun nicht nackt im Raume steht und sich klein und leer vorkommt, springe ich ein: der Blindtext.\\r\\n\\r\\nGenau zu diesem Zwecke erschaffen, immer im Schatten meines gro�en Bruders �Lorem Ipsum�, freue ich mich jedes Mal, wenn Sie ein paar Zeilen lesen. Denn esse est percipi - Sein ist wahrgenommen werden. Und weil Sie nun schon die G�te haben, mich ein paar weitere S�tze lang zu begleiten, m�chte ich die', '2014-04-08 16:43:19'),"
                                               + "(11, 'Pangramm', 'Beschreibung 11', 0, 'Zwei flinke Boxer jagen die quirlige Eva und ihren Mops durch Sylt. Franz jagt im komplett verwahrlosten Taxi quer durch Bayern. Zw�lf Boxk�mpfer jagen Viktor quer �ber den gro�en Sylter Deich.', '2014-04-25 14:28:10'),"
                                               + "(12, 'a-z A-Z 123 ���', 'Beschreibung 12', 0, 'a�b cde fgh ijk lmn o�p qrs� tu� vwx yz A�BC DEF GHI JKL MNO �PQ RST U�V WXYZ ! \"� $%& /() =? * <> |; ��~ @`� ��� �� {} ', '2014-04-25 14:29:50');";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        String url = "jdbc:mariadb://localhost:3306/enbw01";
        Connection conn = DriverManager.getConnection(url, "root", "root");
        Statement st = conn.createStatement();

        st.executeUpdate("DELETE FROM categories;");
        st.executeUpdate("DELETE FROM news;");

        st.executeUpdate(sqlCategories);
        st.executeUpdate(sqlNews);

        conn.close();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        String url = "jdbc:mariadb://localhost:3306/enbw01";
        Connection conn = DriverManager.getConnection(url, "root", "root");
        Statement st = conn.createStatement();

        st.executeUpdate("DELETE FROM news;");

        st.executeUpdate(sqlNews);

        conn.close();
    }

    @Before
    public void setUp() throws Exception {
        dao = NewsDAO.getInstance();
        nrs = new NewsResource();
        news = new News();
    }

    @After
    public void tearDown() throws Exception {
        dao = null;
        nrs = null;
        news = null;
    }

    @Test
    public void a_testFindNews() {
        org.junit.Assert.assertNotNull("findAll should not be null", dao.findNews("findAll"));
        org.junit.Assert.assertNotNull("findNewest should not be null", dao.findNews("findNewest"));
        org.junit.Assert.assertNotNull("findById(2) should not be null", dao.findById(2));

        List<News> list = new ArrayList<News>(); // eine leere Liste
        org.junit.Assert.assertEquals("NewsID 200 gibt es nicht, deshalb muessen beide Listen leer sein", list,
                dao.findById(200));
    }

    @Test
    public void b_testDeleteNews() {
        // Test funktioniert nur wenn die NewsID 6 vorher vorhanden ist, dann
        // hat das Loeschen geklappt
        org.junit.Assert.assertTrue("failure - deleteById(6) should be true", dao.deleteById(6));

        // neueste News wird geloescht
        org.junit.Assert.assertTrue("failure - deleteNewest should be true", dao.deleteNews("deleteNewest"));

        // alle News werden geloescht
        org.junit.Assert.assertFalse("failure - deleteAll should be false", dao.deleteNews("deleteAll"));
    }

    @Test
    public void c_testNewsSetterGetter() throws ParseException {

        int aNumber = 0;
        String aText = "";
        SimpleDateFormat sdfToDate = new SimpleDateFormat("dd.MM.yyyy");
        Date aDate = null;

        aNumber = 768;
        news.setNewsid(aNumber);
        org.junit.Assert.assertEquals("SetterGetter Newsid: " + aNumber + " should be equal", aNumber, news.getNewsid());

        aDate = sdfToDate.parse("30.04.2013");
        news.setCreatedate(aDate);
        org.junit.Assert.assertEquals("SetterGetter Createdate: " + aDate + " should be equal", aDate, news.getCreatedate());

        aText = "Teste mich!";
        news.setCategory(aText);
        org.junit.Assert.assertEquals("SetterGetter Category: " + aText + " should be equal", aText, news.getCategory());

        news.setDescription(aText);
        org.junit.Assert.assertEquals("SetterGetter Description: " + aText + " should be equal", aText,
                news.getDescription());

        news.setText(aText);
        org.junit.Assert.assertEquals("SetterGetter Text: " + aText + " should be equal", aText, news.getText());

        news.setTitle(aText);
        org.junit.Assert.assertEquals("SetterGetter Title: " + aText + " should be equal", aText, news.getTitle());
    }

    @Test
    public void d_testDeleteNews() {

        nrs.findAll();
        nrs.findNewest();
        nrs.findById("5");

        nrs.findById("201");

        nrs.deleteAll();
        nrs.deleteNewest();
        nrs.deleteById("5");

        nrs.deleteById("201");

    }
}
