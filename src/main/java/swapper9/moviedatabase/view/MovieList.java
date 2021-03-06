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
import swapper9.moviedatabase.repository.MovieRepository;

import java.io.IOException;

@Route("")
public class MovieList extends VerticalLayout {
  private final MovieRepository movieRepository;
  private final MovieEditor movieEditor;

  @Autowired
  private ScanCinemate scanCinemate;

  private Grid<Movie> movieGrid= new Grid<>(Movie.class);
  private final TextField filter = new TextField();
  private final Button addNewButton = new Button("New movie", VaadinIcon.PLUS.create());
  private final TextField passkey = new TextField();
  private final Button scanCCButton = new Button("Scan CC database (slow)", VaadinIcon.FILM.create());
  private final HorizontalLayout toolbar = new HorizontalLayout(filter, addNewButton);
  private final HorizontalLayout toolbar_scan = new HorizontalLayout(passkey, scanCCButton);

  @Autowired
  public MovieList(MovieRepository movieRepository, MovieEditor movieEditor) {
    this.movieRepository = movieRepository;
    this.movieEditor = movieEditor;

    filter.setPlaceholder("Type to filter");
    filter.setValueChangeMode(ValueChangeMode.EAGER);
    filter.addValueChangeListener(field -> fillList(field.getValue()));
    passkey.setPlaceholder("Passkey");
    movieGrid.setColumns("id", "title_russian", "title_original", "year");
    movieGrid.setSortableColumns("title_russian", "title_original", "year");
    movieGrid.setColumnReorderingAllowed(true);
    movieGrid.setHeight("500");
    movieGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

    add(toolbar, toolbar_scan, movieGrid, movieEditor);

    movieGrid
      .asSingleSelect()
      .addValueChangeListener(e -> movieEditor.editMovie(e.getValue()));

    addNewButton.addClickListener(e -> movieEditor.editMovie(new Movie()));
    scanCCButton.addClickListener(e -> {
      try {
        scanCinemate.addMoviesToDb(passkey.getValue());
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

  private String findPosterUrl(Integer id) {
    return this.movieRepository.findById(id).map(Movie::getPoster_big).orElse(null);
  }

  private void fillList(String name) {
    if (name.isEmpty()) {
      movieGrid.setItems(this.movieRepository.findAll());
    } else {
      movieGrid.setItems(this.movieRepository.findByTitle(name));
    }
  }
}
