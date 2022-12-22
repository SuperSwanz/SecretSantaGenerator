package com.superswanz;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static Set<String> names = new HashSet<>();
    static Map<String, String> namemailMapping = new HashMap<>();
    static List<String> namesShuffled = null;

    static {
        try {
            Set<String> members = readFile("src/main/resources/members_email.txt");
            for (String m : members) {
                String[] val = m.split(",");
                namemailMapping.put(val[0].trim(), val[1].trim());
                names.add(val[0].trim());
            }
            namesShuffled = new ArrayList<>(names);
            //copy original arraylist to new shuffled arraylist
            Collections.copy(namesShuffled, names.stream().toList());
            //shuffle arraylist
            Collections.shuffle(namesShuffled);
        } catch (IOException ix) {
            throw new RuntimeException(ix);
        }
    }

    public static void main(String[] args) throws MessagingException {
        Set<String> dupes = new HashSet<>();
        for (String name : namesShuffled) {
            String _name = getName(name);
            names.remove(_name);
            String email = namemailMapping.get(name);
            //sendMail(_name, email, "");
            System.out.println(name + " is going to give to " + _name);
        }
    }

    static String getName(String name) {
        System.out.println(name);
        return names.stream().filter(x -> !x.equalsIgnoreCase(name)).findAny().get();
    }

    static Set<String> readFile(String filePath) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            return stream.collect(Collectors.toSet());
        } catch (IOException e) {
            throw e;
        }
    }

    static String read(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    static void sendMail(String name, String to, String href) throws MessagingException {
        String from = "SENDER EMAIL ID";
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "ADD PASSWORD OR AUTH CODE HERE");
            }
        });
        //session.setDebug(true);
        try {
            String text = read("src/main/resources/index.html");
            text = text.replace("{{name}}", name);
            text = text.replace("{{href}}", href);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("You gotta a Santee!");
            message.setContent(text, "text/html; charset=utf-8");
            System.out.println("sending...");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mx) {
            throw mx;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
