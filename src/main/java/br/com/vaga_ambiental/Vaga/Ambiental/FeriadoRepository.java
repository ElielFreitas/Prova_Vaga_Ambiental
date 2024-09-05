package br.com.vaga_ambiental.Vaga.Ambiental;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeriadoRepository extends JpaRepository<Feriado, Long> {
    List<Feriado> findByEstadoAndCidade(String estado, String cidade);
}
