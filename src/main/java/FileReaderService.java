import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class FileReaderService {


    public static List<Check> readFromExcel(String file) throws IOException {
        List<Check> checks=new ArrayList<Check>();
        XSSFWorkbook myExcelBook = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet myExcelSheet = myExcelBook.getSheet("Sheet1");
        Iterator<Row> iterator=myExcelSheet.iterator();
        Map<String,Integer> products=null;
        while(iterator.hasNext()){
            Row row=iterator.next();
            Check check=new Check();
            try{
            check.setCode(String.valueOf((int)row.getCell(0).getNumericCellValue()));
            }
            catch (IllegalStateException e){
                iterator.next();
                break;
            }
            catch (NullPointerException e){
                return checks;
            }
            Check c;
            if(checks.contains(check)){
                c=checks.get(checks.indexOf(check));
            }else{
                c=check;
                products=new HashMap<String, Integer>();
                c.setProducts(products);
            }

            c.getProducts().put(getStringFromCell(row.getCell(1)),
                   0);
            checks.remove(c);
            checks.add(c);
        }
        myExcelBook.close();
        return checks;

    }
    private static String getStringFromCell(Cell cell){
        if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
           return cell.getStringCellValue();
        }

        if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
           return String.valueOf((int)cell.getNumericCellValue());

        }
        return "GOVNOOO";
    }

    public static void main(String[] args) throws IOException {
        List<Check> checks=readFromExcel("src/main/resources/SmalSet.xlsx");
        List<Check> mainCheck=readFromExcel("src/main/resources/SmalSet.xlsx");
        int fromUser=3;
        for(int i = 1; i<=Constant.numProdInCheck; i++){
            checks=Counter.deleteAllDimension(i,checks,mainCheck);
        }
        System.out.println(checks);
    }

}
