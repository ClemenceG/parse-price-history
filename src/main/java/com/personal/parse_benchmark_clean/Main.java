package com.personal.parse_benchmark_clean;



	import java.io.File;

	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.FileNotFoundException;
	import java.io.IOException;
	import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;

	import org.apache.poi.ss.usermodel.DateUtil;
	import java.util.Date;

	import org.apache.poi.ss.usermodel.*;
	import org.apache.poi.xssf.usermodel.XSSFSheet;
	import org.apache.poi.xssf.usermodel.XSSFWorkbook;

	
	public class Main {
		
		private static String filename = "C:/Users/cgran/OneDrive/Documents/Projects/01-LireCSV/px_BBG000BC9MR3.xlsx";
		private static String[] columns = {"ISIN", "Weight", "Currency", "SuperSector Nb ", "SuperSector Name", "Sector Nb", "Sector Name", "Subsector Nb", "Subsector Name", "Country Code", "Country Name"};
		private static String exportFilename = "C:/Users/cgran/OneDrive/Documents/Projects/01-LireCSV/extra_sec_price_hist.xlsx";
		
		public static void main(String[] args) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Prendre comme example le fichier sur Teams: Product/05-Projects/03-Performance/Portfolio Data/Parsing Template/px_history.xlsx:");
			System.out.println("Entrez path/filename du fichier à extraire :");
			String filename = scanner.nextLine();
			
			System.out.println("Entrez path/filename du fichier de destination :");
			String exportFilename = scanner.nextLine();
			
			//System.out.println("Souhitez-vous extraire les transactions? oui/non");
			//boolean parse_transactions = scanner.nextLine().contentEquals("oui");
			scanner.close();
			File xlsx = new File(filename);
			try(FileInputStream is = new FileInputStream(xlsx);
					XSSFWorkbook wb = new XSSFWorkbook(is);) {
				
				Portfolio portfolio = new Portfolio("extra securities");
				
				XSSFSheet sheet = wb.getSheetAt(0);
				parsePriceHistory(portfolio, sheet);
				
				sheet = wb.getSheetAt(1);
				parseCharacteristicSheet(sheet, portfolio);
				System.out.println(portfolio);
				printExcelFile(exportFilename, portfolio);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
		/**
		 * Method that exports and associates the price history from the second sheet to the securities
		 * @param portfolio
		 * @param sheet
		 * @param nameDivider
		 */
		private static void parsePriceHistory(Portfolio portfolio, XSSFSheet sheet) {
			
			Iterator<Row> rowIterator = sheet.iterator();
			
			int benchmarkIndex = 0;
			
			while(rowIterator.hasNext()) {
				Row currentRow = rowIterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();

				while(cellIterator.hasNext()) {
					Cell currentCell = cellIterator.next();
					
					if(currentCell.getStringCellValue().equals("START SECURITY")) {
						currentCell = cellIterator.next();
						String bbGlobal = currentCell.getStringCellValue();
						Security newSec = new Security();
						newSec.setBBGlobal(bbGlobal);
						PortfolioElements newElement = new PortfolioElements(newSec);
						portfolio.addBenchmarkElement(newElement);
						// skip last cell
						currentCell = cellIterator.next();
						
					}	else if(currentCell.getStringCellValue().equals(portfolio.getSecurityBBGlobal(benchmarkIndex))) {
						currentCell = cellIterator.next();
						Date date = DateUtil.getJavaDate(currentCell.getNumericCellValue());
						currentCell = cellIterator.next();
						double price = currentCell.getNumericCellValue();
						PriceElement priceElement = new PriceElement(date, price);
						portfolio.getSecurity(benchmarkIndex).addPriceElement(priceElement);
					} else {
						benchmarkIndex++;
						break;
					}
				}
				
			}
		}
		
		private static void parseCharacteristicSheet(XSSFSheet sheet, Portfolio portfolio) {
			Iterator<Row> rowIterator = sheet.iterator();
			
			// skip header line
			//Row currentRow = rowIterator.next();

			
			while(rowIterator.hasNext()) {
				
				Row currentRow = rowIterator.next();

				int rowIndex = currentRow.getRowNum();
				Security currentSecurity = portfolio.getSecurity(rowIndex);
				
				Iterator<Cell> cellIterator = currentRow.iterator();
				
				while(cellIterator.hasNext()) {
					Cell currentCell = cellIterator.next();
					int cellIndex = currentCell.getColumnIndex();
					
					switch(cellIndex) {
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						currentSecurity.setCurrency(currentCell.getStringCellValue());
						break;
					case 4:
						Sector sector = new Sector(currentCell.getStringCellValue());
						currentSecurity.setSector(sector);
						break;
					case 5:
						currentSecurity.getSector().addSectorNb((int) currentCell.getNumericCellValue());
						break;
					case 6:
						sector = new Sector(currentCell.getStringCellValue());
						currentSecurity.setSubsector(sector);
						break;
					case 7:
						currentSecurity.getSubsector().addSectorNb((int) currentCell.getNumericCellValue());
						break;
					case 8:
						sector = new Sector(currentCell.getStringCellValue());
						currentSecurity.setSupersector(sector);
						break;
					case 9:
						currentSecurity.getSupersector().addSectorNb((int) currentCell.getNumericCellValue());
						break;

					case 10:
						Country country = new Country(currentCell.getStringCellValue());
						currentSecurity.setCountry(country);
						break;
					case 11:
						currentSecurity.getCountry().setCountryIso(currentCell.getStringCellValue());
						break;
					case 12:
						currentSecurity.setIsin(currentCell.getStringCellValue());
						break;
					case 13:
						currentSecurity.setTicker(currentCell.getStringCellValue());
						break;
						
					}
					
				}
			}
		}
		
		private static void printExcelFile(String filename, Portfolio portfolio) {
			
			try(FileOutputStream fileOut = new FileOutputStream(new File(filename));
					Workbook wb = new XSSFWorkbook();) {

				Sheet sheet = wb.createSheet("PriceHist");
				CreationHelper creationHelper = wb.getCreationHelper();

				// initialize header row
				Row headerRow = sheet.createRow(0);
				int i = 0;
				for(; i < columns.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columns[i]);
				}

				ArrayList<Date> dateHeader = parseDateHeader(portfolio);
				
				
				for(Date date : dateHeader) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(date);
					CellStyle style = wb.createCellStyle();
					style.setDataFormat(creationHelper.createDataFormat().getFormat(
							"dd/mm/yyyy"));
					cell.setCellStyle(style);
					i++;
				}
				
				i = 1;		// row index
				for(PortfolioElements currentElement : portfolio.getBenchmarkElements()) {
					Row currentRow = sheet.createRow(i);
					Security currentSec = currentElement.getSecurity();
					
					currentRow.createCell(0).setCellValue(currentSec.getBloombergGlobal());
					currentRow.createCell(1).setCellValue(currentElement.getWeight());
					currentRow.createCell(2).setCellValue(currentSec.getTicker());
					currentRow.createCell(3).setCellValue(currentSec.getCurrency());
					if(currentSec.getCountry() != null) {
						currentRow.createCell(4).setCellValue(currentSec.getCountry().getCountryIso());
						currentRow.createCell(5).setCellValue(currentSec.getSupersector().getSectorNb());
						currentRow.createCell(6).setCellValue(currentSec.getSupersector().getSectorName());
						currentRow.createCell(7).setCellValue(currentSec.getSector().getSectorNb());
						currentRow.createCell(8).setCellValue(currentSec.getSector().getSectorName());
						currentRow.createCell(9).setCellValue(currentSec.getSubsector().getSectorNb());
						currentRow.createCell(10).setCellValue(currentSec.getSubsector().getSectorName());
					}
					int sheetIndex = 11;
					int securityIndex = 0;
					for(int j=0; securityIndex < currentSec.getPrices().size(); j++) {
						PriceElement element = currentSec.getPrices().get(securityIndex);
						if(element.getDate().equals(dateHeader.get(j))) {
							currentRow.createCell(sheetIndex).setCellValue(element.getPrice());
						} else {
							currentRow.createCell(sheetIndex).setCellValue(0);
							securityIndex--;
						}
						securityIndex++;
						sheetIndex++;
					}
					
					i++;
				}
				
				
		        wb.write(fileOut);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		private static ArrayList<Date> parseDateHeader(Portfolio portfolio) {

			ArrayList<Date> dateHeader = new ArrayList<Date>();
			
			for(PortfolioElements currentElement : portfolio.getBenchmarkElements()) {
				System.out.println(currentElement.getSecurity().getBloombergGlobal());
				int i = 0;
				if(dateHeader.isEmpty()) {
					for(PriceElement currentPriceEl : currentElement.getSecurity().getPrices()) {
						Date date = currentPriceEl.getDate();
						dateHeader.add(date);
					}

				} else {
					for(int j=0; j<currentElement.getSecurity().getPrices().size() && i+1<dateHeader.size();) {
						PriceElement currentPriceEl = currentElement.getSecurity().getPriceElement(j);
						Date date = currentPriceEl.getDate();
						System.out.println(date + "\t" + dateHeader.get(i));
						if(date.compareTo(dateHeader.get(i+1)) < 0 && date.compareTo(dateHeader.get(i)) > 0) {
							dateHeader.add(i+1, date);
							j++;
					    } else if(date.compareTo(dateHeader.get(i)) == 0) {
					    	j++;
					    } else if(date.compareTo(dateHeader.get(i)) < 0) {
					    	dateHeader.add(i, date);
					    	i--;
					    	j++;
					    }
						i++;
					}
				}
			}
			return dateHeader;
		}
		
		/*private static String debugger(Cell cell) {
			String cellValue = null;
			if(cell.getCellType().equals(CellType.NUMERIC)) {
				double d = cell.getNumericCellValue();
				cellValue = Double.toString(d);
			} else if(cell.getCellType().equals(CellType.STRING)) {
				cellValue = cell.getStringCellValue();
			}
			return cellValue;
		}*/
	}


