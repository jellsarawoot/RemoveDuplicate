package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.Reader;

import java.io.*;
import java.net.URL;

public class Main {
    static final  String srcTextFile = "D:/SCB Project/Fix Duplicate/EDW_BCM_PAYROLL_PA_FWD_M20220731.txt";
    static final  String targetTextFile = "D:/SCB Project/Fix Duplicate/output.txt";
    static final  String srcCsvFile = "D:/SCB Project/Fix Duplicate/S1CorpID.csv";
    static HashMap<String, CsvKeyCompare> profileMap = new HashMap<>();
    static LinkedHashMap<MyKey, MyRecord> mapValue = new LinkedHashMap<>();

    public static void main(String[] args) {
        ClassLoader classloader = org.apache.poi.poifs.filesystem.POIFSFileSystem.class.getClassLoader();
        URL resPath = classloader.getResource("org/apache/poi/poifs/filesystem/POIFSFileSystem.class");
        String path = resPath.getPath();
        RemoveDuplicate();

        String resultText = Maptotext(mapValue);  //change Map to String
        writeOutput(resultText);                  //write to new file
        System.out.println("Hello world");
    }

    static void RemoveDuplicate(){

        String dataText = readInput();
        int count = 1;
        Scanner scanner = new Scanner(dataText);
        while (scanner.hasNextLine()) {
            MyRecord myRecord = new MyRecord();
            MyKey myKey = new MyKey(), equiKey = new MyKey();
            String line = scanner.nextLine();

            String[] text = line.split("\\|", -1);
            myRecord.runningNo = text[0] != null ? text[0] : "";
            myRecord.asOfYearMonth = text[1] != null ? text[1] : "";
            myRecord.seqNo = text[2] != null ? text[2] : "";
            myRecord.companyName = text[3] != null ? text[3] : "";
            myRecord.corpIdCompCode = text[4] != null ? text[4] : "";
            myRecord.valueDate = text[5] != null ? text[5] : "";
            myRecord.accountNo = text[6] != null ? text[6] : "";
            myRecord.accountName = text[7] != null ? text[7] : "";
            myRecord.employeeCardIdOrPassportNo = text[8] != null ? text[8] : "";
            myRecord.birthDate = text[9] != null ? text[9] : "";
            myRecord.sex = text[10] != null ? text[10] : "";
            myRecord.insurerancePlan = text[11] != null ? text[11] : "";
            myKey.accountNo = text[6] != null ? text[6] : "";
            myKey.companyName = text[3] != null ? text[3] : "";

            if (count < 2) { //first line
                mapValue.put(myKey, myRecord); // insert to mapvalue
                }
             else {
                if (mapValue.containsKey(myKey) && profileMap.containsKey(myRecord.companyName)) {
                    MyRecord recordMap = mapValue.get(myKey);
                    CsvKeyCompare datakey = profileMap.get(myRecord.companyName);
                    if(datakey.corpIdCompCodeCPX != null && datakey.corpIdCompCodeS1.equals(recordMap.corpIdCompCode)){
                        myRecord.corpIdCompCode = datakey.corpIdCompCodeCPX;
                        mapValue.remove(myKey);
                        mapValue.put(myKey,myRecord);
                    }
                }else {
                    mapValue.put(myKey, myRecord);
                }
            }

            count++;
        }

        scanner.close();
    }


    static String Maptotext(LinkedHashMap<MyKey, MyRecord> value){
        int count = 0;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<MyKey, MyRecord> entry : value.entrySet()) {
            MyKey meKey = new MyKey();
            meKey = entry.getKey();
            MyRecord rec = new MyRecord();
            rec = entry.getValue();
            if(count ==0){
                sb.append(rec.runningNo);
            }else{
                sb.append(count);
            }
            sb.append("|");
            sb.append(rec.asOfYearMonth);
            sb.append("|");
            sb.append(rec.seqNo);
            sb.append("|");
            sb.append(rec.companyName);
            sb.append("|");
            sb.append(rec.corpIdCompCode);
            sb.append("|");
            sb.append(rec.valueDate);
            sb.append("|");
            sb.append(rec.accountNo);
            sb.append("|");
            sb.append(rec.accountName);
            sb.append("|");
            sb.append(rec.employeeCardIdOrPassportNo);
            sb.append("|");
            sb.append(rec.birthDate);
            sb.append("|");
            sb.append(rec.sex);
            sb.append("|");
            sb.append(rec.insurerancePlan);
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }

    private static void readXLSXFile() {
        try {

            FileInputStream excelFile = new FileInputStream(new File(srcCsvFile));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        System.out.print(currentCell.getStringCellValue() + "--");
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        System.out.print(currentCell.getNumericCellValue() + "--");
                    }

                }
                System.out.println();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void readCSVFile(){
        try (
                Reader reader = Files.newBufferedReader(Paths.get(srcCsvFile));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        ) {

            for (CSVRecord csvRecord : csvParser) {
                CsvKeyCompare keys = new CsvKeyCompare();
                String companyName = csvRecord.get(0);
                String corpIdCompCodeS1 = csvRecord.get(1);
                String corpIdCompCodeCPX = csvRecord.get(2);
                keys.companyName = companyName;
                keys.corpIdCompCodeS1 = corpIdCompCodeS1;
                keys.corpIdCompCodeCPX = corpIdCompCodeCPX;
                profileMap.put(companyName,keys);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static String readInput() {

        StringBuffer buffer = new StringBuffer();
        try {
            FileInputStream fis = new FileInputStream(srcTextFile);
            InputStreamReader isr = new InputStreamReader(fis, "TIS620");

            Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char) ch);
            }
            in.close();
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    static void writeOutput(String str) {
        try {
            FileOutputStream fos = new FileOutputStream(targetTextFile);
            Writer out = new OutputStreamWriter(fos, "TIS620");
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}