package xbrl.script;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainWindowController {

    private MainApp mainApp;

    public ListView listViewFilesList;
    public ListView listViewFilesWithCondition;

    public TextField textFieldEntryPointName;
    public TextField textField_finalDirectoryPath;

    private String finalDirectoryPath;

    private List<String> files_paths;
    private List<String> filesWithConditions;


    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;

        files_paths = new ArrayList<String>();
        filesWithConditions = new ArrayList<String>();
    }

    public void buttonPressed_ChoseFiles(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();

        //Задаём фильтры расширений
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xlsx)", "*.xlsx", "*.csv", "*.xls");
        fileChooser.getExtensionFilters().add(extensionFilter);

        //Показываем диалог зазгрузки файла
        //File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        List<File> files = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());
        files_paths.clear();

        for (int i = 0; i < files.size(); i++) {
            files_paths.add(files.get(i).getPath());
        }

        listViewFilesList.setItems(FXCollections.observableArrayList(files_paths));

    }

    public void buttonPressed_StartScript(ActionEvent actionEvent) {

        for (int i = 0; i < files_paths.size(); i++) {
            System.out.println("Opening file: " + files_paths.get(i));
            changeEntryPoint(files_paths.get(i));
        }
    }

    public void changeEntryPoint(String filePath){
        try {
            InputStream inputFileStream = new FileInputStream(filePath);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputFileStream);
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(4);

            for(Row r : xssfSheet) {
                if(r.getRowNum() != 0){
                    XSSFCell conditionCell = (XSSFCell) r.getCell(0);
                    XSSFCell changingCell = (XSSFCell) r.getCell(5);
                    /*if(conditionCell != null) {
                            changingCell.setCellValue(textFieldEntryPointName.getText());
                    }*/
                    if(!isCellEmpty(conditionCell)){
                        changingCell.setCellValue(textFieldEntryPointName.getText());
                    }
                }
            }

            inputFileStream.close();

            File file = new File(filePath);
            String fileName = file.getName();

            FileOutputStream outputStream = new FileOutputStream(finalDirectoryPath + "/" + fileName);

            xssfWorkbook.write(outputStream);
            xssfWorkbook.close();

            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void buttonPressed_selectDirectoryForFiles(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        File selectedDirectory = chooser.showDialog(mainApp.getPrimaryStage());
        finalDirectoryPath = selectedDirectory.getPath();
        textField_finalDirectoryPath.setText(finalDirectoryPath);
    }

    public void buttonPressed_FindSomething(ActionEvent actionEvent) {
        filesWithConditions.clear();
        for (int i = 0; i < files_paths.size(); i++) {
            System.out.println("Opening file: " + files_paths.get(i));
            findCondition(files_paths.get(i));
        }
        listViewFilesWithCondition.setItems(FXCollections.observableArrayList(filesWithConditions));
    }

    private void findCondition(String filePath) {
        try {
            InputStream inputFileStream = new FileInputStream(filePath);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputFileStream);
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(2);

            boolean contidion = false;

            for(Row r : xssfSheet) {
                if(r.getRowNum() != 0){
                    XSSFCell conditionCell = (XSSFCell) r.getCell(2);
                    if(conditionCell != null) {
                        if(conditionCell.getNumericCellValue() == 1){
                            contidion = true;
                        }
                    }
                }
            }

            inputFileStream.close();

            if(contidion){
                File file = new File(filePath);
                String fileName = file.getName();
                filesWithConditions.add(fileName);
            }

            xssfWorkbook.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCellEmpty(final XSSFCell cell) {
        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            return true;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
            return true;
        }

        return false;
    }

    public void buttonPressed_ChangeLinkrole(ActionEvent actionEvent) {
        for (int i = 0; i < files_paths.size(); i++) {
            System.out.println("Opening file: " + files_paths.get(i));
            changeLinkrole(files_paths.get(i));
        }
    }

    public void changeLinkrole(String filePath){
        try {
            InputStream inputFileStream = new FileInputStream(filePath);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputFileStream);

            for (int i = 0; i < xssfWorkbook.getNumberOfSheets(); i++) {
                XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(i);

                for(Row r : xssfSheet) {
                    for(Cell c : r){
                        if(c.getCellType() == Cell.CELL_TYPE_STRING){
                            String cellString = c.getStringCellValue();
                            if(cellString.contains("rep/2017-11-30")){
                                String finalString = cellString.replace("rep/2017-11-30", "rep/2018-02-28");
                                c.setCellValue(finalString);
                            }
                        }
                    }
                }
            }


            inputFileStream.close();

            File file = new File(filePath);
            String fileName = file.getName();

            FileOutputStream outputStream = new FileOutputStream(finalDirectoryPath + "/" + fileName);

            xssfWorkbook.write(outputStream);
            xssfWorkbook.close();

            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
