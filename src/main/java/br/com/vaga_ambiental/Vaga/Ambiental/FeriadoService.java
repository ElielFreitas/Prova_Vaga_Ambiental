package br.com.vaga_ambiental.Vaga.Ambiental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Service
public class FeriadoService {

    private static final String API_URL = "https://spprev.ambientalqvt.com.br/api/dinamico/avaliacao-vaga/registrar-feriados";
    private static final String BEARER_TOKEN = "<c35d9d94-10c1-428c-b62c-a970e40e7d6f>";

    @Autowired
    private FeriadoRepository feriadoRepository;

    public void enviarFeriadosParaAPI() {
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder resultado = new StringBuilder();

        List<Feriado> feriados = feriadoRepository.findAll();
        feriados.stream().map(feriado -> feriado.getEstado() + " " + feriado.getCidade()).distinct().forEach(combinacao -> {

            int indexEspaco = combinacao.indexOf(" ");
            if (indexEspaco == -1) {
                resultado.append("Erro: Combinacao inválida para estado e cidade: ").append(combinacao).append("\n");
                return;
            }

            String estado = combinacao.substring(0, indexEspaco);
            String cidade = combinacao.substring(indexEspaco + 1);

            List<Feriado> feriadosPorCidade = feriadoRepository.findByEstadoAndCidade(estado, cidade);

            JSONArray feriadosArray = new JSONArray();
            feriadosPorCidade.forEach(f -> {
                JSONObject feriadoObject = new JSONObject();
                feriadoObject.put("data", f.getDataFeriado().toLocalDate().toString());
                feriadoObject.put("tipo", f.getTipo());
                feriadoObject.put("feriado", f.getNomeFeriado());
                feriadosArray.put(feriadoObject);
            });

            JSONObject payload = new JSONObject();
            payload.put("estado", estado);
            payload.put("cidade", cidade);
            payload.put("feriados", feriadosArray);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + BEARER_TOKEN);

            HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

                resultado.append("Estado: ").append(estado).append(", Cidade: ").append(cidade)
                        .append(" - Response Code: ").append(response.getStatusCodeValue())
                        .append(", Response Body: ").append(response.getBody()).append("\n");

            } catch (Exception e) {
                resultado.append("Erro ao enviar para ").append(cidade).append(": ").append(e.getMessage()).append("\n");
            }
        });

        System.out.println(resultado.toString());
    }
}

