package br.com.vaga_ambiental.Vaga.Ambiental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private LeitorExcel leitorExcel;

    @Autowired
    private FeriadosBot feriadosBot;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            List<Excel> excels = leitorExcel.criar();
            leitorExcel.impirmir(excels);

            feriadosBot.executarFeriadosBot(excels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
