package com.djimgou.reporting;

import com.djimgou.core.exception.AppException;
import com.djimgou.filestorage.service.FichierService;
import com.djimgou.filestorage.service.FileStorage;
import lombok.Getter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;

/**
 * https://www.baeldung.com/spring-jasper
 */
@Service
public class ReportBuilder {

    DataSource dataSource;

    public static String REPORT_DIR = "./Reports";

    @Getter
    private final FileStorage fs;

    @Autowired
    public ReportBuilder(FichierService fichierService, DataSource dataSource) {
        this.dataSource = dataSource;
        this.fs = fichierService.getFsFactory().getInstance();
    }

    /**
     * @param jasperFileName
     * @param parameters
     * @param reportDir
     */
    public Resource toHTML(String jasperFileName, String reportDir, Map<String, Object> parameters) {
     /*   Map<String, Object> pp = new HashMap<>();
        parameters.put("title", "Employee Report");
        parameters.put("minSalary", 15000.0);
        parameters.put("condition", " LAST_NAME ='Smith' ORDER BY FIRST_NAME");*/

        String store = fs.storePath(has(reportDir) ? reportDir : REPORT_DIR);
        String fileOutName = "report-" + UUID.randomUUID().toString() + ".html";
        try {
            Resource r = fs.loadFileAsResource(store, jasperFileName);

            JasperPrint jasperPrint = JasperFillManager.fillReport(r.getInputStream(), parameters, dataSource.getConnection());

            Path p = Paths.get(store)
                    .toAbsolutePath().normalize().resolve(fileOutName).normalize();
            String url = p.toAbsolutePath().normalize().toString();

            JasperExportManager.exportReportToHtmlFile(jasperPrint, url);


            Resource res = fs.loadFileAsResource(store, fileOutName);

            /*FileInputStream fis = new FileInputStream("src/test/resources/fileTest.txt");
            String data = IOUtils.toString(fis, "UTF-8");*/
            return res;
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (AppException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Resource toPDF(String jasperFileName, String reportDir, Map<String, Object> parameters) {
        String store = fs.storePath(has(reportDir) ? reportDir : REPORT_DIR);
        String fileOutName = "report-pdf-" + UUID.randomUUID().toString() + ".pdf";
        try {
            Resource r = fs.loadFileAsResource(store, jasperFileName);

            JasperPrint jasperPrint = JasperFillManager.fillReport(r.getInputStream(), parameters, dataSource.getConnection());

            JRPdfExporter exporter = new JRPdfExporter();

            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

            Path p = Paths.get(store)
                    .toAbsolutePath().normalize().resolve(fileOutName).normalize();
            String url = p.toAbsolutePath().normalize().toString();

            exporter.setExporterOutput(
                    new SimpleOutputStreamExporterOutput(url));

            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Dany Djimgou");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");

            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);

            exporter.exportReport();

            Resource res = fs.loadFileAsResource(store, fileOutName);
            return res;
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (AppException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource toXSX(String jasperFileName, String reportDir, Map<String, Object> parameters) {
        String store = fs.storePath(has(reportDir) ? reportDir : REPORT_DIR);
        String fileOutName = "report-xlsx-" + UUID.randomUUID().toString() + ".xlsx";
        try {
            Resource r = fs.loadFileAsResource(store, jasperFileName);
            JasperPrint jasperPrint = JasperFillManager.fillReport(r.getInputStream(), parameters, dataSource.getConnection());

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
/*
            SimpleXlsxReportConfiguration reportConfig
                    = new SimpleXlsxReportConfiguration();
            reportConfig.setSheetNames(new String[] { "Employee Data" });

            exporter.setConfiguration(reportConfig);*/

            Path p = Paths.get(store)
                    .toAbsolutePath().normalize().resolve(fileOutName).normalize();
            String url = p.toAbsolutePath().normalize().toString();

            exporter.setExporterOutput(
                    new SimpleOutputStreamExporterOutput(url));

            exporter.exportReport();
            Resource res = fs.loadFileAsResource(store, fileOutName);
            return res;
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (AppException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Resource toDocx(String jasperFileName, String reportDir, Map<String, Object> parameters) {
        String store = fs.storePath(has(reportDir) ? reportDir : REPORT_DIR);
        String fileOutName = "report-doc-" + UUID.randomUUID().toString() + ".docx";
        try {
            Resource r = fs.loadFileAsResource(store, jasperFileName);
            JasperPrint jasperPrint = JasperFillManager.fillReport(r.getInputStream(), parameters, dataSource.getConnection());

            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));


/*
            SimpleXlsxReportConfiguration reportConfig
                    = new SimpleXlsxReportConfiguration();
            reportConfig.setSheetNames(new String[] { "Employee Data" });

            exporter.setConfiguration(reportConfig);*/

            Path p = Paths.get(store)
                    .toAbsolutePath().normalize().resolve(fileOutName).normalize();
            String url = p.toAbsolutePath().normalize().toString();

            exporter.setExporterOutput(
                    new SimpleOutputStreamExporterOutput(url));

            exporter.exportReport();
            Resource res = fs.loadFileAsResource(store, fileOutName);
            return res;
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (AppException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        }
        return null;
    }

}
