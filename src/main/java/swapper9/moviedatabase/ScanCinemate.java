package swapper9.moviedatabase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import swapper9.moviedatabase.domain.Movie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

public class ScanCinemate {

  /**
   * Первый параметр - PASSKEY юзера, который можно взять на http://cinemate.cc/preferences/#api
   * Скачивается база, доступная на http://cinemate.cc/profile/{nickname}/view_history/
   * @param passkey
   * @throws IOException
   */
  public static void addMoviesToDb(String passkey) throws IOException {

    SessionFactory factory = new Configuration()
      .configure("hibernate.cfg.xml")
      .addAnnotatedClass(Movie.class)
      .buildSessionFactory();

    int page = 0;
    while (true) {
      JsonObject jsonObject = new JsonParser()
        .parse(getHtmlPageFromUrl(new URL("http://api.cinemate.cc/account.votes?passkey=" + passkey + "&format=json&page=" + page)))
        .getAsJsonObject();
      JsonArray jsonVoteArray = (JsonArray)jsonObject.get("vote");
      if (jsonVoteArray.size() < 1) break;

      for (int i = 0; i < jsonVoteArray.size(); i++) {
        JsonObject jsonMovieObject = (JsonObject)jsonVoteArray.get(i);
        String movie_url = jsonMovieObject.get("movie").getAsJsonObject().get("url").getAsString();
        String title_russian = jsonMovieObject.get("movie").getAsJsonObject().get("title_russian").getAsString();
        String title_original = Optional.ofNullable(movie_url)
          .map(u -> getMetaTag(getDocument(u)))
          .map(d -> d.split(" / "))
          .map(a -> a[a.length - 1].substring(0, a[a.length - 1].length() - 6).trim())
          .orElse(null);
        String year = jsonMovieObject.get("movie").getAsJsonObject().get("year").getAsString();
        String poster_url_small = "https:".concat(jsonMovieObject.get("movie").getAsJsonObject().get("poster").getAsJsonObject().get("small").getAsJsonObject().get("url").getAsString());
        String poster_url_big = "https:".concat(jsonMovieObject.get("movie").getAsJsonObject().get("poster").getAsJsonObject().get("big").getAsJsonObject().get("url").getAsString());
        //byte[] small_poster = savePoster(poster_url_small);
        //byte[] big_poster = savePoster(poster_url_big);

        try(Session session = factory.getCurrentSession()) {
          Movie movie = new Movie(movie_url, title_russian, title_original, year, poster_url_small, poster_url_big);
          session.beginTransaction();
          session.save(movie);
          session.getTransaction().commit();
        }
      }
      page++;
    }
    factory.close();
  }

  private static byte[] savePoster(String image_url) throws IOException {
    URL url = new URL(image_url);
    HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
    httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
    InputStream in = new BufferedInputStream(httpcon.getInputStream());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buf = new byte[2048];
    int n = 0;
    while (-1!=(n=in.read(buf))) {
      out.write(buf, 0, n);
    }
    out.close();
    in.close();
    return out.toByteArray();
  }


  private static String getMetaTag(Document doc) {
    Elements elements = doc.select("meta[name=description]");
    for (Element element : elements) {
      final String s = element.attr("content");
      if (s != null) return s;
    }
    elements = doc.select("meta[property=description]");
    for (Element element : elements) {
      final String s = element.attr("content");
      if (s != null) return s;
    }
    return null;
  }

  private static Document getDocument(String url) {
    Document doc = null;
    try {
      doc = Jsoup.connect(url).get();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return doc;
  }

  private static String getHtmlPageFromUrl(URL url) throws IOException {
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    conn.setRequestMethod("GET");
    conn.connect();
    int respCode = conn.getResponseCode();
    if (respCode != 200) {
      throw new RuntimeException("HttpResponseCode: " + respCode);
    }
    String inline = "";
    Scanner scanner = new Scanner(url.openStream());
    while (scanner.hasNext()) {
      inline += scanner.nextLine();
    }
    scanner.close();
    return inline;
  }
}
