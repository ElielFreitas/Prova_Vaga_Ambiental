package br.com.vaga_ambiental.Vaga.Ambiental;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Component
public class FeriadosBot {

    public static void executarFeriadosBot(List<Excel> excels) {
        System.setProperty("webdriver.chrome.driver", "src\\driver\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        List<String> feriadosNacionais = Arrays.asList(
                "01/01/2024 - Ano Novo",
                "12/02/2024 - Carnaval",
                "13/02/2024 - Carnaval",
                "14/02/2024 - Carnaval",
                "29/03/2024 - Sexta-Feira Santa",
                "21/04/2024 - Dia de Tiradentes",
                "01/05/2024 - Dia do Trabalho",
                "30/05/2024 - Corpus Christi",
                "07/09/2024 - Independência do Brasil",
                "12/10/2024 - Nossa Senhora Aparecida",
                "15/10/2024 - Dia do Professor",
                "28/10/2024 - Dia do Servidor Público",
                "02/11/2024 - Dia de Finados",
                "15/11/2024 - Proclamação da República",
                "20/11/2024 - Consciência Negra",
                "25/12/2024 - Natal"
        );

        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/feriados_db", "postgres", "123")) {

            String insertQuery = "insert into feriados (estado, cidade, dt_feriado, feriado, tipo) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

                for (Excel excel : excels) {
                    driver.get("https://www.feriados.com.br/");

                    Thread.sleep(1000);

                    WebElement estadoSelectElement = driver.findElement(By.xpath("//*[@id=\"estado\"]"));
                    estadoSelectElement.click();

                    Select estadoSelect = new Select(estadoSelectElement);
                    estadoSelect.selectByVisibleText(excel.getEstado());

                    Thread.sleep(1000);

                    WebElement cidadeSelectElement = driver.findElement(By.xpath("//*[@id=\"cidade\"]"));
                    cidadeSelectElement.click();

                    Select cidadeSelect = new Select(cidadeSelectElement);
                    cidadeSelect.selectByVisibleText(excel.getCidade());

                    Thread.sleep(1000);

                    WebElement listaFeriados = driver.findElement(By.xpath("//ul[contains(@class, 'multi-column')]"));

                    Actions actions = new Actions(driver);
                    actions.moveToElement(listaFeriados).perform();

                    List<WebElement> feriadosElements = listaFeriados.findElements(By.tagName("li"));

                    List<String> feriadosParaIgnorar = Arrays.asList(
                            "09/07/2024 - Revolução Constitucionalista",
                            "05/08/2024 - Fundação do Estado da Paraíba"
                    );

                    for (WebElement feriadoElement : feriadosElements) {
                        String feriadoTexto = feriadoElement.getText();

                        if (!feriadosParaIgnorar.contains(feriadoTexto)) {
                            String[] partes = feriadoTexto.split(" - ", 2);
                            if (partes.length == 2) {
                                String dataFeriado = partes[0];
                                String nomeFeriado = partes[1];

                                java.sql.Date sqlDate = null;
                                try {
                                    java.util.Date parsedDate = new SimpleDateFormat("dd/MM/yyyy").parse(dataFeriado);
                                    sqlDate = new java.sql.Date(parsedDate.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String tipoFeriado = feriadosNacionais.contains(feriadoTexto) ? "Nacional" : "Municipal";

                                pstmt.setString(1, excel.getEstado());
                                pstmt.setString(2, excel.getCidade());
                                pstmt.setDate(3, sqlDate);
                                pstmt.setString(4, nomeFeriado);
                                pstmt.setString(5, tipoFeriado);
                                pstmt.executeUpdate();
                            }
                        }
                    }

                    System.out.println("Feriados de " + excel.getCidade() + " salvos com sucesso no banco de dados!");
                }
            }

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
