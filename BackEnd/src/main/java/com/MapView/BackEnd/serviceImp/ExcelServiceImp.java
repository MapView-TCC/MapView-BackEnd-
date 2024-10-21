package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.Location;
import com.MapView.BackEnd.repository.EquipmentRepository;
import com.MapView.BackEnd.service.ExcelService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class ExcelServiceImp implements ExcelService {

    private final EquipmentRepository equipmentRepository;

    public ExcelServiceImp(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public void generateExcel(HttpServletResponse response) throws IOException {
        List<Equipment> equipments = equipmentRepository.findAllByOperativeTrue();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Equipment Info");

        // Estilo do cabeçalho
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex()); // Cor de fundo (cinza claro)
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // Define o padrão de preenchimento
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);

        // Estilizando a fonte
        HSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex()); // Cor da fonte (branca)
        headerStyle.setFont(font);

        // Criar a linha do cabeçalho
        HSSFRow rowDate = sheet.createRow(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = sdf.format(new Date());
        rowDate.createCell(0).setCellValue(String.format("Data do último download: %s", formattedDate));

        // Criar a linha do cabeçalho
        HSSFRow row = sheet.createRow(2);

        // Criar as células do cabeçalho e aplicar o estilo
        createHeaderCell(row, 0, "Codigo Equipamento", headerStyle);
        createHeaderCell(row, 1, "Tipo", headerStyle);
        createHeaderCell(row, 2, "Modelo", headerStyle);
        createHeaderCell(row, 3, "Validade", headerStyle);
        createHeaderCell(row, 4, "Usuário Principal", headerStyle);
        createHeaderCell(row, 5, "Ambiente", headerStyle);
        createHeaderCell(row, 6, "Posto", headerStyle);
        createHeaderCell(row, 7, "Observação", headerStyle);

        int dataRowIndex = 3;

        // Estilo das células de dados
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);

        for (Equipment equipment : equipments) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);

            createDataCell(dataRow, 0, equipment.getCodigo(), dataStyle);
            createDataCell(dataRow, 1, equipment.getType(), dataStyle);
            createDataCell(dataRow, 2, equipment.getModel().toString(), dataStyle);
            createDataCell(dataRow, 3, getQuarterStringFromDate(equipment.getValidity()), dataStyle);

            // Extrair informações textuais das entidades Location e MainOwner
            String IdownerName = equipment.getOwner() != null ? equipment.getOwner().getId_owner() : "N/A";
            Location location = equipment.getLocation();
            String environment = (location != null && location.getEnvironment() != null) ? location.getEnvironment().getEnvironment_name() : "N/A";
            String postName = (location != null && location.getPost() != null) ? location.getPost().getPost() : "N/A";

            createDataCell(dataRow, 4, IdownerName, dataStyle);
            createDataCell(dataRow, 5, environment, dataStyle);
            createDataCell(dataRow, 6, postName, dataStyle);
            createDataCell(dataRow, 7, equipment.getObservation(), dataStyle);

            dataRowIndex++;
        }

        // Definir largura fixa para cada coluna
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 9000);

        // definir altura da coluna
        row.setHeight((short) (18 * 20));

        // adiciona o filtro
        sheet.setAutoFilter(new CellRangeAddress(2,2,0,7));

        // Gerar o arquivo e enviá-lo na resposta
        ServletOutputStream ops = response.getOutputStream(); // Obtém o ServletOutputStream a partir da resposta HTTP.
        workbook.write(ops); // escreve o conteúdo do workbook para o output stream.
        workbook.close();
        ops.close();
    }

    private void createHeaderCell(HSSFRow row, int columnIndex, String value, HSSFCellStyle style) {
        HSSFCell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createDataCell(HSSFRow row, int columnIndex, String value, HSSFCellStyle style) {
        HSSFCell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public static String getQuarterStringFromDate(LocalDate date) {
        // Obter o ano
        int year = date.getYear();

        // Calcular o trimestre
        int month = date.getMonthValue();
        int quarter = (month - 1) / 3 + 1; // Q1 -> 1, Q2 -> 2, Q3 -> 3, Q4 -> 4

        // Formatar a string no formato "YYYY.QX"
        return year + ".Q" + quarter;
    }
}

