package com.MapView.BackEnd.serviceImp;

import com.MapView.BackEnd.entities.Equipment;
import com.MapView.BackEnd.entities.EquipmentResponsible;
import com.MapView.BackEnd.entities.Location;
import com.MapView.BackEnd.repository.EquipmentRepository;
import com.MapView.BackEnd.repository.EquipmentResponsibleRepository;
import com.MapView.BackEnd.service.ReportService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportServiceImp implements ReportService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentResponsibleRepository equipmentResponsibleRepository;

    public ReportServiceImp(EquipmentRepository equipmentRepository, EquipmentResponsibleRepository equipmentResponsibleRepository) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentResponsibleRepository = equipmentResponsibleRepository;
    }

    @Override
    public void generateExcel(HttpServletResponse response) throws IOException {
        List<Equipment> equipments = equipmentRepository.findAllByOperativeTrue();


        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = LocalDate.now().format(fmt);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Equipment Info");

        // Estilo do cabeçalho mesclado (Data do último download)
        HSSFCellStyle mergedHeaderStyle = workbook.createCellStyle();
        mergedHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        mergedHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Fonte preta para o cabeçalho mesclado
        HSSFFont mergedHeaderFont = workbook.createFont();
        mergedHeaderFont.setFontName("Arial");
        mergedHeaderFont.setBold(true);
        mergedHeaderFont.setColor(IndexedColors.BLACK.getIndex()); // Cor da fonte preta
        mergedHeaderStyle.setFont(mergedHeaderFont);

        // Criar a linha mesclada (0 e 1) para o cabeçalho
        HSSFRow mergedRow = sheet.createRow(0);
        mergedRow.setHeight((short) (30 * 20)); // Aumenta a altura das linhas mescladas
        HSSFCell mergedCell = mergedRow.createCell(0);
        mergedCell.setCellValue(String.format("Data do último download: %s", formattedDate));
        mergedCell.setCellStyle(mergedHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 7)); // Mescla as células das linhas 0 e 1

        // Estilo do cabeçalho da tabela (linha 2)
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex()); // Fundo preto para os títulos da tabela
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);

        // Fonte branca para os títulos da tabela
        HSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex()); // Cor da fonte branca
        headerStyle.setFont(headerFont);

        // Criar a linha do cabeçalho (linha 2)
        HSSFRow row = sheet.createRow(2);
        row.setHeight((short) 630); // Define a altura da linha do cabeçalho

        // Criar as células do cabeçalho e aplicar o estilo
        createHeaderCell(row, 0, "Codigo Equipamento", headerStyle);
        createHeaderCell(row, 1, "Tipo", headerStyle);
        createHeaderCell(row, 2, "Modelo", headerStyle);
        createHeaderCell(row, 3, "Validade", headerStyle);
        createHeaderCell(row, 4, "Usuário Principal", headerStyle);
        createHeaderCell(row, 5, "Ambiente", headerStyle);
        createHeaderCell(row, 6, "Posto", headerStyle);
        createHeaderCell(row, 7, "Observação", headerStyle);
        createHeaderCell(row, 8, "Classes", headerStyle);

        int dataRowIndex = 3;

        // Estilo das células de dados
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);

        for (Equipment equipment : equipments) {
            HSSFRow dataRow = sheet.createRow(dataRowIndex);
            List<EquipmentResponsible> responsibles = equipmentResponsibleRepository.findByOperativeTrueByIdEquipment(equipment.getId_equipment());

            createDataCell(dataRow, 0, equipment.getId_equipment(), dataStyle);
            createDataCell(dataRow, 1, equipment.getType(), dataStyle);
            createDataCell(dataRow, 2, equipment.getModel().toString(), dataStyle);
            createDataCell(dataRow, 3, equipment.getValidity(), dataStyle);

            // Extrair informações textuais das entidades Location e MainOwner
            String IdownerName = equipment.getOwner() != null ? equipment.getOwner().getId_owner() : "N/A";
            Location location = equipment.getLocation();
            String environment = (location != null && location.getEnvironment() != null) ? location.getEnvironment().getEnvironment_name() : "N/A";
            String postName = (location != null && location.getPost() != null) ? location.getPost().getPost() : "N/A";

            createDataCell(dataRow, 4, IdownerName, dataStyle);
            createDataCell(dataRow, 5, environment, dataStyle);
            createDataCell(dataRow, 6, postName, dataStyle);
            createDataCell(dataRow, 7, equipment.getObservation(), dataStyle);

            // Iterar sobre a lista de responsibles e concatenar os nomes ou classes em uma String
            StringBuilder responsibleNames = new StringBuilder();
            for (EquipmentResponsible responsible : responsibles) {
                if (responsibleNames.length() > 0) {
                    responsibleNames.append(", "); // Adiciona uma vírgula entre os nomes
                }
                responsibleNames.append(responsible.getId_responsible().getResponsible_name()); // Ou outro campo que você queira mostrar
            }

            // Adicionar a String com os responsáveis na célula correspondente
            createDataCell(dataRow, 8, responsibleNames.toString(), dataStyle);

            dataRowIndex++;
        }

        // Definir largura fixa para cada coluna
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 9000);

        // Adicionar o filtro
        sheet.setAutoFilter(new CellRangeAddress(2, 2, 0, 7));

        // Gerar o arquivo e enviá-lo na resposta
        ServletOutputStream ops = response.getOutputStream();
        workbook.write(ops);
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
}