package swapper9.moviedatabase.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="movies")
public class Movie {

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private int id;

  private String url;

  private String title_russian;

  private String title_original;

  private String year;

  private String poster_small;

  private String poster_big;

  public Movie() {
  }

  public Movie(String url, String title_russian, String title_original, String year, String poster_small, String poster_big) {
    this.url = url;
    this.title_russian = title_russian;
    this.title_original = title_original;
    this.year = year;
    this.poster_small = poster_small;
    this.poster_big = poster_big;
  }
}

