package swapper9.moviedatabase.component;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import swapper9.moviedatabase.domain.Movie;
import swapper9.moviedatabase.repo.MovieRepo;


@SpringComponent
@UIScope
public class MovieEditor extends VerticalLayout implements KeyNotifier {
  private final MovieRepo movieRepo;
  private Movie movie;

  private TextField movie_url = new TextField("", "Movie url");
  private TextField title_russian = new TextField("", "Russian title");
  private TextField title_original = new TextField("", "Original title");
  private TextField year = new TextField("", "Year");
  private TextField poster_url_small = new TextField("", "Small poster url");
  private TextField poster_url_big = new TextField("", "Big poster url");
  private Button save = new Button("Save");
  private Button cancel = new Button("Cancel");
  private Button delete = new Button("Delete");
  private HorizontalLayout buttons = new HorizontalLayout(save, cancel, delete);

  private Binder<Movie> binder = new Binder<>(Movie.class);

  @Setter
  private ChangeHandler changeHandler;

  public interface ChangeHandler {
    void onChange();
  }

  @Autowired
  public MovieEditor(MovieRepo movieRepo) {
    this.movieRepo = movieRepo;
    add(movie_url, title_russian, title_original, year, poster_url_small, poster_url_big, buttons);
    binder.bindInstanceFields(this);
    setSpacing(true);
    save.getElement().getThemeList().add("primary");
    delete.getElement().getThemeList().add("error");
    addKeyPressListener(Key.ENTER, e -> save());
    save.addClickListener(e -> save());
    delete.addClickListener(e -> delete());
    cancel.addClickListener(e -> editMovie(movie));
    setVisible(false);
  }

  private void save() {
    movieRepo.save(movie);
    changeHandler.onChange();
  }

  private void delete() {
    movieRepo.delete(movie);
    changeHandler.onChange();
  }

  public void editMovie(Movie mov) {
    if (mov == null) {
      setVisible(false);
      return;
    }

    if (mov.getId() != 0) {
      this.movie = movieRepo.findById(mov.getId()).orElse(mov);
    } else {
      this.movie = mov;
    }

    binder.setBean(this.movie);
    setVisible(true);
    title_russian.focus();
  }
}
