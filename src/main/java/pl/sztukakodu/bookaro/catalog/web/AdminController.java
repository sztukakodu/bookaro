package pl.sztukakodu.bookaro.catalog.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sztukakodu.bookaro.catalog.application.port.CatalogInitializerUseCase;

@Slf4j
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
class AdminController {

    private final CatalogInitializerUseCase initializer;

    @PostMapping("/initialization")
    @Transactional
    public void initialize() {
        initializer.initialize();
    }

}
