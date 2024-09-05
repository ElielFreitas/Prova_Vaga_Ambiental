package br.com.vaga_ambiental.Vaga.Ambiental;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class LeitorExcel {

    public List<Excel> criar() throws IOException {

        List<Excel> excels = new ArrayList<>();

        FileInputStream file = new FileInputStream("src\\archive\\ProjetovagaAmbiental.xlsx");

        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet =  workbook.getSheetAt(0);

        List<Row> rows = (List<Row>) toList(sheet.iterator());

        rows.remove(0);

        rows.forEach(row -> {

            List<Cell> cells = (List<Cell>) toList(row.cellIterator());

            Excel excel = Excel.builder()
                    .estado(cells.get(0).getStringCellValue())
                    .cidade(cells.get(1).getStringCellValue())
                    .build();

            excels.add(excel);
        });

        return excels;
    }

    public List<?> toList(Iterator<?> iterator){
        return IteratorUtils.toList(iterator);
    }

    public void impirmir(List<Excel> excels){

        excels.forEach(System.out::println);
    }
}