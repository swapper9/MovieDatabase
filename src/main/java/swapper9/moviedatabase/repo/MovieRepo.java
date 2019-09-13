package swapper9.moviedatabase.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import swapper9.moviedatabase.domain.Movie;

import java.util.List;

public interface MovieRepo extends JpaRepository<Movie, Integer> {

  @Query("from Movie m where " +
    " concat(m.title_original, ' ', m.title_russian, ' ', m.year) like concat('%', :title, '%')")
  List<Movie> findByTitle(@Param("title")String title);
}
