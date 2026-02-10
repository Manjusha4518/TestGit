package v20.Common;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Common {

    public static Properties prob;
    public final static String COLOR_RESET = "\u001B[0m";
    public final static String GREEN = "\u001B[32m";
    public final static String BLUE = "\u001B[34m";

    public final static String YELLOW = "\u001B[33m";
    public final static String PURPLE = "\u001B[35m";

    public static String getProductdatafile(String prodname) throws Exception {
        String custFilename = "";
        if (prodname.equalsIgnoreCase("HI") || prodname.equalsIgnoreCase("VHI"))
            custFilename = Config.CustomerDatafilePath_HI;
        else if (prodname.equalsIgnoreCase("AC") || prodname.equalsIgnoreCase("VAI"))
            custFilename = Config.CustomerDatafilePath_AC;
        else if (prodname.equalsIgnoreCase("CI") || prodname.equalsIgnoreCase("VCI"))
            custFilename = Config.CustomerDatafilePath_CI;
        else if (prodname.equalsIgnoreCase("LTD_LTD"))
            custFilename = Config.CustomerDatafilePath_LTD_LTD;
        else if (prodname.equalsIgnoreCase("LTD_VPL"))
            custFilename = Config.CustomerDatafilePath_LTD_VPL;
        else if (prodname.equalsIgnoreCase("LTD_ASL"))
            custFilename = Config.CustomerDatafilePath_LTD_ASL;
        else if (prodname.equalsIgnoreCase("STD_VPS"))
            custFilename = Config.CustomerDatafilePath_STD_VPS;
        else if (prodname.equalsIgnoreCase("STD_STD"))
            custFilename = Config.CustomerDatafilePath_STD_STD;
        else if (prodname.equalsIgnoreCase("STD_ASW"))
            custFilename = Config.CustomerDatafilePath_STD_ASW;
        else if (prodname.equalsIgnoreCase("SMP_TDB"))
            custFilename = Config.CustomerDatafilePath_SMP_TDB;
        else if (prodname.equalsIgnoreCase("SMP_TDI"))
            custFilename = Config.CustomerDatafilePath_SMP_TDI;
        else if (prodname.equalsIgnoreCase("SMP_MAL"))
            custFilename = Config.CustomerDatafilePath_SMP_MAL;
        else if (prodname.equalsIgnoreCase("SMP_CTL"))
            custFilename = Config.CustomerDatafilePath_SMP_CTL;
        else if (prodname.equalsIgnoreCase("SMP_ORL"))
            custFilename = Config.CustomerDatafilePath_SMP_ORL;
        else if (prodname.equalsIgnoreCase("SMP_COL"))
            custFilename = Config.CustomerDatafilePath_SMP_COL;
        else if (prodname.equalsIgnoreCase("SMP_CVP"))
            custFilename = Config.CustomerDatafilePath_SMP_CVP;
        else if (prodname.equalsIgnoreCase("SMP_WAV"))
            custFilename = Config.CustomerDatafilePath_SMP_WAV;
        else if (prodname.equalsIgnoreCase("SMP_DBL"))
            custFilename = Config.CustomerDatafilePath_SMP_DBL;
        else if (prodname.equalsIgnoreCase("SMP_MEL"))
            custFilename = Config.CustomerDatafilePath_SMP_MEL;
        else if (prodname.equalsIgnoreCase("Leave_FML"))
            custFilename = Config.CustomerDatafilePath_Leave_FML;
        else if (prodname.equalsIgnoreCase("Leave_LOA"))
            custFilename = Config.CustomerDatafilePath_Leave_LOA;
        else if (prodname.equalsIgnoreCase("Leave_PLA"))
            custFilename = Config.CustomerDatafilePath_Leave_PLA;
        else if (prodname.equalsIgnoreCase("Leave_ADA"))
            custFilename = Config.CustomerDatafilePath_Leave_ADA;
        else if (prodname.equalsIgnoreCase("GTL_VAR"))
            custFilename = Config.CustomerDatafilePath_GTL_VAR;
        else if (prodname.equalsIgnoreCase("GTL_VG"))
            custFilename = Config.CustomerDatafilePath_GTL_VG;
        else if (prodname.equalsIgnoreCase("GTL_GL"))
            custFilename = Config.CustomerDatafilePath_GTL_GL;

        else if (prodname == null)
            throw new Exception(" Product name not specified and " + prodname);
/*        else if (prodname.equalsIgnoreCase("AgencyPACS"))
            custFilename = Config.AgencyPACSDbData;
        else if (prodname.equalsIgnoreCase("VUE"))
            custFilename = Config.VUEDBData;
        else if (prodname.equalsIgnoreCase("APS"))
            custFilename = Config.APSDBData;
        else if (prodname.equalsIgnoreCase("STACS"))
            custFilename = Config.STACSDBData;
        else if (prodname.equalsIgnoreCase("STACSINT"))
            custFilename = Config.STACSDBData;*/
        System.out.println("custFilename-->"+custFilename);
        System.out.println("File name: " + custFilename);
        return custFilename;
    }

    public static String readProductPropertyFile(String prodctname, String Key) throws Exception {

        String filename = Common.getProductdatafile(prodctname);
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        prob = new Properties();

        InputStream input = new FileInputStream(filename);
        prob.load(input);
        String data = prob.getProperty(Key);
        input.close();
        return data;
    }

    public static String convertKeyWithPrefix(String key, String appendPrefixString) {
        if (appendPrefixString == null)
            appendPrefixString = "";
        return appendPrefixString.trim().equalsIgnoreCase("") ? key.trim() : appendPrefixString.trim() + "-" + key.trim();
    }

    public static String createOutputMigrationExcelWithResult(String pacsID, String excelTemplatePath, String targetFilePath, String sheetName,ArrayList<String[]> resultData) throws IOException {
        String err = "";
        try {
            base.Common.closeExcel();
            base.Common.deleteFile(targetFilePath);
            File tDir = new File(Config.migrationOutputDirectory);
            if (!tDir.exists())
                (new File(Config.migrationOutputDirectory)).mkdir();
            if (!(new File(targetFilePath)).exists()) {
                File tFile = new File(targetFilePath);
            }
            base.Common.copyFile(excelTemplatePath, targetFilePath);
            FileInputStream fileinstream = new FileInputStream(targetFilePath);
            XSSFWorkbook workbook = new XSSFWorkbook(fileinstream);

            // spreadsheet object
            XSSFSheet spreadsheet = workbook.getSheet(sheetName);
            int tRows = spreadsheet.getPhysicalNumberOfRows();
            spreadsheet.getRow(0).getCell(3).setCellValue(pacsID);
            //spreadsheet.getRow(0).getCell(4).setCellValue(pacsID);
            int autofiledrow = 0;
            //boolean dmatch=false;


            for (String[] data : resultData) {
                boolean dmatch = false;
                for (int r = 2; tRows > r; r++) {
                    String d = spreadsheet.getRow(r).getCell(2).getStringCellValue();
                    if (d.trim().equalsIgnoreCase(data[0].trim())) {
                        autofiledrow = r;
                        dmatch = true;
                        break;
                    }
                }
                if (dmatch) {
                    spreadsheet.getRow(autofiledrow).getCell(3).setCellValue(data[1]);
                    spreadsheet.getRow(autofiledrow).getCell(4).setCellValue(data[2]);
                    spreadsheet.getRow(autofiledrow).getCell(5).setCellValue(data[3]);
                } else {
                    err = err + "[PACSID: "+pacsID+"] This Field [" + data[0]+"] NOT available in the migration excel Unable to write comparison data --> ["+data[1]+"]"+" ["+data[2]+ "]"+" --> Result ["+data[3]+ "]"+"\n";
                    System.out.println("[PACSID: "+pacsID+"] Field Name NOT available in the migration excel --> " + data[0] +" Unable to write ["+data[1]+"]"+" ["+data[2]+ "]"+" ["+data[3]+ "]");
                }
            }
            fileinstream.close();
            FileOutputStream outFile = new FileOutputStream(targetFilePath);
            workbook.write(outFile);
            outFile.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        return err;

    }

    public static void createOutputMigrationLogs(String pacsid, String logFilepath, String logString) {
        try {
            if (!(new File(logFilepath)).exists())
                new File(logFilepath);

            String dt = (new Date()).toString();
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFilepath, true));

            writer.append("[" + dt + "]\n");
            writer.append("--------- "+pacsid+"--------------------\n");
            writer.append(logString);
            writer.append("----------------------------------------------------");

            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public static void readResultArrayListAndConvertToDataCSV(List<String[]> allData,String csvOutFile,String product) throws IOException {


        List<String[]> outData = new ArrayList<>();
        try {
            String[] temp;
            int i=0;
            outData.add(new String[]{"Created Time","Product","TC Name","TC Run Status","QuoteOrPolicy Status","QuoteOrPolicy ID"});
            for (String[] row : allData) {
                temp=new String[6];

                String detailnospace=row[8].toUpperCase().trim().replaceAll(" ","").replaceAll("_","").trim();
                temp[0]=row[0];
                temp[1]=product;
                temp[2]=row[5];
                temp[3]=row[7].trim();
                if(detailnospace.contains("POLICYSTATUS:")){
                    temp[4]=detailnospace.split("POLICYSTATUS:\\[")[1].trim().split("\\]")[0].trim();
                }else if(detailnospace.contains("QUOTESTATUS:")){
                    temp[4]=detailnospace.split("QUOTESTATUS:\\[")[1].trim().split("\\]")[0].trim();
                }else{
                    temp[4]="";
                }
                if(detailnospace.contains("POLICYID:")){
                    temp[5]=detailnospace.split("POLICYID:\\[")[1].trim().split("\\]")[0].trim();
                }else if(detailnospace.contains("QUOTEID:")){
                    temp[5]=detailnospace.split("QUOTEID:\\[")[1].trim().split("\\]")[0].trim();
                }else{
                    temp[5]="";
                }
                outData.add(temp);

               /* if(row[7].trim().equalsIgnoreCase("PASS")){
                    if(scenarioNamenospace.toUpperCase().contains("POLICYCREATION")){
                        temp[0]=row[0];
                        temp[1]=product;
                        temp[2]=row[5];
                        temp[3]=row[7].trim();
                        temp[4]=row[8].toUpperCase().trim().replaceAll(" ","").trim().split("POLICYSTATUS:\\[")[1].trim().split("\\]")[0].trim();;
                        temp[5]=row[8].toUpperCase().trim().replaceAll(" ","").trim().split("POLICYID:\\[")[1].trim().split("\\]")[0].trim();
                        outData.add(temp);
                    }else if(scenarioNamenospace.toUpperCase().contains("QUOTECREATION")){
                        temp[0]=row[0];
                        temp[1]=product;
                        temp[2]=row[5];
                        temp[3]=row[7].trim();
                        temp[4]=row[8].toUpperCase().trim().replaceAll(" ","").trim().split("QUOTESTATUS:\\[")[1].trim().split("\\]")[0].trim();;
                        temp[5]=row[8].toUpperCase().trim().replaceAll(" ","").trim().split("QUOTEID:\\[")[1].trim().split("\\]")[0].trim();
                        outData.add(temp);
                    }

                }

                if((scenarioNamenospace.toUpperCase().contains("QUOTECREATION")||scenarioNamenospace.toUpperCase().contains("POLICYCREATION"))&&!row[7].trim().equalsIgnoreCase("PASS")) {
                    temp[0]=row[0];
                    temp[1]=product;
                    temp[2]=row[5];
                    temp[3]=row[7].trim();
                    temp[4]="";
                    temp[5]="";
                    outData.add(temp);
                }*/

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (outData != null) {
            CSVWriter writer;
            writer = new CSVWriter(new FileWriter(csvOutFile, false));
            writer.writeAll(outData);
            /*for (String[] a : outData) {
                writer.writeNext(a);
            }*/
            writer.close();
            System.out.println(GREEN + "Data written in the csv (file created if not exist): " + csvOutFile + " !!" + COLOR_RESET);
        } else
            System.out.println(YELLOW + "Data NOT written to file " + csvOutFile + " !!" + COLOR_RESET);
    }

    public static void readResultCSVAndConvertToDataCSV(String csvInputfile,String csvOutFile,String product) throws IOException {


        List<String[]> outData = new ArrayList<>();
        try {
            // Create an object of file reader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(csvInputfile);

            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();

            String[] temp;
            int i=0;
            outData.add(new String[]{"Created Time","Product","TC Name","Quote ID"});
            for (String[] row : allData) {
                temp=new String[4];
                if(row[6].contains("QUOTECREATION")&&row[7].trim().equalsIgnoreCase("PASS")) {
                    temp[0]=row[0];
                    temp[1]=product;
                    temp[2]=row[5];
                    temp[3]=row[8].trim().replaceAll(" ","").trim().split("QuoteID:\\[")[1].trim().split("\\]")[0].trim();
                    outData.add(temp);
                }
                if(row[6].contains("QUOTECREATION")&&!row[7].trim().equalsIgnoreCase("PASS")) {
                    temp[0]=row[0];
                    temp[1]=product;
                    temp[2]=row[5];
                    temp[3]="";
                    outData.add(temp);
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (outData != null) {
            CSVWriter writer = null;
            writer = new CSVWriter(new FileWriter(csvOutFile, false));
            writer.writeAll(outData);
            /*for (String[] a : outData) {
                writer.writeNext(a);
            }*/
            writer.close();
            System.out.println(GREEN + "Data written in the csv (file created if not exist): " + csvOutFile + " !!" + COLOR_RESET);
        } else
            System.out.println(YELLOW + "Data NOT written to file " + csvOutFile + " !!" + COLOR_RESET);
    }


}
