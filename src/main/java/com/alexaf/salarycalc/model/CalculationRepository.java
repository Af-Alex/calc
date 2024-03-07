package com.alexaf.salarycalc.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

@Slf4j
@Service
@ConditionalOnExpression("${repository.enabled:false}")
public class CalculationRepository {

    private final Workbook workbook;

    public CalculationRepository(@Value("${calc.repository.filePath}") String filePath) throws IOException {
        this.workbook = new XSSFWorkbook(new FileInputStream(filePath));
    }

    private <V> V execute(Callable<V> task) {
        try (workbook) {
            return task.call();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения Excel-файла");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в работе с Excel-файлом");
        }
    }

    public double getKataPercent() {
        return execute(() -> {
            Cell cell = getCell(workbook.getSheetAt(0), new CellAddress(1, 7));
            return cell.getNumericCellValue();
        });
    }

    public static Cell getCell(Sheet sheet, CellAddress cellAddress) {
        return sheet.getRow(cellAddress.getRow()).getCell(cellAddress.getColumn());
    }

    public static void setCell(Sheet sheet, CellAddress cellAddress, Object value) {
        getCell(sheet, cellAddress).setCellValue(value.toString());
    }

}
