package swapper9.moviedatabase.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import swapper9.moviedatabase.ScanCinemate;
import swapper9.moviedatabase.component.MovieEditor;
import swapper9.moviedatabase.domain.Movie;
import swapper9.moviedatabase.repo.MovieRepo;

import java.io.IOException;

@Route("")
public class MovieList extends VerticalLayout {
  private final MovieRepo movieRepo;
  private final MovieEditor movieEditor;

  private Grid<Movie> movieGrid= new Grid<>(Movie.class);
  private final TextField filter = new TextField();
  private final Button addNewButton = new Button("New movie", VaadinIcon.PLUS.create());
  private final TextField passkey = new TextField();
  private final Button scanCCButton = new Button("Scan CC database (slow)", VaadinIcon.FILM.create());
  private final HorizontalLayout toolbar = new HorizontalLayout(filter, addNewButton);
  private final HorizontalLayout toolbar_scan = new HorizontalLayout(passkey, scanCCButton);

  @Autowired
  public MovieList(MovieRepo movieRepo, MovieEditor movieEditor) {
    this.movieRepo = movieRepo;
    this.movieEditor = movieEditor;

    filter.setPlaceholder("Type to filter");
    filter.setValueChangeMode(ValueChangeMode.EAGER);
    filter.addValueChangeListener(field -> fillList(field.getValue()));
    passkey.setPlaceholder("Passkey");
    movieGrid.setColumns("id", "title_russian", "title_original", "year");
    movieGrid.setHeight("500");

    add(toolbar, toolbar_scan, movieGrid, movieEditor);

    movieGrid
      .asSingleSelect()
      .addValueChangeListener(e -> movieEditor.editMovie(e.getValue()));

    addNewButton.addClickListener(e -> movieEditor.editMovie(new Movie()));
    scanCCButton.addClickListener(e -> {
      try {
        ScanCinemate.addMoviesToDb(passkey.getValue());
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    });

    movieEditor.setChangeHandler(() -> {
      movieEditor.setVisible(false);
      fillList(filter.getValue());
    });

    fillList("");
  }

  private void fillList(String name) {
    if (name.isEmpty()) {
      movieGrid.setItems(this.movieRepo.findAll());
    } else {
      movieGrid.setItems(this.movieRepo.findByTitle(name));
    }
  }
}
