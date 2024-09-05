package br.com.vaga_ambiental.Vaga.Ambiental;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Excel{
    private String estado;
    private String cidade;
}
