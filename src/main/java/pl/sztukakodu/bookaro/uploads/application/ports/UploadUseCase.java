package pl.sztukakodu.bookaro.uploads.application.ports;

import lombok.AllArgsConstructor;
import lombok.Value;
import pl.sztukakodu.bookaro.uploads.domain.Upload;

import java.util.Optional;

public interface UploadUseCase {
    Upload save(SaveUploadCommand command);

    Optional<Upload> getById(String id);

    @Value
    @AllArgsConstructor
    class SaveUploadCommand {
        String filename;
        byte[] file;
        String contentType;
    }
}
