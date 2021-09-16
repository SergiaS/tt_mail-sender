package org.mailsender;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Properties;

public class EmailSender {

    private static final Properties properties = new Properties();
    static {
        try(InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        properties.getProperty("mail.send.login"),
                        properties.getProperty("mail.send.password"));
            }
        });

        // Used to debug SMTP issues
        session.setDebug(true);


        // Recipient's email ID needs to be mentioned.
        String to = properties.getProperty("mail.send.to");

        // Sender's email ID needs to be mentioned
        String from = properties.getProperty("mail.send.from");

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("This is the Subject Line!");

            // Now set the actual message
//            addTextToMessage(message, "This is actual message");
//            addHTMLToMessage(message, "<h1>This is actual message embedded in HTML tags</h1>. Hello <b>Bob</b>!");
            addAttachment(message, "This is actual message", "README.md");

            System.out.println("sending...");
            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

    private static void addTextToMessage(MimeMessage message, String text) throws MessagingException {
        message.setText(text);
    }

    private static void addHTMLToMessage(MimeMessage message, String html) throws MessagingException {
        message.setContent(html, "text/html");
    }

    private static void addAttachment(MimeMessage message, String text, String filePath) throws MessagingException {
        Multipart multipart = new MimeMultipart();
        MimeBodyPart attachmentPart = new MimeBodyPart();
        MimeBodyPart textPart = new MimeBodyPart();

        try {
            File f = new File(filePath);
            attachmentPart.attachFile(f);
            textPart.setText(text);
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }

        message.setContent(multipart);
    }

}
