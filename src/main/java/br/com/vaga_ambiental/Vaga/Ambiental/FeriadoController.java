package br.com.vaga_ambiental.Vaga.Ambiental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeriadoController {

    @Autowired
    private FeriadoService feriadoService;

    @GetMapping("/enviar-feriados")
    public ResponseEntity<String> enviarFeriados() {
        try {
            feriadoService.enviarFeriadosParaAPI();
            return ResponseEntity.status(HttpStatus.OK).body("Feriados enviados com sucesso para a API.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar feriados para a API: " + e.getMessage());
        }
    }
}
