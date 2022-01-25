package com.certibliz.blizcom.controller;

import com.certibliz.blizcom.model.ResponseServer;
import com.certibliz.blizcom.service.ComunicacionOseService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.applet.Main;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

@RestController
@RequestMapping("/api")
public class SendOseController {
    @Autowired
    ComunicacionOseService comunicacionOseService;

    @Value("${server.port}")
    private Integer puerto;

    @Autowired
    ResourceLoader resourceLoader;

    @Value("S{pdfencode}")
    private String pdfenc;

    @Value("classpath:resumen.txt")
    Resource resourceFile;

    @PostMapping("/sendose")
    public ResponseServer sendose(
            @RequestBody String formatSoap,
            @RequestParam(name = "endpoint", required = true) String endpoint,
            @RequestParam(name = "tagOperacionOK", required = true) String tagOperacionOK
    ) throws IOException {
        System.out.println(formatSoap);
        System.out.println(endpoint);
        System.out.println(tagOperacionOK);
        return comunicacionOseService.sendOse(formatSoap,endpoint,tagOperacionOK) ;
    }
    @PostMapping("/sendguiaose")
    public ResponseServer sendguiaose(
            @RequestBody String formatSoap,
            @RequestParam(name = "endpoint", required = true) String endpoint,
            @RequestParam(name = "tagOperacionOK", required = true) String tagOperacionOK
    ) throws IOException {
        System.out.println(formatSoap);
        System.out.println(endpoint);
        System.out.println(tagOperacionOK);
        return comunicacionOseService.sendGuiaOse(formatSoap,endpoint,tagOperacionOK) ;
    }
    @GetMapping("/sendosetest")
    public ResponseEntity<?> sendosetest( ) throws IOException {
        //InputStreamResource resource = comunicacionOseService.getimage();
        File file = ResourceUtils.getFile("classpath:dev.jpg");

        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource res = new ByteArrayResource(Files.readAllBytes(path));
        return ResponseEntity.ok().contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(res);
    }
    @GetMapping("/sendemail")
    public String sendemail( ) throws IOException, MessagingException {
        System.out.println("TEST SEND MAIL "+puerto);

        return "Email sent successfully";
    }
    @PostMapping("/savefile")
    public String savefile( @RequestParam("file") MultipartFile file ) throws IOException, MessagingException {

        savefield(file);
        return "Email sent successfully";
    }
    private final Path root = Paths.get("D:\\PROYECTOS");
    private void savefield(MultipartFile file) {

        try {
            Files.copy(file.getInputStream(),this.root.resolve(file.getOriginalFilename()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendmail() throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("leyterpalacios@gmail.com", "hdfeeywnjddbrqvq");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("leyterpalacios@gmail.com", false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("lpalacios@tns.com.pe"));
        msg.setSubject("Tutorials point email");
        msg.setContent("Tutorials point email", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();
        InputStream stream = SendOseController.class.getResourceAsStream("/resumen.txt");

        InputStream inputStream = SendOseController.class.getClassLoader().getResourceAsStream("/HojaResumen.pdf");

        File targetFile = new File("/HojaResumen.pdf");

        java.nio.file.Files.copy(
                inputStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        IOUtils.closeQuietly(inputStream);
        attachPart.attachFile(targetFile);
        multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);
    }
}
