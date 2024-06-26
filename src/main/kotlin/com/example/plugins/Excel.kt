package com.example.pluginsimport

import com.example.db.UserRoleProject.UserRoleProjectModel
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


data class CalendarPlan(val nameTask: String, val execution: Int, val start: Int = 0)

class Excel {
    fun readExcel(paths: String) {
        val file = File(paths)
        val workbook = XSSFWorkbook(file)
        val sheet = workbook.getSheetAt(0)

        for (row in sheet) {
            for (cell in row) {
                val cellValue = when (cell.cellType) {
                    CellType.STRING -> cell.stringCellValue
                    CellType.NUMERIC -> cell.numericCellValue.toString()
                    CellType.BOOLEAN -> cell.booleanCellValue.toString()
                    else -> ""
                }
                println("Cell value: $cellValue")
            }
        }
        workbook.close()
    }

    fun writeExcel(projectId: Int, outputPath: String) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")

        val cellStyle = workbook.createCellStyle()
        val color = IndexedColors.GREEN.getIndex()
        cellStyle.setFillForegroundColor(color)
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

        // Создание заголовка
        val headerRow = sheet.createRow(0)

        for (header in 1..120) {
            val cell = headerRow.createCell(header)
            cell.setCellValue(header.toString() + " день")
        }

        // Метод, который выводить словарь, где ключ - название задача, а
        // значение кол-во дней выполнения задания
        val calendarPlan = UserRoleProjectModel.scheduling(projectId)

        val data = calendarPlan

//        for ((rowIndex, rowData) in data.withIndex()) {
//            val row = sheet.createRow(rowIndex + 1)
//
//            val cell = row.createCell(0)
//            cell.setCellValue(rowData.nameTask)
//            for (i in 1..rowData.execution) {
//                val cellExecution = row.createCell(i + rowData.start)
//                cellExecution.setCellValue(" ")
//                cellExecution.cellStyle = cellStyle
//            }
//        }


        val headerStyle = workbook.createCellStyle()
        headerStyle.alignment = HorizontalAlignment.CENTER
        val headerFont = workbook.createFont()
        headerFont.bold = true
        headerStyle.setFont(headerFont)
        val cell = headerRow.createCell(0)
        cell.cellStyle = headerStyle
        cell.setCellValue("Task/Day") // Установка значения ячейки
        sheet.setColumnWidth(0, 12 * 256) // Установка ширины столбцов
        headerRow.getCell(0).cellStyle = headerStyle


        sheet.setColumnWidth(0, 12 * 256) // Установка ширины столбца
    }

}