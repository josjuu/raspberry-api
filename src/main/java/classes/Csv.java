package classes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class Csv {
    //Laptop 
//    private final String FILE_PATH = ".\\src\\statueses.csv";
//    private final String FILE_PATH = "D:\\Users\\josmu\\Documents\\statueses.csv";
    //Raspberry Pi
    private final String FILE_PATH = "/home/pi/Documents/statueses.csv";

    public List<Statues> statueses;

    /**
     * Creates an instance of the class and reads the document.
     */
    public Csv() {
        statueses = new ArrayList<>();

        try {
            this.read();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * Reads the document and puts it in de statueses.
     *
     * @throws IOException
     */
    private void read() throws IOException {
        statueses = new ArrayList<>();
        try (
                Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader("id", "doorOpen", "timeStamp")
                        .withIgnoreHeaderCase()
                        .withFirstRecordAsHeader()
                        .withTrim());) {
            for (CSVRecord csvRecord : csvParser) {
                Statues statues = new Statues(Integer.valueOf(csvRecord.get("id")), Boolean.valueOf(csvRecord.get("doorOpen")), csvRecord.get("timeStamp"));
                this.statueses.add(statues);
            }
        }
    }

    /**
     * Reads the document and returns it.
     *
     * @return Returns the list of that is in the document.
     * @throws IOException
     */
    private List<Statues> readWithReturn() throws IOException {
        ArrayList<Statues> list = new ArrayList<>();

        try (
                Reader reader = Files.newBufferedReader(Paths.get(FILE_PATH));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader("id", "doorOpen", "timeStamp")
                        .withIgnoreHeaderCase()
                        .withFirstRecordAsHeader()
                        .withTrim());) {
            for (CSVRecord csvRecord : csvParser) {
                Statues statues = new Statues(Integer.valueOf(csvRecord.get("id")), Boolean.valueOf(csvRecord.get("doorOpen")), csvRecord.get("timeStamp"));
                list.add(statues);
            }
        }

        return list;
    }

    /**
     * Writes the current statueses to the document.
     *
     * @throws IOException
     * @throws Exception
     */
    private void write() throws IOException, Exception {
        List<Statues> currentList = readWithReturn();
        if (statueses.isEmpty()) {
            throw new Exception("List is empty.");
        }

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("id", "doorOpen", "timeStamp"));) {

            for (Statues statues : statueses) {
                csvPrinter.printRecord(statues.id, statues.doorOpen, statues.timeStamp);
            }

            csvPrinter.flush();
        }
    }

    /**
     * Get the last id in the list. If the list is not set, then it will read it
     * first.
     *
     * @return Returns the id of the last record in the list.
     */
    public int getLastId() {
        if (statueses.isEmpty()) {
            try {
                this.read();
            } catch (IOException ex) {
                System.out.println("Failed to read the csv file");
            }
        }

        Statues lastStatues = statueses.get(statueses.size() - 1);
        return lastStatues.id;
    }

    /**
     * Adds a single record to the list. Then it will write it to the document.
     *
     * @param doorOpen  The statue of the door.
     * @param timeStamp The time to save. Formatted in yyyyMMddHHmmss
     */
    public void addNewRecord(boolean doorOpen, String timeStamp) {
        int newId = getLastId() + 1;
        Statues newStatues = new Statues(newId, doorOpen, timeStamp);

        this.addNewRecord(newStatues);
    }

    /**
     * Adds a single record to the list. Then it will write it to the document.
     *
     * @param newStatues The object to save to the document.
     */
    public void addNewRecord(Statues newStatues) {
        if (newStatues == null || newStatues.timeStamp.isEmpty()) {
            return;
        }

        if (newStatues.id != getLastId() + 1) {
            newStatues.id = getLastId() + 1;
        }

        this.statueses.add(newStatues);
        try {
            this.write();
            this.read();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * Adds multiple records to the list.
     *
     * @param newStatueses A list of records to be added.
     */
    public void addNewRecords(List<Statues> newStatueses) {
        if (newStatueses.isEmpty()) {
            return;
        }

        for (Statues statues : newStatueses) {
            addNewRecord(statues);
        }
    }
}
