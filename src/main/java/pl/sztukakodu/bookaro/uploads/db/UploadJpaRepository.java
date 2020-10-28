package pl.sztukakodu.bookaro.uploads.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sztukakodu.bookaro.uploads.domain.Upload;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {
}
